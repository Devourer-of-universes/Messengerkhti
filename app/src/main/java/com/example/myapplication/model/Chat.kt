package com.example.myapplication.model

// model/Chat.kt
data class Chat(
    val id: Int = 0,
    val name: String = "",
    val isGroup: Boolean = false,
    val avatarUri: String? = "",
    val createdBy: Int = 0,
    val createdAt: String = "",  // ← String
    val lastMessageAt: String = "",  // ← String
    val unreadCount: Int = 0,
    val participants: List<ChatParticipant> = emptyList(),
    val lastMessage: String = "",
    val lastMessageUserId: Int = 0,
    val folderId: Int? = null
)
data class ChatParticipant(
    val id: Int = 0,
    val surname: String = "",
    val name: String = "",
    val avatarUri: String = "",
    val isOnline: Boolean = false,
    val lastSeenAt: Long = 0L
)