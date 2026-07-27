package com.example.myapplication.model

data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // message, mention, system, etc
    val title: String = "",
    val content: String = "",
    val data: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val createdAt: Long = 0L
) {
    constructor() : this(
        id = "",
        userId = "",
        type = "",
        title = "",
        content = "",
        data = emptyMap(),
        isRead = false,
        createdAt = 0L
    )
}