package com.example.myapplication.screen.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.txtMainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_surftxt = MaterialTheme.colorScheme.onSurface

    var newTasks by remember { mutableStateOf(true) }
    var docStatus by remember { mutableStateOf(true) }
    var deadlineReminder by remember { mutableStateOf(true) }
    var internalNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = c_bg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(c_bg)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Верхняя панель
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(c_acc),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = txtMainWhite
                        )
                    }

                    Text(
                        text = "Уведомления",
                        fontSize = 22.sp,
                        color = txtMainWhite,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Типы уведомлений
                    item {
                        SettingsHeader(title = "Типы уведомлений")
                    }

                    item {
                        SettingsCard {
                            NotificationSwitchItem(
                                title = "Новые задачи",
                                description = "Уведомлять о новых назначенных задачах",
                                checked = newTasks,
                                onCheckedChange = { newTasks = it }
                            )
                            NotificationSwitchItem(
                                title = "Изменение статуса документов",
                                description = "Когда документ переходит на ваше согласование",
                                checked = docStatus,
                                onCheckedChange = { docStatus = it }
                            )
                            NotificationSwitchItem(
                                title = "Напоминания о deadline",
                                description = "За 1 день до истечения срока",
                                checked = deadlineReminder,
                                onCheckedChange = { deadlineReminder = it }
                            )
                        }
                    }

                    // Каналы уведомлений
                    item {
                        SettingsHeader(title = "Каналы уведомлений")
                    }

                    item {
                        SettingsCard {
                            NotificationSwitchItem(
                                title = "Внутренние уведомления",
                                description = "Уведомления в системе",
                                checked = internalNotifications,
                                onCheckedChange = { internalNotifications = it }
                            )
                            NotificationSwitchItem(
                                title = "Email-уведомления",
                                description = "На рабочую почту",
                                checked = emailNotifications,
                                onCheckedChange = { emailNotifications = it }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}