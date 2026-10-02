package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InAppWebScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoutesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TravelViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TravelApp()
            }
        }
    }
}

@Composable
fun TravelApp(
    viewModel: TravelViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

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
