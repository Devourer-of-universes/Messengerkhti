package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

// utils/TokenManager.kt
object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"
    private const val KEY_AVATAR_URI = "avatar_uri"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveTokens(
        accessToken: String,
        userId: Int? = null,
        username: String? = null,
        email: String? = null
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            userId?.let { putInt(KEY_USER_ID, it) }
            username?.let { putString(KEY_USERNAME, it) }
            email?.let { putString(KEY_EMAIL, it) }
            apply()
        }
    }

    fun saveAvatarUri(avatarUri: String) {
        prefs.edit().putString(KEY_AVATAR_URI, avatarUri).apply()
    }

    fun getAvatarUri(): String? {
        return prefs.getString(KEY_AVATAR_URI, null)
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrEmpty()
}