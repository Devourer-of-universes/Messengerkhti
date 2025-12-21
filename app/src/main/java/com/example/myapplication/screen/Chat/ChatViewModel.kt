package com.example.myapplication.screen.Chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.DataMessanger.NODE_CHANNELS
import com.example.myapplication.DataMessanger.NODE_INDIVIDUAL_MESSAGES
import com.example.myapplication.model.Channel
import com.example.myapplication.model.UserData
import com.example.myapplication.model.indivMessage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    private val firebaseDatabase = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    private val _individualMessages = MutableStateFlow<List<indivMessage>>(emptyList())
    val individualMessages = _individualMessages.asStateFlow()

    init {
        setupChannelsListener()
        setupIndividualMessagesListener()
    }

    private fun setupChannelsListener() {
        firebaseDatabase.getReference(NODE_CHANNELS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Channel>()
                    snapshot.children.forEach { data ->
                        try {
                            val channelId = data.key ?: ""
                            Log.d("ChatViewModel", "Processing channel: $channelId")

                            // Получаем как объект Channel
                            val channel = data.getValue(Channel::class.java)
                            if (channel != null) {
                                // Восстанавливаем id из ключа если нужно
                                val finalChannel = if (channel.id.isEmpty()) {
                                    channel.copy(id = channelId)
                                } else {
                                    channel
                                }
                                list.add(finalChannel)
                                Log.d("ChatViewModel", "Added channel: ${finalChannel.name}")
                            } else {
                                // Если не получается распарсить как Channel, попробуем получить имя
                                val name = data.child("name").getValue(String::class.java)
                                if (name != null) {
                                    val createdAt = data.child("createdAT").getValue(Long::class.java) ?: 0L
                                    list.add(Channel(id = channelId, name = name, createdAT = createdAt))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "Error parsing channel ${data.key}: ${e.message}")
                        }
                    }
                    _channels.value = list
                    Log.d("ChatViewModel", "Total channels loaded: ${list.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatViewModel", "Error getting channels: ${error.message}")
                }
            })
    }

    private fun setupIndividualMessagesListener() {
        val currentUserId = auth.currentUser?.uid ?: return

        firebaseDatabase.getReference(NODE_INDIVIDUAL_MESSAGES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<indivMessage>()
                    snapshot.children.forEach { data ->
                        try {
                            val chatId = data.key ?: ""
                            Log.d("ChatViewModel", "Processing individual chat: $chatId")

                            // Получаем данные как Map для отладки
                            val chatMap = data.value as? Map<*, *>

                            if (chatMap != null) {
                                Log.d("ChatViewModel", "Chat map keys: ${chatMap.keys}")

                                // Получаем имя чата
                                val name = chatMap["name"] as? String ?: "Chat"

                                // Получаем участников
                                val participants = when (val parts = chatMap["participants"]) {
                                    is List<*> -> parts.filterIsInstance<String>()
                                    else -> emptyList()
                                }

                                // Получаем participantIds
                                val participantIds = when (val ids = chatMap["participantIds"]) {
                                    is Map<*, *> -> ids.mapKeys { it.key.toString() }.mapValues { it.value == true }
                                    else -> emptyMap()
                                }

                                // Получаем поле group
                                val group = chatMap["group"] as? Boolean ?: false

                                Log.d("ChatViewModel", "Participants: $participants, Contains current user: ${participants.contains(currentUserId)}")

                                // Проверяем, является ли текущий пользователь участником
                                val isParticipant = participantIds.containsKey(currentUserId) ||
                                        participants.contains(currentUserId)

                                if (isParticipant) {
                                    // Определяем имя для отображения
                                    val displayName = if (!group && participants.size >= 2) {
                                        // Для индивидуального чата находим другого участника
                                        val otherUserId = participants.firstOrNull { it != currentUserId }
                                        if (otherUserId != null) {
                                            // Пытаемся получить имя пользователя из базы
                                            // Пока используем сохраненное имя или ID
                                            name.takeIf { it.isNotBlank() } ?: "User $otherUserId"
                                        } else {
                                            name
                                        }
                                    } else {
                                        name
                                    }

                                    // Создаем объект indivMessage
                                    val chat = indivMessage(
                                        id = chatId,
                                        name = displayName,
                                        participantIds = participantIds,
                                        participants = participants,
                                        lastMessage = chatMap["lastMessage"] as? String ?: "",
                                        lastMessageTime = (chatMap["lastMessageTime"] as? Long) ?: 0L,
                                        createdAt = (chatMap["createdAt"] as? Long) ?: 0L,
                                        group = group
                                    )

                                    list.add(chat)
                                    Log.d("ChatViewModel", "Added individual chat: $displayName (id: $chatId)")
                                } else {
                                    Log.d("ChatViewModel", "User $currentUserId is not participant in chat $chatId")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "Error parsing individual chat ${data.key}: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                    _individualMessages.value = list
                    Log.d("ChatViewModel", "Total individual chats loaded: ${list.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatViewModel", "Error getting individual chats: ${error.message}")
                }
            })
    }

    fun addChannel(name: String) {
        val key = firebaseDatabase.getReference(NODE_CHANNELS).push().key ?: return

        // Сохраняем в правильном формате
        val channelData = hashMapOf<String, Any>(
            "id" to key,
            "name" to name,
            "createdAT" to System.currentTimeMillis()
        )

        firebaseDatabase.getReference(NODE_CHANNELS)
            .child(key)
            .setValue(channelData)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Channel added successfully: $name")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error adding channel: ${e.message}")
            }
    }

    fun getOrCreateIndividualChat(otherUserId: String, otherUserName: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        // Создаем упорядоченный ID для чата
        val chatId = if (currentUserId < otherUserId) {
            "${currentUserId}_${otherUserId}"
        } else {
            "${otherUserId}_${currentUserId}"
        }

        // Проверяем, существует ли уже такой чат
        firebaseDatabase.getReference(NODE_INDIVIDUAL_MESSAGES).child(chatId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    // Создаем новый индивидуальный чат в правильном формате
                    val individualChat = hashMapOf<String, Any>(
                        "id" to chatId,
                        "name" to otherUserName,
                        "participantIds" to hashMapOf(
                            currentUserId to true,
                            otherUserId to true
                        ),
                        "participants" to listOf(currentUserId, otherUserId),
                        "lastMessage" to "",
                        "lastMessageTime" to 0L,
                        "createdAt" to System.currentTimeMillis(),
                        "group" to false
                    )

                    firebaseDatabase.getReference(NODE_INDIVIDUAL_MESSAGES)
                        .child(chatId)
                        .setValue(individualChat)
                        .addOnSuccessListener {
                            Log.d("ChatViewModel", "Individual chat created: $chatId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatViewModel", "Error creating chat: ${e.message}")
                        }
                }
            }
    }

    fun getChatName(channelId: String, onResult: (String) -> Unit) {
        if (channelId.contains("_") && channelId.split("_").size == 2) {
            // Индивидуальный чат
            firebaseDatabase.getReference(NODE_INDIVIDUAL_MESSAGES)
                .child(channelId)
                .child("name")
                .get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.getValue(String::class.java) ?: "Chat"
                    onResult(name)
                }
                .addOnFailureListener {
                    onResult("Chat")
                }
        } else {
            // Общий канал
            firebaseDatabase.getReference(NODE_CHANNELS)
                .child(channelId)
                .child("name")
                .get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.getValue(String::class.java) ?: "Channel"
                    onResult(name)
                }
                .addOnFailureListener {
                    onResult("Channel")
                }
        }
    }

}