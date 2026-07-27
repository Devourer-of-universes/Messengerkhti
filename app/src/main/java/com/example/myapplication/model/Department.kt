package com.example.myapplication.model

data class Department(
    val id: String = "",
    val name: String = "",
    val parentDepartmentId: String? = null,
    val managerId: String? = null,
    val managerName: String = "",
    val code: String = "",
    val description: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val createdAt: Long = 0L
) {
    constructor() : this(
        id = "",
        name = "",
        parentDepartmentId = null,
        managerId = null,
        managerName = "",
        code = "",
        description = "",
        email = "",
        phone = "",
        location = "",
        createdAt = 0L
    )
}