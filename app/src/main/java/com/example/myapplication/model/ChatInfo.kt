package com.example.myapplication.model

data class ChatInfo(
    val id: String = "",           // String для совместимости
    val name: String = "",
    val isGroup: Boolean = false,
    val avatarUri: String = "",
    val createdBy: String = "",     // String для совместимости
    val createdAt: String = "",
    val adminId: String? = null,
    val participants: List<ChatParticipant> = emptyList(),
    val lastMessage: String = "",
    val lastMessageAt: String = "",
    val messageCount: Int = 0
)