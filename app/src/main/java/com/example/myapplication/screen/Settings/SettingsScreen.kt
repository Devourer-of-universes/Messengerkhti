package com.example.myapplication.screen.Settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.firm.ActiveSession
import com.example.myapplication.firm.ActiveSessionCard
import com.example.myapplication.firm.DeviceType
import com.example.myapplication.firm.SettingsSwitch
import com.example.myapplication.firm.ThemeSelectorItem
import com.example.myapplication.ui.theme.*
import java.util.Date

@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeMode: ThemeMode,
    onChangeThemeMode: (ThemeMode) -> Unit,
    currentAccent: Color,
    onChangeAccent: (Color) -> Unit,
) {
    val col_bgmain = MaterialTheme.colorScheme.background
    val col_bgtxt = MaterialTheme.colorScheme.onBackground
    val col_ = MaterialTheme.colorScheme.

    // ✅ 1. Объявляем переменные для переключателей
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding: PaddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(col_bgmain)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Верхняя панель с кнопкой назад
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(col_bgmain),
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
                                tint = col_bgtxt
                            )
                        }

                        Text(
                            text = "Настройки",
                            fontSize = 22.sp,
                            color = col_bgtxt,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Основной контент с прокруткой
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // === ТЕМА ОФОРМЛЕНИЯ ===
                        SettingsSection(
                            title = "Тема оформления",
                            content = {
                                ThemeOption(
                                    text = "Как в системе",
                                    selected = currentThemeMode == ThemeMode.SYSTEM,
                                    onClick = { onChangeThemeMode(ThemeMode.SYSTEM) }
                                )
                                ThemeOption(
                                    text = "Светлая",
                                    selected = currentThemeMode == ThemeMode.LIGHT,
                                    onClick = { onChangeThemeMode(ThemeMode.LIGHT) }
                                )
                                ThemeOption(
                                    text = "Тёмная",
                                    selected = currentThemeMode == ThemeMode.DARK,
                                    onClick = { onChangeThemeMode(ThemeMode.DARK) }
                                )
                            }
                        )

                        // === АКЦЕНТНЫЙ ЦВЕТ ===
                        SettingsSection(
                            title = "Акцентный цвет",
                            content = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ThemeSelectorItem(
                                        color = accentOrange,
                                        name = "Оранжевый",
                                        isSelected = currentAccent == accentOrange,
                                        onClick = { onChangeAccent(accentOrange) }
                                    )
                                    ThemeSelectorItem(
                                        color = accentBlue,
                                        name = "Синий",
                                        isSelected = currentAccent == accentBlue,
                                        onClick = { onChangeAccent(accentBlue) }
                                    )
                                    ThemeSelectorItem(
                                        color = accentGreen,
                                        name = "Зелёный",
                                        isSelected = currentAccent == accentGreen,
                                        onClick = { onChangeAccent(accentGreen) }
                                    )
                                    ThemeSelectorItem(
                                        color = accentRed,
                                        name = "Красный",
                                        isSelected = currentAccent == accentRed,
                                        onClick = { onChangeAccent(accentRed) }
                                    )
                                    ThemeSelectorItem(
                                        color = accentYellow,
                                        name = "Жёлтый",
                                        isSelected = currentAccent == accentYellow,
                                        onClick = { onChangeAccent(accentYellow) }
                                    )
                                    ThemeSelectorItem(
                                        color = accentCyan,
                                        name = "Циан",
                                        isSelected = currentAccent == accentCyan,
                                        onClick = { onChangeAccent(accentCyan) }
                                    )
                                    ThemeSelectorItem(
                                        color = accentMagenta,
                                        name = "Фиолетовый",
                                        isSelected = currentAccent == accentMagenta,
                                        onClick = { onChangeAccent(accentMagenta) }
                                    )
                                }
                            }
                        )

                        // === УВЕДОМЛЕНИЯ ===
                        SettingsSection(
                            title = "Уведомления",
                            content = {
                                SettingsSwitch(
                                    title = "Включить уведомления",
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it }
                                )
                                SettingsSwitch(
                                    title = "Звук",
                                    description = "Звуковые сигналы для сообщений",
                                    checked = soundEnabled,
                                    onCheckedChange = { soundEnabled = it },
                                    enabled = notificationsEnabled
                                )
                                SettingsSwitch(
                                    title = "Вибрация",
                                    description = "Виброотклик при уведомлениях",
                                    checked = vibrationEnabled,
                                    onCheckedChange = { vibrationEnabled = it },
                                    enabled = notificationsEnabled
                                )
                            }
                        )

                        // === БЕЗОПАСНОСТЬ / АКТИВНЫЕ СЕССИИ ===
                        SettingsSection(
                            title = "Активные сессии",
                            content = {
                                val sessions = remember {
                                    listOf(
                                        ActiveSession(
                                            id = "1",
                                            deviceType = DeviceType.COMPUTER,
                                            deviceName = "MacBook Pro",
                                            osVersion = "macOS 14",
                                            browser = "Chrome",
                                            location = "Москва, Россия",
                                            ipAddress = "192.168.1.100",
                                            lastActive = Date(System.currentTimeMillis() - 5 * 60 * 1000),
                                            isCurrentSession = true
                                        ),
                                        ActiveSession(
                                            id = "2",
                                            deviceType = DeviceType.PHONE,
                                            deviceName = "Xiaomi Redmi Note 10",
                                            osVersion = "Android 13",
                                            location = "Москва, Россия",
                                            ipAddress = "192.168.1.101",
                                            lastActive = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
                                        ),
                                        ActiveSession(
                                            id = "3",
                                            deviceType = DeviceType.TABLET,
                                            deviceName = "iPad Pro",
                                            osVersion = "iPadOS 17",
                                            browser = "Safari",
                                            location = "Санкт-Петербург, Россия",
                                            ipAddress = "192.168.1.102",
                                            lastActive = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000)
                                        )
                                    )
                                }

                                var activeSessions by remember { mutableStateOf(sessions) }

                                // ✅ Карточки с отступами
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    activeSessions.forEach { session ->
                                        ActiveSessionCard(
                                            session = session,
                                            onTerminate = { sessionId ->
                                                activeSessions = activeSessions.filter { it.id != sessionId }
                                            }
                                        )
                                    }
                                }

                                // Кнопка "Завершить все сессии"
                                if (activeSessions.size > 1) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedButton(
                                        onClick = {
                                            activeSessions = activeSessions.filter { it.isCurrentSession }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.Red
                                        ),
                                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Завершить все остальные сессии")
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    )
}

// Вспомогательные компоненты
@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}