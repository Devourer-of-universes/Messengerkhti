package com.example.myapplication.model

data class AuditLog(
    val id: String = "",
    val userId: String? = null,
    val userName: String = "",
    val action: String = "",
    val entityType: String = "",
    val entityId: String = "",
    val oldData: Map<String, Any>? = null,
    val newData: Map<String, Any>? = null,
    val ipAddress: String = "",
    val userAgent: String = "",
    val createdAt: Long = 0L
) {
    constructor() : this(
        id = "",
        userId = null,
        userName = "",
        action = "",
        entityType = "",
        entityId = "",
        oldData = null,
        newData = null,
        ipAddress = "",
        userAgent = "",
        createdAt = 0L
    )
}