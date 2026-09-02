package com.example.myapplication.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.screen.Calendar.CalendarScreen
import com.example.myapplication.screen.Chat.Message.ChatInfoScreen
import com.example.myapplication.screen.Chat.Message.ImageViewerScreen
import com.example.myapplication.screen.Chat.Message.MessageScreen
import com.example.myapplication.screen.Dashboard.DashboardScreen
import com.example.myapplication.screen.Login.signIn.SignInScreen
import com.example.myapplication.screen.Login.signUp.SignUpScreen
import com.example.myapplication.screen.Profile.AppInfoScreen
import com.example.myapplication.screen.Profile.NotificationsScreen
import com.example.myapplication.screen.Settings.PersonalInfoScreen
import com.example.myapplication.screen.Settings.InterfaceSettingsScreen
import com.example.myapplication.screen.Settings.NotificationSettingsScreen
import com.example.myapplication.screen.Settings.SecuritySettingsScreen
import com.example.myapplication.screen.Settings.SettingsScreen
import com.example.myapplication.screen.Tasks.TasksScreen
import com.example.myapplication.ui.theme.ThemeMode
import com.google.firebase.auth.FirebaseAuth
@Composable
fun MainScreen(
    currentAccent: Color,
    currentThemeMode: ThemeMode,
    onChangeAccent: (Color) -> Unit,
    onChangeThemeMode: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
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
            AppScreen(
                navController = navController,
                currentAccent = currentAccent,
                currentThemeMode = currentThemeMode,
                onChangeAccent = onChangeAccent,
                onChangeThemeMode = onChangeThemeMode
            )
        }

        composable("chat/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            MessageScreen(navController, chatId)
        }
        composable("chat_info/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatInfoScreen(navController, chatId)
        }
        composable("settings") {
            SettingsScreen(navController)
        }

        composable("interface_settings") {
            InterfaceSettingsScreen(
                navController = navController,
                currentThemeMode = currentThemeMode,
                onChangeThemeMode = onChangeThemeMode,
                currentAccent = currentAccent,
                onChangeAccent = onChangeAccent
            )
        }

        composable("notification_settings") {
            NotificationSettingsScreen(navController)
        }

        composable("security_settings") {
            SecuritySettingsScreen(navController)
        }

        composable(route = "appinfo") {
            AppInfoScreen(navController)
        }
        composable("profile_info") {
            PersonalInfoScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("notifications") {
            NotificationsScreen(navController)
        }
        composable("tasks") {
            TasksScreen(navController)
        }
        composable("calendar") {
            CalendarScreen(navController = navController)
        }
        composable(
            route = "image_viewer/{imageUrl}/{fileName}/{senderName}/{sentAt}",
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("fileName") { type = NavType.StringType },
                navArgument("senderName") { type = NavType.StringType },
                navArgument("sentAt") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val fileName = backStackEntry.arguments?.getString("fileName") ?: "image"
            val senderName = backStackEntry.arguments?.getString("senderName") ?: ""
            val sentAt = backStackEntry.arguments?.getString("sentAt") ?: ""

            // Декодируем
            val decodedUrl = try {
                java.net.URLDecoder.decode(imageUrl, "UTF-8")
            } catch (e: Exception) {
                imageUrl
            }
            val decodedFileName = try {
                java.net.URLDecoder.decode(fileName, "UTF-8")
            } catch (e: Exception) {
                fileName
            }
            val decodedSender = try {
                java.net.URLDecoder.decode(senderName, "UTF-8")
            } catch (e: Exception) {
                senderName
            }
            val decodedTime = try {
                java.net.URLDecoder.decode(sentAt, "UTF-8")
            } catch (e: Exception) {
                sentAt
            }

            ImageViewerScreen(
                navController = navController,
                imageUrl = decodedUrl,
                fileName = decodedFileName,
                senderName = decodedSender,
                sentAt = decodedTime
            )
        }
    }
}