package com.example.myapplication.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.bottomNavigation.BottomItem
import com.example.myapplication.screen.Chat.ChatScreen
import com.example.myapplication.screen.Chat.Message.MessageScreen
import com.example.myapplication.screen.Contacts.ContactsScreen
import com.example.myapplication.screen.Profile.AppInfoScreen
import com.example.myapplication.screen.Profile.ProfileScreen
import com.example.myapplication.screen.Settings.SecurityScreen
import com.example.myapplication.screen.Settings.SettingsScreen
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun AppScreen(
    rootNavController: NavController,
    currentAccent: Color,
    currentThemeMode: ThemeMode,
    onChangeAccent: (Color) -> Unit,
    onChangeThemeMode: (ThemeMode) -> Unit
) {
    Log.d("proverka", "appScreen открылся")

    // Создаем отдельный навигационный контроллер для экранов внутри приложения
    val appNavController = rememberNavController()

    // Отслеживаем текущий маршрут для скрытия BottomBar
    val currentBackStackEntry by appNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Показываем BottomBar только на главных экранах (не на чате)
    val isChatScreen = currentRoute?.startsWith("chat/") == true
    val isSettingsScreen = currentRoute == "settings" ||
            currentRoute == "security" ||
            currentRoute == "appinfo"

    val showBottomBar = !isChatScreen && !isSettingsScreen

    val navItems = listOf(
        BottomItem(title = "Контакты", iconId = Icons.Default.AccountCircle),
        BottomItem(title = "Чаты", iconId = Icons.Default.Home, badgeCount = 5),
        BottomItem(title = "Профиль", iconId = Icons.Default.Info)
    )

    var selectedIndexed by remember {
        mutableIntStateOf(1)
    }

    // Синхронизируем selectedIndexed с текущим маршрутом
    val currentSelectedIndex = when {
        currentRoute == "contacts" -> 0
        currentRoute == "chats" -> 1
        currentRoute == "profile" -> 2
        else -> selectedIndexed
    }

    if (selectedIndexed != currentSelectedIndex && currentSelectedIndex != -1) {
        selectedIndexed = currentSelectedIndex
    }

    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Показываем BottomBar только на главных экранах
            if (showBottomBar) {
                NavigationBar(
                    containerColor = c_accmin,
                    tonalElevation = 0.dp,
                ) {
                    navItems.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                            selected = selectedIndexed == index,
                            onClick = {
                                selectedIndexed = index
                                when (index) {
                                    0 -> appNavController.navigate("contacts") {
                                        popUpTo("contacts") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    1 -> appNavController.navigate("chats") {
                                        popUpTo("chats") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    2 -> appNavController.navigate("profile") {
                                        popUpTo("profile") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (navItem.badgeCount != 0)
                                            Badge {
                                                Text(text = navItem.badgeCount.toString())
                                            }
                                    }
                                ) {
                                    if (selectedIndexed == index) {
                                        Box(
                                            modifier = Modifier
                                                .background(c_acc, shape = CircleShape)
                                                .size(70.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = navItem.iconId,
                                                contentDescription = "",
                                                tint = txtMainWhite
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = navItem.iconId,
                                            contentDescription = "",
                                            tint = txtMainWhite
                                        )
                                    }
                                }
                            },
                            label = {
                                Text(
                                    color = txtMainWhite,
                                    text = navItem.title
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Внутренняя навигация для всех экранов приложения
        // Используем innerPadding для правильных отступов
        NavHost(
            navController = appNavController,
            startDestination = "chats",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // ← Добавили innerPadding
        ) {
            composable("chats") {
                ChatScreen(modifier = Modifier, navController = appNavController)
            }
            composable("contacts") {
                ContactsScreen(modifier = Modifier, navController = appNavController)
            }
            composable("profile") {
                ProfileScreen(
                    modifier = Modifier,
                    navController = appNavController,
                    rootNavController = rootNavController // Добавьте эту строку
                )
            }
            composable("settings") {
                SettingsScreen(
                    navController = appNavController,
                    currentThemeMode = currentThemeMode,
                    onChangeThemeMode = onChangeThemeMode,
                    currentAccent = currentAccent,
                    onChangeAccent = onChangeAccent
                )
            }
            composable("security") {
                SecurityScreen(navController = appNavController)
            }
            composable("appinfo") {
                AppInfoScreen(navController = appNavController)
            }
            composable(
                "chat/{channelId}",
                arguments = listOf(
                    navArgument("channelId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                MessageScreen(
                    navController = appNavController,
                    channelId = channelId
                )
            }
        }
    }
}