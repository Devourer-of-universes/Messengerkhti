package com.example.myapplication.screen.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.firm.ActiveSession
import com.example.myapplication.firm.ActiveSessionCard
import com.example.myapplication.firm.DeviceType
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    navController: NavController
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_acc = MaterialTheme.colorScheme.primary

    // Убираем Scaffold полностью, используем простой Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c_bg)
    ) {
        // === ШАПКА С КНОПКОЙ НАЗАД ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Уменьшили с 60.dp до 56.dp
                .background(c_acc),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = c_bgtxt
                )
            }

            Text(
                text = "Безопасность",
                fontSize = 22.sp,
                color = c_bgtxt,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // === Контент с прокруткой ===
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp) // Небольшие отступы для контента
        ) {
            // === АКТИВНЫЕ СЕССИИ ===
            item {
                SecurityHeader(title = "Активные сессии")
            }

            item {
                SecurityCard {
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

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeSessions.forEach { session ->
                            ActiveSessionCard(
                                session = session,
                                onTerminate = { sessionId ->
                                    activeSessions = activeSessions.filter { it.id != sessionId }
                                }
                            )
                        }
                    }

                    if (activeSessions.size > 1) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        activeSessions = activeSessions.filter { it.isCurrentSession }
                                    },
                                    indication = ripple(),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(horizontal = 25.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.account_lock),
                                colorFilter = ColorFilter.tint(Color.Red), // Добавили tint для цвета
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Завершить все остальные сессии",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
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

@Composable
fun SecurityHeader(title: String) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp), // Уменьшили vertical с 15.dp до 8.dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = c_bgtxt,
            fontSize = 20.sp,
            fontWeight = W700
        )
    }
}

@Composable
fun SecurityCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val c_surf = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp) // Убрали top отступ, оставили только горизонтальные
            .shadow(
                elevation = 16.dp,
                spotColor = Color.Black.copy(alpha = 0.9f),
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .clip(RoundedCornerShape(24.dp))
            .background(c_surf),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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