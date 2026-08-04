package com.example.myapplication.model

data class ChatInfo(
    val id: String = "",
    val name: String = "",
    val isGroup: Boolean = false,
    val avatarUri: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val participants: List<ChatParticipant> = emptyList(),
    val lastMessage: String = "",
    val lastMessageAt: String = "",
    val messageCount: Int = 0
)