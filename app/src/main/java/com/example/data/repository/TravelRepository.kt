package com.example.data.repository

import com.example.data.api.WordPressApiService
import com.example.data.local.AppDatabase
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.PostEntity
import com.example.data.local.TripNoteEntity
import com.example.data.model.HtmlUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TravelRepository(
    private val database: AppDatabase,
    private val apiService: WordPressApiService = WordPressApiService.create()
) {
    private val postDao = database.postDao()
    private val tripNoteDao = database.tripNoteDao()
    private val checklistDao = database.checklistDao()
    private val userDao = database.userDao()
    private val expenseDao = database.expenseDao()

    val allPosts: Flow<List<PostEntity>> = postDao.getAllPosts()
    val bookmarkedPosts: Flow<List<PostEntity>> = postDao.getBookmarkedPosts()
    val tripNotes: Flow<List<TripNoteEntity>> = tripNoteDao.getAllNotes()
    val checklistItems: Flow<List<ChecklistItemEntity>> = checklistDao.getAllItems()
    val userProfile: Flow<com.example.data.local.UserProfileEntity?> = userDao.getUserProfile()
    val expenses: Flow<List<com.example.data.local.ExpenseEntity>> = expenseDao.getAllExpenses()
    val totalExpenseAmount: Flow<Double?> = expenseDao.getTotalExpenseAmount()

    fun getPostById(id: Int): Flow<PostEntity?> = postDao.getPostById(id)

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        // Seed default checklist if empty
        if (checklistDao.getCount() == 0) {
            val defaultItems = listOf(
                ChecklistItemEntity(title = "Nüfus Cüzdanı / Pasaport / Ehliyet", category = "Evraklar"),
                ChecklistItemEntity(title = "Uçak / Hızlı Tren (YHT) Biletleri", category = "Evraklar"),
                ChecklistItemEntity(title = "Telefon Şarj Aleti & Powerbank", category = "Elektronik"),
                ChecklistItemEntity(title = "Fotoğraf Makinesi & Yedek Hafıza Kartı", category = "Elektronik"),
                ChecklistItemEntity(title = "Kulaklık (Yolculuk için)", category = "Elektronik"),
                ChecklistItemEntity(title = "Mevsime Uygun Rahat Yürüyüş Ayakkabısı", category = "Giyim"),
                ChecklistItemEntity(title = "Rüzgarlık / İnce Hırka", category = "Giyim"),
                ChecklistItemEntity(title = "Güneş Gözlüğü & Şapka", category = "Giyim"),
                ChecklistItemEntity(title = "Kişisel İlaçlar & Ağrı Kesici", category = "Sağlık"),
                ChecklistItemEntity(title = "Güneş Kremi & Dudak Nemlendirici", category = "Sağlık"),
                ChecklistItemEntity(title = "Yolculuk Boyun Yastığı & Su Matarası", category = "Seyahat")
            )
            checklistDao.insertItems(defaultItems)
        }

        // Seed initial blog posts if empty so the app has instant offline content
        if (postDao.getPostCount() == 0) {
            val initialPosts = listOf(
                PostEntity(
                    id = 22,
                    title = "Ankara < Çanakkale, Biga ve Düğün",
                    content = "<p>Yeniden bir seyahat hikayesine hoş geldiniz.</p><p>17.08.2023 - Perşembe</p><p>Bugün günlerden Perşembe ve yarın Çanakkale'nin ilçesi Biga'ya gideceğiz.</p><p>Sabah yatağımdan kalktıktan sonra ellerimi ve yüzümü yıkadım. Hemen kahvaltıya gittim. Annem babama kahvaltıdan sonra alınacakları söyledi, biz de onları almaya gittik.</p><p>Büyük bir heyecanla yolculuk hazırlıklarını tamamladık. Yol boyunca İç Anadolu'dan Marmara'ya uzanan manzaraları seyrettik. Biga'da akrabalarımızla buluşup düğün coşkusuna ortak olduk. Çanakkale'nin tarihi havası, boğaz manzarası ve Ege esintileri seyahatimize unutulmaz anlar kattı.</p><p>Düğün merasiminde yöresel oyunlar, sıcak sohbetler ve neşeli anlar hepimizi büyüledi. Ertesi gün Biga çarşısını gezip meşhur Biga köftesini tattık. Tarih kokan sokaklar ve samimi esnaf seyahat günlüğümüze harika anılar olarak yazıldı.</p>",
                    plainText = HtmlUtils.decodeAndCleanHtml(
                        "Yeniden bir seyahat hikayesine hoş geldiniz.\n\n17.08.2023 - Perşembe\n\nBugün günlerden Perşembe ve yarın Çanakkale'nin ilçesi Biga'ya gideceğiz.\n\nSabah yatağımdan kalktıktan sonra ellerimi ve yüzümü yıkadım. Hemen kahvaltıya gittim. Annem babama kahvaltıdan sonra alınacakları söyledi, biz de onları almaya gittik.\n\nBüyük bir heyecanla yolculuk hazırlıklarını tamamladık. Yol boyunca İç Anadolu'dan Marmara'ya uzanan manzaraları seyrettik. Biga'da akrabalarımızla buluşup düğün coşkusuna ortak olduk. Çanakkale'nin tarihi havası, boğaz manzarası ve Ege esintileri seyahatimize unutulmaz anlar kattı.\n\nDüğün merasiminde yöresel oyunlar, sıcak sohbetler ve neşeli anlar hepimizi büyüledi. Ertesi gün Biga çarşısını gezip meşhur Biga köftesini tattık. Tarih kokan sokaklar ve samimi esnaf seyahat günlüğümüze harika anılar olarak yazıldı."
                    ),
                    date = "2024-06-27T20:37:44",
                    link = "https://guvengeziyor1.wordpress.com/2024/06/27/ankaracanakkalebiga-ve-dugun/",
                    category = "Yurtiçi Gezileri",
                    destinationCity = "Çanakkale / Biga",
                    readTimeMinutes = 3,
                    isBookmarked = false
                ),
                PostEntity(
                    id = 20,
                    title = "Ankara-Konya Arası YHT Maceramız",
                    content = "<p>Ankara Garı'ndan kalkan Yüksek Hızlı Tren (YHT) ile Konya'ya uzanan konforlu yolculuğumuz.</p><p>Sabahın erken saatlerinde Ankara Garı'nda buluştuk. Trenimizin perona yanaşmasıyla birlikte heyecanımız katlandı. Bozkırın ortasında 250 km hızla pürüzsüzce süzülen tren, yolculara adeta bir uçak konforu sunuyor.</p><p>Yaklaşık 1 saat 45 dakika gibi kısa bir sürede Mevlana diyarı Konya'ya ulaştık. Trenden iner inmez şehrin sakin ve huzurlu havası bizi karşıladı. Şehir merkezine geçip ilk durağımız olan Mevlana Celaleddin-i Rumi Türbesi ve Müzesi'ni ziyaret ettik. Müzenin manevi derinliği ve Kubbe-i Hadra'nın yeşil çinileri büyüleyiciydi.</p><p>Öğle yemeğinde meşhur Konya fırın kebabı ve taptaze etli ekmek ziyafeti çektik. Alaaddin Tepesi Parkı'nda çaylarımızı yudumlayarak günün yorgunluğunu attık. Akşam yine aynı konforla YHT trenimize binerek Ankara'ya geri döndük. Günübirlik harika bir seyahat rotası!</p>",
                    plainText = HtmlUtils.decodeAndCleanHtml(
                        "Ankara Garı'ndan kalkan Yüksek Hızlı Tren (YHT) ile Konya'ya uzanan konforlu yolculuğumuz.\n\nSabahın erken saatlerinde Ankara Garı'nda buluştuk. Trenimizin perona yanaşmasıyla birlikte heyecanımız katlandı. Bozkırın ortasında 250 km hızla pürüzsüzce süzülen tren, yolculara adeta bir uçak konforu sunuyor.\n\nYaklaşık 1 saat 45 dakika gibi kısa bir sürede Mevlana diyarı Konya'ya ulaştık. Trenden iner inmez şehrin sakin ve huzurlu havası bizi karşıladı. Şehir merkezine geçip ilk durağımız olan Mevlana Celaleddin-i Rumi Türbesi ve Müzesi'ni ziyaret ettik. Müzenin manevi derinliği ve Kubbe-i Hadra'nın yeşil çinileri büyüleyiciydi.\n\nÖğle yemeğinde meşhur Konya fırın kebabı ve taptaze etli ekmek ziyafeti çektik. Alaaddin Tepesi Parkı'nda çaylarımızı yudumlayarak günün yorgunluğunu attık. Akşam yine aynı konforla YHT trenimize binerek Ankara'ya geri döndük. Günübirlik harika bir seyahat rotası!"
                    ),
                    date = "2024-06-27T20:31:45",
                    link = "https://guvengeziyor1.wordpress.com/2024/06/27/ankara-konya-arasi-yht-maceramiz/",
                    category = "Yurtiçi Gezileri",
                    destinationCity = "Konya",
                    readTimeMinutes = 2,
                    isBookmarked = false
                ),
                PostEntity(
                    id = 5,
                    title = "05.09.2022-15.09.2022 Narlı ve Bursa Gezisi",
                    content = "<p>Eylül ayının tatlı serinliğinde Narlı sahil kasabası ve ardından yeşil Bursa durağımız.</p><p>Narlı'da denizin dalga sesleri, zeytin ağaçlarının serin gölgesi ve sakin plajlar bizi karşıladı. Şehrin gürültüsünden uzak, doğayla baş başa geçen huzurlu günlerin ardından Bursa istikametine doğru yola çıktık.</p><p>Bursa'ya vardığımızda ilk olarak Osmanlı payitahtının kalbi olan Tarihi Kapalıçarşı ve Kozahan'ı gezdik. Kozahan'ın asırlık çınarlarının altında demli Türk kahvesi molası verdik. Ardından 20 kubbesi ve şadırvanıyla ünlü heybetli Ulu Cami'yi ziyaret ettik.</p><p>Akşam yemeğinde tereyağının cızırdayan kokusuyla servis edilen hakiki Bursa İskender Kebabı'nı tattık. Şehrin tarihi dokusu, teleferikle Uludağ eteklerine bakış ve Narlı'nın dinlendirici sakinliği birleşince 10 günlük unutulmaz bir rota ortaya çıktı.</p>",
                    plainText = HtmlUtils.decodeAndCleanHtml(
                        "Eylül ayının tatlı serinliğinde Narlı sahil kasabası ve ardından yeşil Bursa durağımız.\n\nNarlı'da denizin dalga sesleri, zeytin ağaçlarının serin gölgesi ve sakin plajlar bizi karşıladı. Şehrin gürültüsünden uzak, doğayla baş başa geçen huzurlu günlerin ardından Bursa istikametine doğru yola çıktık.\n\nBursa'ya vardığımızda ilk olarak Osmanlı payitahtının kalbi olan Tarihi Kapalıçarşı ve Kozahan'ı gezdik. Kozahan'ın asırlık çınarlarının altında demli Türk kahvesi molası verdik. Ardından 20 kubbesi ve şadırvanıyla ünlü heybetli Ulu Cami'yi ziyaret ettik.\n\nAkşam yemeğinde tereyağının cızırdayan kokusuyla servis edilen hakiki Bursa İskender Kebabı'nı tattık. Şehrin tarihi dokusu, teleferikle Uludağ eteklerine bakış ve Narlı'nın dinlendirici sakinliği birleşince 10 günlük unutulmaz bir rota ortaya çıktı."
                    ),
                    date = "2024-05-10T22:33:36",
                    link = "https://guvengeziyor1.wordpress.com/2024/05/10/05-09-2022-15-09-2022-narli-ve-bursa-gezisi/",
                    category = "Yurtiçi Gezileri",
                    destinationCity = "Bursa / Narlı",
                    readTimeMinutes = 3,
                    isBookmarked = false
                ),
                PostEntity(
                    id = 6,
                    title = "2015 Çanakkale Okul Gezisi",
                    content = "<p>Okul arkadaşları ve öğretmenlerimizle birlikte Çanakkale Şehitlikleri ve Tarihi Yarımada ziyaretimiz.</p><p>Otobüsümüz sabahın ilk ışıklarıyla Gelibolu Yarımadası'na ulaştı. Boğazın serin rüzgarı yüzümüze çarparken rehberimizin anlattığı kahramanlık destanları hepimizi derinden etkiledi.</p><p>Kilitbahir Kalesi, Seyit Onbaşı'nın devasa top mermisini tek başına kaldırdığı Mecidiye Tabyası, Conkbayırı ve 57. Alay Şehitliği'ni tek tek adımladık. Şehitler Abidesi'ne vardığımızda Çanakkale Boğazı'na nazır duran o devasa anıtın gölgesinde tüylerimiz diken diken oldu.</p><p>Vatan savunmasının ne büyük fedakarlıklarla kazanıldığını bizzat yerinde hissettik. Bu gezi sadece bir seyahat değil, aynı zamanda tarihimize duyduğumuz saygıyı tazeleyen eşsiz bir hayat dersi oldu.</p>",
                    plainText = HtmlUtils.decodeAndCleanHtml(
                        "Okul arkadaşları ve öğretmenlerimizle birlikte Çanakkale Şehitlikleri ve Tarihi Yarımada ziyaretimiz.\n\nOtobüsümüz sabahın ilk ışıklarıyla Gelibolu Yarımadası'na ulaştı. Boğazın serin rüzgarı yüzümüze çarparken rehberimizin anlattığı kahramanlık destanları hepimizi derinden etkiledi.\n\nKilitbahir Kalesi, Seyit Onbaşı'nın devasa top mermisini tek başına kaldırdığı Mecidiye Tabyası, Conkbayırı ve 57. Alay Şehitliği'ni tek tek adımladık. Şehitler Abidesi'ne vardığımızda Çanakkale Boğazı'na nazır duran o devasa anıtın gölgesinde tüylerimiz diken diken oldu.\n\nVatan savunmasının ne büyük fedakarlıklarla kazanıldığını bizzat yerinde hissettik. Bu gezi sadece bir seyahat değil, aynı zamanda tarihimize duyduğumuz saygıyı tazeleyen eşsiz bir hayat dersi oldu."
                    ),
                    date = "2024-05-10T22:33:35",
                    link = "https://guvengeziyor1.wordpress.com/2024/05/10/2015-canakkale-okul-gezisi/",
                    category = "Yurtiçi Gezileri",
                    destinationCity = "Çanakkale",
                    readTimeMinutes = 3,
                    isBookmarked = false
                ),
                PostEntity(
                    id = 4,
                    title = "Temmuz 2014 İstanbul Gezisi İlk",
                    content = "<p>İstanbul'a ilk adımımız ve büyüleyici metropolün keşfi.</p><p>Yedi tepeli şehrin kalbi olan Tarihi Yarımada'da sabah yürüyüşüyle güne başladık. Sultanahmet Camii'nin altı minaresi ve mavi çinileri, karşısındaki bin beş yüz yıllık Ayasofya ile birlikte zamana meydan okuyordu.</p><p>Topkapı Sarayı'nda padişahların yaşadığı avluları, kutsal emanetler dairesini ve Boğaz'a bakan muazzam manzarayı seyrettik. Ardından rengarenk baharat kokularıyla dolu Kapalıçarşı ve Mısır Çarşısı sokaklarında kaybolduk.</p><p>Eminönü İskelesi'nden kalkan vapura binip martılara simit atarken, Kız Kulesi ve Boğaziçi yalıları adeta birer tablo gibi önümüzden geçti. İstanbul'un canlı ritmi, ezan sesleri ve vapur düdükleri hafızamızda silinmez izler bıraktı.</p>",
                    plainText = HtmlUtils.decodeAndCleanHtml(
                        "İstanbul'a ilk adımımız ve büyüleyici metropolün keşfi.\n\nYedi tepeli şehrin kalbi olan Tarihi Yarımada'da sabah yürüyüşüyle güne başladık. Sultanahmet Camii'nin altı minaresi ve mavi çinileri, karşısındaki bin beş yüz yıllık Ayasofya ile birlikte zamana meydan okuyordu.\n\nTopkapı Sarayı'nda padişahların yaşadığı avluları, kutsal emanetler dairesini ve Boğaz'a bakan muazzam manzarayı seyrettik. Ardından rengarenk baharat kokularıyla dolu Kapalıçarşı ve Mısır Çarşısı sokaklarında kaybolduk.\n\nEminönü İskelesi'nden kalkan vapura binip martılara simit atarken, Kız Kulesi ve Boğaziçi yalıları adeta birer tablo gibi önümüzden geçti. İstanbul'un canlı ritmi, ezan sesleri ve vapur düdükleri hafızamızda silinmez izler bıraktı."
                    ),
                    date = "2014-07-25T22:33:00",
                    link = "https://guvengeziyor1.wordpress.com/2014/07/25/temmuz-2014-istabul-gezisi-ilk/",
                    category = "Yurtiçi Gezileri",
                    destinationCity = "İstanbul",
                    readTimeMinutes = 2,
                    isBookmarked = false
                )
            )
            postDao.insertPosts(initialPosts)
        }
    }

    suspend fun refreshPostsFromNetwork(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val networkPosts = apiService.getPosts(perPage = 25)
            if (networkPosts.isNotEmpty()) {
                val entities = networkPosts.map { dto ->
                    val cleanTitle = HtmlUtils.cleanTitle(dto.title.rendered)
                    val rawContent = dto.content.rendered
                    val plain = HtmlUtils.decodeAndCleanHtml(rawContent)
                    val city = HtmlUtils.detectCity(cleanTitle, plain)
                    val readTime = HtmlUtils.calculateReadTime(plain)

                    PostEntity(
                        id = dto.id,
                        title = cleanTitle,
                        content = rawContent,
                        plainText = plain,
                        date = dto.date,
                        link = dto.link,
                        category = "Yurtiçi Gezileri",
                        destinationCity = city,
                        readTimeMinutes = readTime,
                        isBookmarked = false
                    )
                }

                // Insert new ones, and update content of existing ones
                postDao.insertPosts(entities)
                for (item in entities) {
                    postDao.updatePostContent(
                        id = item.id,
                        title = item.title,
                        content = item.content,
                        plainText = item.plainText,
                        date = item.date,
                        link = item.link,
                        city = item.destinationCity
                    )
                }
                Result.success(entities.size)
            } else {
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleBookmark(postId: Int, currentBookmark: Boolean) = withContext(Dispatchers.IO) {
        postDao.setBookmark(postId, !currentBookmark)
    }

    suspend fun addTripNote(title: String, destination: String, date: String, notes: String, rating: Int) = withContext(Dispatchers.IO) {
        val note = TripNoteEntity(
            title = title.trim(),
            destination = destination.trim(),
            date = date.trim(),
            notes = notes.trim(),
            rating = rating
        )
        tripNoteDao.insertNote(note)
    }

    suspend fun deleteTripNote(id: Long) = withContext(Dispatchers.IO) {
        tripNoteDao.deleteNoteById(id)
    }

    suspend fun toggleChecklistItem(item: ChecklistItemEntity) = withContext(Dispatchers.IO) {
        checklistDao.updateItem(item.copy(isCompleted = !item.isCompleted))
    }

    suspend fun addChecklistItem(title: String, category: String) = withContext(Dispatchers.IO) {
        checklistDao.insertItem(ChecklistItemEntity(title = title.trim(), category = category))
    }

    suspend fun deleteChecklistItem(id: Long) = withContext(Dispatchers.IO) {
        checklistDao.deleteItemById(id)
    }

    suspend fun saveUserProfile(profile: com.example.data.local.UserProfileEntity) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateProfile(profile)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logout()
    }

    suspend fun addExpense(title: String, amount: Double, category: String, destination: String, date: String) = withContext(Dispatchers.IO) {
        val expense = com.example.data.local.ExpenseEntity(
            title = title.trim(),
            amount = amount,
            category = category,
            destination = destination.trim(),
            date = date.trim()
        )
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(id: Long) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpenseById(id)
    }
}
