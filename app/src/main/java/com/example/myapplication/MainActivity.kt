package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.myapplication.screen.MainScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.ThemeStorage
import com.example.myapplication.ui.theme.accentOrange
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Загружаем сохранённые настройки
            val savedAccent = remember { ThemeStorage.loadAccent(this) }
            val savedThemeMode = remember { ThemeStorage.loadThemeMode(this) }

            var currentAccent by remember { mutableStateOf(savedAccent) }
            var currentThemeMode by remember { mutableStateOf(ThemeMode.fromInt(savedThemeMode)) }

            MyApplicationTheme(
                accentColor = currentAccent,
                themeMode = currentThemeMode
            ) {
                MainScreen(
                    currentAccent = currentAccent,
                    currentThemeMode = currentThemeMode,
                    onChangeAccent = { newAccent ->
                        currentAccent = newAccent
                        ThemeStorage.saveAccent(this, newAccent)
                    },
                    onChangeThemeMode = { newMode ->
                        currentThemeMode = newMode
                        ThemeStorage.saveThemeMode(this, newMode.value)
                    }
                )
            }
        }
    }
}