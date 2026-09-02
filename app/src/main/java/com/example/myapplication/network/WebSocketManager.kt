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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.net.URISyntaxException

// network/WebSocketManager.kt - убедитесь, что он правильно настроен

object WebSocketManager {
    private var socket: Socket? = null
    private val gson = Gson()
    private var isConnected = false

    private val _newMessageFlow = MutableSharedFlow<ChatMessage>()
    val newMessageFlow: SharedFlow<ChatMessage> = _newMessageFlow.asSharedFlow()

    fun connect(token: String) {
        if (isConnected) return

        try {
            val options = IO.Options().apply {
                this@apply.query = "token=$token"
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
            }

            socket = IO.socket("http://10.0.2.2:3000", options)

            socket?.on(Socket.EVENT_CONNECT) {
                isConnected = true
                Log.d("WebSocket", "Connected")
            }

            socket?.on("new-message") { args ->
                val data = args[0] as? String
                data?.let {
                    try {
                        val message = gson.fromJson(it, ChatMessage::class.java)
                        _newMessageFlow.tryEmit(message)
                        Log.d("WebSocket", "New message received: ${message.content}")
                    } catch (e: Exception) {
                        Log.e("WebSocket", "Error parsing message: ${e.message}")
                    }
                }
            }

            socket?.connect()
        } catch (e: Exception) {
            Log.e("WebSocket", "Error connecting: ${e.message}")
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
        isConnected = false
    }
}