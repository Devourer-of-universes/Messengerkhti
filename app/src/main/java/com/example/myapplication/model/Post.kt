package com.example.myapplication.model

data class Post(
    val id: String = "",
    val departmentId: String = "",
    val name: String = "",
    val createdAt: Long = 0L
) {
    constructor() : this(
        id = "",
        departmentId = "",
        name = "",
        createdAt = 0L
    )
}