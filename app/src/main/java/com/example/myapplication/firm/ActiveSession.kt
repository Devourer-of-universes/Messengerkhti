package com.example.myapplication.firm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class ActiveSession(
    val id: String,
    val deviceType: DeviceType,
    val deviceName: String,
    val osVersion: String,
    val browser: String? = null,
    val location: String,
    val ipAddress: String,
    val lastActive: Date,
    val isCurrentSession: Boolean = false
)

enum class DeviceType {
    PHONE, TABLET, COMPUTER, UNKNOWN
}

@Composable
fun ActiveSessionCard(
    session: ActiveSession,
    onTerminate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    var showTerminateDialog by remember { mutableStateOf(false) }

    // Иконка в зависимости от типа устройства
    val deviceIcon = when (session.deviceType) {
        DeviceType.PHONE -> Icons.Default.PhoneAndroid
        DeviceType.TABLET -> Icons.Default.TabletAndroid
        DeviceType.COMPUTER -> Icons.Default.Computer
        else -> Icons.Outlined.Info
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (session.isCurrentSession)
                accentColor.copy(alpha = 0.1f)
            else surfaceColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (session.isCurrentSession)
            BorderStroke(1.dp, accentColor)
        else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Верхняя строка: иконка, устройство, метка "Текущая сессия"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Иконка устройства
                    Icon(
                        imageVector = deviceIcon,
                        contentDescription = null,
                        tint = if (session.isCurrentSession) accentColor else onSurfaceColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Название устройства
                    Column {
                        Text(
                            text = session.deviceName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (session.isCurrentSession) accentColor else onSurfaceColor
                        )
                        Text(
                            text = "${session.osVersion}${session.browser?.let { " • $it" } ?: ""}",
                            fontSize = 12.sp,
                            color = onSurfaceColor.copy(alpha = 0.7f)
                        )
                    }
                }

                // Бейдж "Текущая сессия"
                if (session.isCurrentSession) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Текущая",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Информация о местоположении и IP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Локация
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = session.location,
                        fontSize = 14.sp,
                        color = onSurfaceColor.copy(alpha = 0.8f)
                    )
                }

                // IP адрес
                Text(
                    text = "IP: ${session.ipAddress}",
                    fontSize = 12.sp,
                    color = onSurfaceColor.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Дата и время последней активности
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🕐",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDate(session.lastActive),
                    fontSize = 14.sp,
                    color = onSurfaceColor.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопка завершить сессию
            if (!session.isCurrentSession) {
                Button(
                    onClick = { showTerminateDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.1f),
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Завершить сессию", fontSize = 14.sp)
                }
            }
        }
    }

    // Диалог подтверждения завершения сессии
    if (showTerminateDialog) {
        AlertDialog(
            onDismissRequest = { showTerminateDialog = false },
            title = { Text("Завершить сессию?") },
            text = {
                Text("Вы будете выйдены из сессии на устройстве ${session.deviceName} (${session.location})")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTerminate(session.id)
                        showTerminateDialog = false
                    }
                ) {
                    Text("Завершить", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTerminateDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

// Форматирование даты
private fun formatDate(date: Date): String {
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < 60 * 1000 -> "Только что"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} мин. назад"
        diff < 24 * 60 * 60 * 1000 -> {
            val hours = diff / (60 * 60 * 1000)
            "$hours ${pluralize(hours, "час", "часа", "часов")} назад"
        }
        else -> {
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            format.format(date)
        }
    }
}

private fun pluralize(count: Long, one: String, few: String, many: String): String {
    return when {
        count % 10 == 1L && count % 100 != 11L -> one
        count % 10 in 2..4 && (count % 100 !in 12..14) -> few
        else -> many
    }
}

// Preview функция для тестирования
@Composable
fun ActiveSessionPreview() {
    val sampleSession = ActiveSession(
        id = "1",
        deviceType = DeviceType.PHONE,
        deviceName = "Xiaomi Redmi Note 10",
        osVersion = "Android 13",
        location = "Москва, Россия",
        ipAddress = "192.168.1.1",
        lastActive = Date(System.currentTimeMillis() - 5 * 60 * 1000) // 5 минут назад
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            ActiveSessionCard(
                session = sampleSession,
                onTerminate = {}
            )
        }
    }
}