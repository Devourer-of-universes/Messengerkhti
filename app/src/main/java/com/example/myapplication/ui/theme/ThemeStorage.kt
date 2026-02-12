package com.example.myapplication.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore("app_settings")

object ThemeStorage {
    private val ACCENT_KEY = intPreferencesKey("accent_color")
    private val THEME_KEY = intPreferencesKey("theme_mode")

    // Сохраняем цвет
    fun saveAccent(context: Context, color: Color) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences[ACCENT_KEY] = color.toArgb()
            }
        }
    }

    // Загружаем цвет (синхронно, для простоты)
    fun loadAccent(context: Context): Color {
        val preferences = runBlocking { context.dataStore.data.first() }
        val colorInt = preferences[ACCENT_KEY] ?: Color(0xFFF56E0F).toArgb()
        return Color(colorInt)
    }
    // --- 👇 НОВОЕ: Тема ---
    fun saveThemeMode(context: Context, mode: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences[THEME_KEY] = mode
            }
        }
    }

    fun loadThemeMode(context: Context): Int {
        val preferences = runBlocking { context.dataStore.data.first() }
        return preferences[THEME_KEY] ?: 0 // 0 = системная по умолчанию
    }
}

// 👇 Enum для удобства
enum class ThemeMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromInt(value: Int): ThemeMode {
            return entries.find { it.value == value } ?: SYSTEM
        }
    }
}
