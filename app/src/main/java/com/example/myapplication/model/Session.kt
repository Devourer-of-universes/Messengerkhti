// model/Session.kt
package com.example.myapplication.model

data class Session(
    val id: Int = 0,
    val application: String = "",
    val device: String = "",
    val location: String = "",
    val ipAddress: String = "",
    val userAgent: String = "",
    val lastActivity: String = "",
    val isActive: Boolean = true,
    val isCurrent: Boolean = false
)

data class SessionsResponse(
    val activeSessions: List<Session>,
    val historySessions: List<Session>,
    val currentSessionId: Int?
)

data class SimpleResponse(
    val success: Boolean,
    val message: String? = null
)