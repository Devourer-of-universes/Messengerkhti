package com.example.myapplication.screen.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.firm.ThemeSelectorItem
import com.example.myapplication.ui.theme.*
import com.example.myapplication.ui.theme.txtMainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterfaceSettingsScreen(
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
                        text = "Интерфейс",
                        fontSize = 22.sp,
                        color = txtMainWhite,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Тема оформления
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

                    // Акцентный цвет
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
}