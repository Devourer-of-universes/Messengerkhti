package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.TokenManager
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.net.URISyntaxException

class WebSocketManager {

    private var socket: Socket? = null
    private val gson = Gson()
    private val tag = "WebSocketManager"

    private var isConnected = false

    companion object {
        @Volatile
        private var instance: WebSocketManager? = null

        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }

    fun connect() {
        if (isConnected) {
            Log.d(tag, "Already connected")
            return
        }

        try {
            val token = TokenManager.getAccessToken()
            if (token.isNullOrEmpty()) {
                Log.e(tag, "No token available")
                return
            }

            val options = IO.Options().apply {
                this@apply.query = "token=$token"
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
            }

            val url = Constants.BASE_URL.replace("http://", "ws://")
            socket = IO.socket(url, options)

            socket?.on(Socket.EVENT_CONNECT) {
                isConnected = true
                Log.d(tag, "WebSocket connected")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                isConnected = false
                Log.d(tag, "WebSocket disconnected")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(tag, "WebSocket connection error: ${args?.joinToString()}")
            }

            socket?.connect()

        } catch (e: URISyntaxException) {
            Log.e(tag, "Invalid WebSocket URL: ${e.message}")
        } catch (e: Exception) {
            Log.e(tag, "WebSocket connection error: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            socket?.disconnect()
            socket = null
            isConnected = false
            Log.d(tag, "WebSocket disconnected manually")
        } catch (e: Exception) {
            Log.e(tag, "Error disconnecting: ${e.message}")
        }
    }

    fun isConnected(): Boolean = isConnected

    fun sendMessage(chatId: Int, content: String): Boolean {
        if (!isConnected) {
            Log.e(tag, "Cannot send message: not connected")
            return false
        }

        return try {
            val data = mapOf(
                "chatId" to chatId,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )
            socket?.emit("send-message", gson.toJson(data))
            Log.d(tag, "Message sent: $content")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error sending message: ${e.message}")
            false
        }
    }

    fun sendTyping(chatId: Int, isTyping: Boolean): Boolean {
        if (!isConnected) return false

        return try {
            val data = mapOf(
                "chatId" to chatId,
                "isTyping" to isTyping
            )
            socket?.emit("typing", gson.toJson(data))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun markAsRead(chatId: Int, messageId: Int): Boolean {
        if (!isConnected) return false

        return try {
            val data = mapOf(
                "chatId" to chatId,
                "messageId" to messageId
            )
            socket?.emit("read-receipt", gson.toJson(data))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun listenForMessages(): Flow<ChatMessage> = callbackFlow {
        val onMessage = { args: Array<Any>? ->
            try {
                val data = args?.firstOrNull() as? String
                if (data != null) {
                    val message = gson.fromJson(data, ChatMessage::class.java)
                    trySend(message)
                    Log.d(tag, "Message received: ${message.content}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error parsing message: ${e.message}")
            }
        }

        socket?.on("new-message", onMessage as ((Array<Any>) -> Unit)?)

        awaitClose {
            socket?.off("new-message", onMessage as ((Array<Any>) -> Unit)?)
        }
    }

    fun listenForTyping(): Flow<Pair<Int, Boolean>> = callbackFlow {
        val onTyping = { args: Array<Any>? ->
            try {
                val data = args?.firstOrNull() as? String
                if (data != null) {
                    val typingData = gson.fromJson(data, TypingData::class.java)
                    trySend(typingData.userId to typingData.isTyping)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error parsing typing data: ${e.message}")
            }
        }

        socket?.on("user-typing", onTyping as ((Array<Any>) -> Unit)?)

        awaitClose {
            socket?.off("user-typing", onTyping as ((Array<Any>) -> Unit)?)
        }
    }

    fun listenForReadReceipts(): Flow<ReadReceiptData> = callbackFlow {
        val onRead = { args: Array<Any>? ->
            try {
                val data = args?.firstOrNull() as? String
                if (data != null) {
                    val receipt = gson.fromJson(data, ReadReceiptData::class.java)
                    trySend(receipt)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error parsing read receipt: ${e.message}")
            }
        }

        socket?.on("message-read", onRead as ((Array<Any>) -> Unit)?)

        awaitClose {
            socket?.off("message-read", onRead as ((Array<Any>) -> Unit)?)
        }
    }

    // Data classes for parsing
    private data class TypingData(
        val userId: Int,
        val isTyping: Boolean
    )

    data class ReadReceiptData(
        val userId: Int,
        val chatId: Int,
        val lastReadMessageId: Int
    )
}