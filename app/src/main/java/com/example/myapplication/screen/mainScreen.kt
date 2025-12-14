package com.example.myapplication.screen

import android.R.attr.type
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.bottomNavigation.BottomItem
import com.example.myapplication.screen.Chat.ChatScreen
import com.example.myapplication.screen.Chat.Message.MessageScreen
import com.example.myapplication.screen.Contacts.ContactsScreen
import com.example.myapplication.screen.Profile.ProfileScreen
import com.example.myapplication.screen.Login.signIn.SignInScreen
import com.example.myapplication.screen.Login.signUp.SignUpScreen
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.txtMainSelected
import com.google.firebase.auth.FirebaseAuth
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val start = if (currentUser != null) "app" else "login"

    NavHost(navController = navController, startDestination = start) {
        composable(route = "login") {
            SignInScreen(navController)
        }
        composable(route = "signup") {
            SignUpScreen(navController)
        }
        composable(route = "app") {
            AppScreen(navController)
        }
        composable(route = "chat/{channelId}", arguments = listOf(
            navArgument("channelId"){
                type = NavType.StringType
            }
        )) {
            val channelId = it.arguments?.getString("channelId") ?: ""
            MessageScreen(navController,channelId)
        }
    }
}