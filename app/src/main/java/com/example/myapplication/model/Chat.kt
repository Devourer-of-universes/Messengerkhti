package com.example.myapplication.model

data class Chat(
    val id: Int = 0,
    val name: String = "",
    val isGroup: Boolean = false,
    val avatarUri: String = "",
    val createdBy: Int = 0,
    val createdAt: Long = 0L,
    val lastMessageAt: Long = 0L,
    val unreadCount: Int = 0,
    val participants: List<ChatParticipant> = emptyList(),
    val lastMessage: ChatMessage? = null
)

data class ChatParticipant(
    val id: Int = 0,
    val surname: String = "",
    val name: String = "",
    val avatarUri: String = "",
    val isOnline: Boolean = false,
    val lastSeenAt: Long = 0L
)