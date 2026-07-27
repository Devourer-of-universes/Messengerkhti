package com.example.myapplication.data.mapper

import com.example.myapplication.model.Chat
import com.example.myapplication.model.ChatInfo
import com.example.myapplication.model.ChatParticipant
import com.example.myapplication.model.ChatMessage

object ChatMapper {

    fun mapToChatInfo(
        chat: Chat,
        participants: List<ChatParticipant>,
        messages: List<ChatMessage>
    ): ChatInfo {
        return ChatInfo(
            id = chat.id.toString(), // Int -> String
            name = chat.name,
            isGroup = chat.isGroup,
            avatarUri = chat.avatarUri,
            createdBy = chat.createdBy.toString(), // Int -> String
            createdAt = chat.createdAt,
            participants = participants,
            lastMessage = messages.firstOrNull()?.content ?: "",
            lastMessageAt = messages.firstOrNull()?.createdAt?.toLongOrNull() ?: 0L,
            messageCount = messages.size
        )
    }

    fun getChatType(chat: Chat): String {
        return if (chat.isGroup) "Групповой" else "Личный"
    }

    fun getParticipantCount(chat: Chat): Int {
        return chat.participants.size
    }
}