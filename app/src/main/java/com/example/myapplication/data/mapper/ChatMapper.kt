// data/mapper/ChatMapper.kt
package com.example.myapplication.data.mapper

import com.example.myapplication.model.Chat
import com.example.myapplication.network.ApiChat

object ChatMapper {

    fun mapToChat(apiChat: ApiChat): Chat {
        return Chat(
            id = apiChat.id,
            name = apiChat.name,
            isGroup = apiChat.is_group,
            avatarUri = apiChat.avatar_uri,
            createdBy = apiChat.created_by,
            createdAt = apiChat.created_at,
            lastMessageAt = apiChat.last_message_at,
            unreadCount = apiChat.unread_count,
            participants = emptyList(),
            lastMessage = apiChat.last_message?.content ?: "",
            lastMessageUserId = apiChat.last_message?.user_id ?: 0,
            folderId = apiChat.folder_id
        )
    }
}