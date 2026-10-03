package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InAppWebScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoutesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TravelViewModel
import com.example.util.TravelNotificationManager

class MainActivity : ComponentActivity() {
    private val viewModel: TravelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TravelNotificationManager.createNotificationChannels(this)
        handleNotificationIntent(intent)
        setContent {
            MyApplicationTheme {
                TravelApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            TravelNotificationManager.ACTION_OPEN_POST -> {
                val postId = intent.getIntExtra(TravelNotificationManager.EXTRA_POST_ID, -1)
                if (postId != -1) {
                    viewModel.openPostDetail(postId)
                }
            }
            TravelNotificationManager.ACTION_OPEN_JOURNAL -> {
                viewModel.navigateTo(Screen.JOURNAL)
            }
            TravelNotificationManager.ACTION_OPEN_ROUTES -> {
                viewModel.navigateTo(Screen.ROUTES)
            }
        }
    }
}

@Composable
fun TravelApp(
    viewModel: TravelViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            TravelNotificationManager.showTravelTip(
                context,
                "🔔 Bildirimler Aktif!",
                "Güven Geziyor seyahat ipuçları ve yeni rota bildirimleri başarıyla etkinleştirildi."
            )
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (currentScreen == Screen.DETAIL) {
        PostDetailScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    val navItems = listOf(
                        NavigationItem(
                            screen = Screen.POSTS,
                            label = "Yazılar",
                            selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
                            unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
                            tag = "nav_posts"
                        ),
                        NavigationItem(
                            screen = Screen.WEB_VIEW,
                            label = "Web Siteleri",
                            selectedIcon = Icons.Default.Language,
                            unselectedIcon = Icons.Outlined.Language,
                            tag = "nav_web"
                        ),
                        NavigationItem(
                            screen = Screen.ROUTES,
                            label = "Rotalar",
                            selectedIcon = Icons.Default.Map,
                            unselectedIcon = Icons.Outlined.Map,
                            tag = "nav_routes"
                        ),
                        NavigationItem(
                            screen = Screen.JOURNAL,
                            label = "Günlüğüm",
                            selectedIcon = Icons.Default.EditNote,
                            unselectedIcon = Icons.Outlined.EditNote,
                            tag = "nav_journal"
                        ),
                        NavigationItem(
                            screen = Screen.PROFILE,
                            label = "Üyelik",
                            selectedIcon = Icons.Default.AccountCircle,
                            unselectedIcon = Icons.Outlined.AccountCircle,
                            tag = "nav_profile"
                        )
                    )

                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(item.tag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition",
                modifier = Modifier.padding(innerPadding)
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.POSTS -> HomeScreen(viewModel = viewModel)
                    Screen.WEB_VIEW -> InAppWebScreen(viewModel = viewModel)
                    Screen.ROUTES -> RoutesScreen(viewModel = viewModel)
                    Screen.JOURNAL -> JournalScreen(viewModel = viewModel)
                    Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                    Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    Screen.ABOUT -> AboutScreen(viewModel = viewModel)
                    Screen.DETAIL -> PostDetailScreen(viewModel = viewModel)
                }
            }
        }
    }
}

private data class NavigationItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)
