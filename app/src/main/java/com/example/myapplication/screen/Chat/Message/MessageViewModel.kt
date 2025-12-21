package com.example.myapplication.screen.Chat.Message


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import com.example.myapplication.DataMessanger.CHILD_CREATED_AT
import com.example.myapplication.DataMessanger.NODE_INDIVIDUAL_MESSAGES
import com.example.myapplication.DataMessanger.NODE_MESSAGES
import com.example.myapplication.model.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    private val db = Firebase.database


    fun sendMessage(channelID: String, messageText: String) {
        val message = Message(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = Firebase.auth.currentUser?.uid ?: "",
            message = messageText,
            createdAT = System.currentTimeMillis(),
            senderName = Firebase.auth.currentUser?.displayName ?: "",
            senderImage = null,
            imageUrl = null
        )
//        db.reference.child(NODE_MESSAGES).child(channelID).push().setValue(message)
        // Проверяем, является ли channelID индивидуальным чатом
        if (channelID.contains("_") && channelID.split("_").size == 2) {
            // Это индивидуальный чат - сохраняем в NODE_INDIVIDUAL_MESSAGES
            db.reference
                .child(NODE_INDIVIDUAL_MESSAGES)
                .child(channelID)
                .child("messages")
                .push()
                .setValue(message)
                .addOnSuccessListener {
                    // Обновляем последнее сообщение в чате
                    updateLastMessage(channelID, messageText)
                }
        } else {
            // Это общий канал - сохраняем в NODE_MESSAGES
            db.reference
                .child(NODE_MESSAGES)
                .child(channelID)
                .push()
                .setValue(message)
        }


    }

    private fun updateLastMessage(chatId: String, messageText: String) {
        val updates = hashMapOf<String, Any>(
            "lastMessage" to messageText,
            "lastMessageTime" to System.currentTimeMillis()
        )
        db.reference
            .child(NODE_INDIVIDUAL_MESSAGES)
            .child(chatId)
            .updateChildren(updates)
    }

    fun listenForMessages(channelID: String) {
// Определяем путь к сообщениям в зависимости от типа чата
        val messagesPath = if (channelID.contains("_") && channelID.split("_").size == 2) {
            // Индивидуальный чат
            db.getReference(NODE_INDIVIDUAL_MESSAGES)
                .child(channelID)
                .child("messages")
        } else {
            // Общий канал
            db.getReference(NODE_MESSAGES)
                .child(channelID)
        }

        messagesPath.orderByChild(CHILD_CREATED_AT)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MessageViewModel", "Error listening to messages: ${error.message}")
                }
            })

    }
}