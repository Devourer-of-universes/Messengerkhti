package com.example.myapplication.model

data class ChatMessage(
    val id: Int = 0,
    val chatId: Int = 0,
    val userId: Int = 0,
    val content: String = "",
    val createdAt: String = "",
    val surname: String = "",
    val name: String = "",
    val avatarUri: String = "",
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
)

// Для обратной совместимости с вашим кодом
typealias Message = ChatMessage