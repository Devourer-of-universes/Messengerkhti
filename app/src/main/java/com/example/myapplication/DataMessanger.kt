package com.example.myapplication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

object DataMessanger {
    var chatName: String = ""

//    var isChangePhone: Boolean = false
//    var isChangeUsername: Boolean = false

    val NODE_CHANNELS = "channels"
    val NODE_INDIVIDUAL_MESSAGES = "individual_messages"
    val NODE_MESSAGES = "messages"
    val NODE_USERS = "users"

    val CHILD_CREATED_AT = "createdAT"
}