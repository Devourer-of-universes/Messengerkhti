package com.example.myapplication.model

data class User(
    val id: Int = 0,
    val username: String = "",
    val surname: String = "",
    val name: String = "",
    val patronymic: String = "",
    val email: String = "",
    val telNum: String = "",
    val avatarUri: String = "",
    val status: String = "active",
    val roleId: Int = 2,
    val roleName: String = "Пользователь",
    val postName: String = "",
    val departmentName: String = "",
    val isSuperAdmin: Boolean = false,
    val birthday: String? = null,
    val startDate: String? = null,
    val createdAt: String = "",
    val lastSeenAt: Long = 0L
)