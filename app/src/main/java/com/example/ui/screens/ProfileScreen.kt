package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.CompassAmber
import com.example.ui.theme.SunsetTerracotta
import com.example.ui.viewmodel.TravelViewModel

data class TravelBadge(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean
)

@Composable
fun ProfileScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val posts by viewModel.filteredPosts.collectAsStateWithLifecycle()
    val bookmarkedPosts by viewModel.bookmarkedPosts.collectAsStateWithLifecycle()
    val tripNotes by viewModel.tripNotes.collectAsStateWithLifecycle()
    val checklistItems by viewModel.checklistItems.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    val isLoggedIn = userProfile != null && userProfile!!.isLoggedIn

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isLoggedIn) {
            item {
                NotLoggedInCard(
                    onLogin = { name, email, city, bio ->
                        viewModel.registerOrUpdateUser(name, email, city, bio)
                    },
                    onQuickDemo = {
                        viewModel.quickDemoLogin()
                    }
                )
            }
        } else {
            val profile = userProfile!!

            // Profile Header Card
            item {
                MemberProfileCard(
                    profile = profile,
                    onEditClick = { showEditProfileDialog = true },
                    onLogoutClick = { viewModel.logout() }
                )
            }

            // Stats row
            item {
                Text(
                    text = "Gezgin İstatistikleri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MemberStatPill(
                        label = "Okunan",
                        value = "${posts.size}",
                        icon = Icons.Default.MenuBook,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MemberStatPill(
                        label = "Favoriler",
                        value = "${bookmarkedPosts.size}",
                        icon = Icons.Default.Bookmark,
                        tint = SunsetTerracotta,
                        modifier = Modifier.weight(1f)
                    )
                    MemberStatPill(
                        label = "Gezi Notum",
                        value = "${tripNotes.size}",
                        icon = Icons.Default.EditNote,
                        tint = CompassAmber,
                        modifier = Modifier.weight(1f)
                    )
                    MemberStatPill(
                        label = "Bavul Hazır",
                        value = "${checklistItems.count { it.isCompleted }}",
                        icon = Icons.Default.CheckCircle,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Travel Badges Section
            item {
                Text(
                    text = "Seyahat Başarı Rozetleri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                val badges = listOf(
                    TravelBadge(
                        title = "Demiryolu Kaşifi",
                        description = "YHT Ankara - Konya tren rotası tamamlandı",
                        icon = Icons.Default.DirectionsTransit,
                        isUnlocked = true
                    ),
                    TravelBadge(
                        title = "Tarih & Vatan",
                        description = "Çanakkale Şehitlikleri & Biga gezi yazısı",
                        icon = Icons.Default.EmojiEvents,
                        isUnlocked = true
                    ),
                    TravelBadge(
                        title = "Gezi Yazarı",
                        description = if (tripNotes.isNotEmpty()) "${tripNotes.size} gezi anısı kaydedildi" else "İlk gezi notunu günlüğe ekle",
                        icon = Icons.Default.EditNote,
                        isUnlocked = tripNotes.isNotEmpty()
                    ),
                    TravelBadge(
                        title = "Bavul Ustası",
                        description = if (checklistItems.any { it.isCompleted }) "Seyahat hazırlık listesi aktif" else "Bavul listesinden madde tamamla",
                        icon = Icons.Default.CheckCircle,
                        isUnlocked = checklistItems.any { it.isCompleted }
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    badges.forEach { badge ->
                        BadgeCard(badge = badge)
                    }
                }
            }

            // Membership Benefits Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aktif Gezgin Üyelik Avantajları",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "• Yazıları çevrimdışı favorilere ekleme ve sınırsız kaydetme\n• Sesli dinleme (Türkçe TTS) ile yolda eller serbest okuma\n• Kişisel seyahat günlüğü ve bavul hazırlık listesi senkronizasyonu\n• WordPress ve Blogspot sitelerine uygulama içi doğrudan erişim",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    if (showEditProfileDialog && userProfile != null) {
        EditProfileDialog(
            currentProfile = userProfile!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, email, city, bio ->
                viewModel.registerOrUpdateUser(name, email, city, bio)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun MemberProfileCard(
    profile: UserProfileEntity,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("member_profile_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.fullName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Onaylı Gezgin",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CompassAmber.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = profile.memberLevel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = profile.homeCity,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Profili Düzenle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = profile.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Üyelik: ${profile.joinedDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Çıkış Yap", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun NotLoggedInCard(
    onLogin: (name: String, email: String, city: String, bio: String) -> Unit,
    onQuickDemo: () -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardMembership,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Güven Geziyor Gezgin Üyeliği",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gezi notlarını kaydet, rozetler kazan ve topluluğa katıl",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Demo Login Button for instant evaluation
            Button(
                onClick = onQuickDemo,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_demo_login_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunsetTerracotta)
            ) {
                Icon(imageVector = Icons.Default.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hızlı Giriş Yap (Erdal Güven)")
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Tabs: Giriş Yap / Üye Ol
            TabRow(
                selectedTabIndex = tabIndex,
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = { Text("Giriş Yap", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = { Text("Yeni Üye Ol", fontWeight = FontWeight.Bold) }
                )
            }

            // Form inputs
            if (tabIndex == 1) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ad Soyad") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_input")
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Yaşadığınız Şehir (Örn: Rize, Ankara)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Place, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta Adresi") },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input")
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Parola") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input")
            )

            Button(
                onClick = {
                    val finalName = if (name.isNotBlank()) name else email.substringBefore("@")
                    onLogin(finalName, email, city, "Gezgin seyahat sever.")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_submit_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (tabIndex == 0) Icons.Default.AccountCircle else Icons.Default.PersonAdd,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (tabIndex == 0) "Giriş Yap" else "Üyeliğimi Başlat")
            }
        }
    }
}

@Composable
private fun MemberStatPill(
    label: String,
    value: String,
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun BadgeCard(badge: TravelBadge) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (badge.isUnlocked) CompassAmber.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) CompassAmber else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                    if (badge.isUnlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Kazanıldı",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentProfile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, city: String, bio: String) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.fullName) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var city by remember { mutableStateOf(currentProfile.homeCity) }
    var bio by remember { mutableStateOf(currentProfile.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Profili Düzenle", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ad Soyad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-posta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Şehir") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Hakkımda / Biyografi") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, email, city, bio) },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}
