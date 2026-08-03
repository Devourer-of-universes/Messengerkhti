// model/ChatMessage.kt
package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

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
    val isDeleted: Boolean = false,
    val readers: List<ReadReceipt> = emptyList()  // Пока пустой
) {
    // Вычисляемые свойства
    val isRead: Boolean
        get() = readers.isNotEmpty()

    val isDelivered: Boolean
        get() = true // Или своя логика

    val readCount: Int
        get() = readers.size
}

data class ReadReceipt(
    val userId: Int,
    val readAt: String
)