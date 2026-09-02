package com.example.myapplication.screen.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Session
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.txtMainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val sessions by viewModel.sessions.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val c_bg = MaterialTheme.colorScheme.background
    val c_surf = MaterialTheme.colorScheme.surface
    val c_acc = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        viewModel.loadSessions()
    }

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
                        text = "Безопасность",
                        fontSize = 22.sp,
                        color = txtMainWhite,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.loadSessions() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = c_acc
                                )
                            ) {
                                Text("Повторить")
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            // Активные сессии
                            item {
                                SettingsHeader(title = "Активные сессии")
                            }

                            if (sessions.isEmpty()) {
                                item {
                                    SettingsCard {
                                        Text(
                                            text = "Нет активных сессий",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            } else {
                                item {
                                    SettingsCard {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            sessions.forEach { session ->
                                                SessionCard(
                                                    session = session,
                                                    isCurrent = session.id == currentSessionId,
                                                    onTerminate = {
                                                        viewModel.terminateSession(session.id)
                                                    }
                                                )
                                            }
                                        }

                                        if (sessions.size > 1) {
                                            Spacer(modifier = Modifier.height(16.dp))

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(
                                                        onClick = {
                                                            viewModel.terminateOtherSessions()
                                                        },
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() }
                                                    )
                                                    .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Logout,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Завершить все остальные сессии",
                                                    color = MaterialTheme.colorScheme.error,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
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
    }
}

@Composable
fun SessionCard(
    session: Session,
    isCurrent: Boolean,
    onTerminate: () -> Unit
) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surftxt = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка устройства
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrent)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getDeviceIcon(session.device),
                    contentDescription = null,
                    tint = if (isCurrent)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getDeviceDisplayName(session),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = c_bgtxt
                    )
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Текущая",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${session.location} • ${session.ipAddress}",
                    fontSize = 12.sp,
                    color = c_surftxt
                )

                Text(
                    text = "Последняя активность: ${formatSessionTime(session.lastActivity)}",
                    fontSize = 11.sp,
                    color = c_surftxt.copy(alpha = 0.7f)
                )
            }

            // Кнопка завершения
            if (!isCurrent) {
                IconButton(
                    onClick = onTerminate,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Завершить сессию",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Вспомогательные функции
fun getDeviceIcon(device: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        device.contains("Windows", ignoreCase = true) -> Icons.Default.Computer
        device.contains("macOS", ignoreCase = true) -> Icons.Default.Computer
        device.contains("Android", ignoreCase = true) -> Icons.Default.PhoneAndroid
        device.contains("iPhone", ignoreCase = true) -> Icons.Default.PhoneIphone
        device.contains("iPad", ignoreCase = true) -> Icons.Default.Tablet
        device.contains("Linux", ignoreCase = true) -> Icons.Default.Computer
        else -> Icons.Default.Computer
    }
}

fun getDeviceDisplayName(session: Session): String {
    return when {
        session.application.isNotEmpty() -> {
            if (session.device.isNotEmpty() && session.device != session.application) {
                "${session.device} / ${session.application}"
            } else {
                session.application
            }
        }
        session.device.isNotEmpty() -> session.device
        else -> "Неизвестное устройство"
    }
}

fun formatSessionTime(dateString: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        val outputFormat = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}