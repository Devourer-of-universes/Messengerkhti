package com.example.myapplication.bottomNavigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomItem(
    var title: String = "",
    var iconId: ImageVector,
    var badgeCount : Int? = null,
)