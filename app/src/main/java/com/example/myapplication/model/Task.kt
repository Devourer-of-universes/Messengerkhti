package com.example.myapplication.model

data class Task(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val priority: String = "medium",
    val status: String = "pending",
    val progress: Int = 0,
    val startDate: String? = null,
    val dueDate: String? = null,
    val assigneeId: Int? = null,
    val assigneeName: String? = null,
    val creatorId: Int? = null,
    val creatorName: String? = null
)

data class DashboardStats(
    val tasksInProgress: Int = 0,
    val documentsTotal: Int = 0,
    val activeProcesses: Int = 0,
    val unreadNotifications: Int = 0
)