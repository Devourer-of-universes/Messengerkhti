package com.example.myapplication.model

data class DashboardStats(
    val tasksInProgress: Int = 0,
    val documentsTotal: Int = 0,
    val activeProcesses: Int = 0,
    val unreadNotifications: Int = 0
)