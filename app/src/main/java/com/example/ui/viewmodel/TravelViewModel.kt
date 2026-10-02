package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.PostEntity
import com.example.data.local.TripNoteEntity
import com.example.data.local.UserProfileEntity
import com.example.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen {
    POSTS,
    WEB_VIEW,
    ROUTES,
    JOURNAL,
    PROFILE,
    ABOUT,
    DETAIL
}

enum class WebSiteType(val title: String, val shortName: String, val url: String) {
    WORDPRESS("WordPress Gezi Blogu", "WordPress", "https://guvengeziyor1.wordpress.com/"),
    BLOGSPOT("Blogspot Gezi Blogu", "Blogspot", "https://guvengeziyor.blogspot.com/?m=1")
}

class TravelViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val repository: TravelRepository
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _currentScreen = MutableStateFlow(Screen.POSTS)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedPostId = MutableStateFlow<Int?>(null)
    val selectedPostId: StateFlow<Int?> = _selectedPostId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tümü")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>("Canlı WordPress senkronizasyonu hazır")
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _textZoomScale = MutableStateFlow(1.0f)
    val textZoomScale: StateFlow<Float> = _textZoomScale.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // In-app WebView state
    private val _selectedWebSite = MutableStateFlow(WebSiteType.WORDPRESS)
    val selectedWebSite: StateFlow<WebSiteType> = _selectedWebSite.asStateFlow()

    private val _currentWebUrl = MutableStateFlow("https://guvengeziyor1.wordpress.com/")
    val currentWebUrl: StateFlow<String> = _currentWebUrl.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TravelRepository(database)

        tts = TextToSpeech(application, this)

        viewModelScope.launch {
            repository.initializeDatabase()
            refreshPosts()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("tr", "TR"))
            isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }
                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }
                @Suppress("DEPRECATION")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
        }
    }

    val tripNotes: StateFlow<List<TripNoteEntity>> = repository.tripNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklistItems: StateFlow<List<ChecklistItemEntity>> = repository.checklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val expenses: StateFlow<List<com.example.data.local.ExpenseEntity>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpenseAmount: StateFlow<Double?> = repository.totalExpenseAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val allPosts: StateFlow<List<PostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedPosts: StateFlow<List<PostEntity>> = repository.bookmarkedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPosts: StateFlow<List<PostEntity>> = combine(
        allPosts,
        _searchQuery,
        _selectedCategory
    ) { posts, query, category ->
        posts.filter { post ->
            val matchesQuery = query.isBlank() ||
                    post.title.contains(query, ignoreCase = true) ||
                    post.plainText.contains(query, ignoreCase = true) ||
                    post.destinationCity.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                "Tümü" -> true
                "Favorilerim" -> post.isBookmarked
                "Tren Seyahati" -> post.title.contains("YHT", ignoreCase = true) || post.title.contains("Tren", ignoreCase = true)
                "Marmara & Ege" -> post.destinationCity.contains("Çanakkale") || post.destinationCity.contains("Bursa") || post.destinationCity.contains("İstanbul")
                else -> post.category.equals(category, ignoreCase = true)
            }

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPost: StateFlow<PostEntity?> = combine(
        allPosts,
        _selectedPostId
    ) { posts, id ->
        id?.let { pid -> posts.find { it.id == pid } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun navigateTo(screen: Screen) {
        if (screen != Screen.DETAIL) {
            stopAudio()
        }
        _currentScreen.value = screen
    }

    fun openPostDetail(postId: Int) {
        _selectedPostId.value = postId
        _currentScreen.value = Screen.DETAIL
    }

    fun openInAppWeb(url: String, type: WebSiteType = WebSiteType.WORDPRESS) {
        _selectedWebSite.value = type
        _currentWebUrl.value = url
        _currentScreen.value = Screen.WEB_VIEW
    }

    fun selectWebSite(type: WebSiteType) {
        _selectedWebSite.value = type
        _currentWebUrl.value = type.url
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleBookmark(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(post.id, post.isBookmarked)
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _statusMessage.value = "WordPress sitesi kontrol ediliyor..."
            val result = repository.refreshPostsFromNetwork()
            _isRefreshing.value = false
            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                _statusMessage.value = "guvengeziyor1.wordpress.com senkronize ($count yazı)"
            } else {
                _statusMessage.value = "Çevrimdışı mod: Kayıtlı yazılar hazır"
            }
        }
    }

    fun increaseTextZoom() {
        if (_textZoomScale.value < 1.4f) {
            _textZoomScale.value += 0.1f
        }
    }

    fun decreaseTextZoom() {
        if (_textZoomScale.value > 0.85f) {
            _textZoomScale.value -= 0.1f
        }
    }

    fun playAudio(text: String) {
        if (tts != null && isTtsReady) {
            if (_isSpeaking.value) {
                stopAudio()
            } else {
                val cleanText = text.take(3000)
                tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "GuvenReader")
                _isSpeaking.value = true
            }
        }
    }

    fun stopAudio() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun addTripNote(title: String, destination: String, date: String, notes: String, rating: Int) {
        viewModelScope.launch {
            repository.addTripNote(title, destination, date, notes, rating)
        }
    }

    fun deleteTripNote(id: Long) {
        viewModelScope.launch {
            repository.deleteTripNote(id)
        }
    }

    fun toggleChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            repository.toggleChecklistItem(item)
        }
    }

    fun addChecklistItem(title: String, category: String) {
        viewModelScope.launch {
            repository.addChecklistItem(title, category)
        }
    }

    fun deleteChecklistItem(id: Long) {
        viewModelScope.launch {
            repository.deleteChecklistItem(id)
        }
    }

    fun registerOrUpdateUser(name: String, email: String, city: String, bio: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            val profile = UserProfileEntity(
                id = 1,
                fullName = name.trim(),
                email = email.trim(),
                homeCity = if (city.isNotBlank()) city.trim() else "Türkiye",
                bio = if (bio.isNotBlank()) bio.trim() else "Seyahat etmeyi, tren yolculuklarını ve yeni yerler keşfetmeyi seven gezgin.",
                memberLevel = "Gezgin Kaşif",
                joinedDate = dateStr,
                isLoggedIn = true
            )
            repository.saveUserProfile(profile)
        }
    }

    fun quickDemoLogin() {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            val profile = UserProfileEntity(
                id = 1,
                fullName = "Erdal Güven",
                email = "erdalguven53@gmail.com",
                homeCity = "Rize / Ankara",
                bio = "Güven Geziyor blog yazarı & gezgin seyyah. Tren rotaları, Karadeniz ve tarihi yarımada tutkunu.",
                memberLevel = "Usta Gezgin & Blog Yazarı",
                joinedDate = dateStr,
                isLoggedIn = true
            )
            repository.saveUserProfile(profile)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun addExpense(title: String, amount: Double, category: String, destination: String, date: String) {
        viewModelScope.launch {
            repository.addExpense(title, amount, category, destination, date)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
