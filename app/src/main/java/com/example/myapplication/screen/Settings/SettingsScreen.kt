package com.example.myapplication.screen.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.firm.ThemeSelectorItem
import com.example.myapplication.ui.theme.*
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeMode: ThemeMode,
    onChangeThemeMode: (ThemeMode) -> Unit,
    currentAccent: Color,
    onChangeAccent: (Color) -> Unit,
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_acc = MaterialTheme.colorScheme.primary

    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding: PaddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(c_bg)
                    .padding(innerPadding)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(c_bg)
                ) {
                    // Верхняя панель
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
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
                                tint = c_bgtxt
                            )
                        }

                        Text(
                            text = "Оформление",
                            fontSize = 22.sp,
                            color = c_bgtxt,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Контент с прокруткой
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // === ТЕМА ОФОРМЛЕНИЯ ===
                        item {
                            SettingsHeader(title = "Тема оформления")
                        }

                        item {
                            SettingsCard {
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
                        }

                        // === АКЦЕНТНЫЙ ЦВЕТ ===
                        item {
                            SettingsHeader(title = "Акцентный цвет")
                        }

                        item {
                            SettingsCard {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SettingsHeader(title: String) {
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
fun SettingsCard(
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

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground

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
            color = c_bgtxt,
            fontSize = 16.sp
        )
    }
}