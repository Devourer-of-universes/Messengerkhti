package com.example.myapplication.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.bottomNavigation.BottomItem
import com.example.myapplication.firm.NavBottomBar
import com.example.myapplication.screen.Chat.ChatScreen
import com.example.myapplication.screen.Contacts.ContactsScreen
import com.example.myapplication.screen.Profile.ProfileScreen
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun AppScreen(
    navController: NavController,
    currentAccent: Color,
    currentThemeMode: ThemeMode,
    onChangeAccent: (Color) -> Unit,
    onChangeThemeMode: (ThemeMode) -> Unit,
) {
    var selectedIndexed by rememberSaveable {
        mutableIntStateOf(1)
    }

    // Слушаем возврат из настроек
    LaunchedEffect(navController.currentBackStackEntry) {
        val savedTab = navController.currentBackStackEntry?.savedStateHandle?.get<Int>("selectedTab")
        if (savedTab != null) {
            selectedIndexed = savedTab
            navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("selectedTab")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavBottomBar(
                selectedIndex = selectedIndexed,
                onItemSelected = { index ->
                    selectedIndexed = index
                }
            )
        },
        content = { innerPadding: PaddingValues ->
            ContentScreen(
                modifier = Modifier.padding(innerPadding),
                selectedIndexed = selectedIndexed,
                navController = navController as NavHostController,
                onNavigateToSettings = { route ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedTab", selectedIndexed)
                    navController.navigate(route)
                }
            )
        }
    )
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndexed: Int,
    navController: NavHostController,
    onNavigateToSettings: (String) -> Unit  // Добавляем параметр
) {
    val c_bg = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .background(c_bg)
    ) {
        when (selectedIndexed) {
            0 -> ContactsScreen(navController, modifier = modifier, )
            1 -> ChatScreen(navController, modifier = modifier, )
            2 -> ProfileScreen(
                modifier = modifier,
                navController = navController,
//                onNavigateToSettings = onNavigateToSettings  // Передаем функцию
            )
        }
    }
}