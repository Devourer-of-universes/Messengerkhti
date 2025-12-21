package com.example.myapplication.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class indivMessage(
    val id: String = "",
    val name: String = "",
    val participantIds: Map<String, Boolean> = emptyMap(), // Измените на Map для Firebase
    val participants: List<String> = emptyList(), // Измените на List<String> для ID
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val createdAt: Long = 0L,
    val group: Boolean = false // Измените isGroup на group (как в базе)
) {
    constructor() : this("", "", emptyMap(), emptyList(), "", 0L, 0L, false)

    // Вычисляемое свойство для обратной совместимости
    val isGroup: Boolean get() = group
}