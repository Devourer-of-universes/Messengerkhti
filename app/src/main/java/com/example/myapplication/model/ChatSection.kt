package com.example.myapplication.model

import android.os.Parcelable


data class ChatSection(
    val id: String,
    val name: String,
    val chatIds: List<String> = emptyList(), // ID чатов в этой секции
    val chatTypes: Map<String, ChatType> = emptyMap(), // ID чата -> его тип
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val order: Int = 0 // Для сортировки
)

enum class ChatType {
    CHANNEL,
    INDIVIDUAL
}

// Обертка для чата с типом
data class ChatWithType(
    val id: String,
    val type: ChatType,
    val name: String,
    val data: Any // Channel или indivMessage
)