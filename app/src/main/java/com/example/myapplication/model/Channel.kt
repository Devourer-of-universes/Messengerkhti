package com.example.myapplication.model
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties // Это игнорирует лишние поля в Firebase
data class Channel(
    val id: String = "",
    val name: String = "",
    val createdAT: Long = 0L // Обратите внимание: AT в конце
) {
    // Конструктор без параметров для Firebase
    constructor() : this("", "", 0L)
}