package com.example.myapplication.model

data class Channel(
    val id: String = "",
    val name: String,
    val createdAT:Long = System.currentTimeMillis()
)