package com.example.myapplication.screen.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.accentBlue
import com.example.myapplication.ui.theme.accentOrange
import com.example.myapplication.ui.theme.bgMainBack
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun SettingsScreen(navController: NavController,
                   currentThemeMode: ThemeMode,
                   onChangeThemeMode: (ThemeMode) -> Unit,
                   currentAccent: Color,                    // ← ТЕКУЩИЙ АКЦЕНТ ДЛЯ ПОДСВЕТКИ
                   onChangeAccent: (Color) -> Unit,
                   onBack: () -> Unit) {
    val col_bgmain = MaterialTheme.colorScheme.background;
    val col_bgsec = MaterialTheme.colorScheme.surface;
    val col_acc = MaterialTheme.colorScheme.primary;
    val col_acctxt = MaterialTheme.colorScheme.onPrimary;
    val col_sec = MaterialTheme.colorScheme.secondary;
    val col_bgtxt = MaterialTheme.colorScheme.onBackground;
    val col_sectxt = MaterialTheme.colorScheme.onSurface;
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(col_bgmain)
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            contentColor = txtMainWhite,
                            containerColor = Color.Transparent,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                hoveredElevation = 0.dp,
                                focusedElevation = 0.dp
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(50.dp),
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }

                        Text(
                            text = "Настройки",
                            fontSize = 25.sp,
                            color = col_bgtxt
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Тема оформления",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Системная
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChangeThemeMode(ThemeMode.SYSTEM) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentThemeMode == ThemeMode.SYSTEM,
                                    onClick = { onChangeThemeMode(ThemeMode.SYSTEM) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Как в системе")
                            }

                            // Светлая
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChangeThemeMode(ThemeMode.LIGHT) }
                                    .padding(vertical = 12.dp)
                            ) {
                                RadioButton(
                                    selected = currentThemeMode == ThemeMode.LIGHT,
                                    onClick = { onChangeThemeMode(ThemeMode.LIGHT) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Светлая")
                            }

                            // Тёмная
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChangeThemeMode(ThemeMode.DARK) }
                                    .padding(vertical = 12.dp)
                            ) {
                                RadioButton(
                                    selected = currentThemeMode == ThemeMode.DARK,
                                    onClick = { onChangeThemeMode(ThemeMode.DARK) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Тёмная")
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Акцентный цвет",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                                    // ← ФУНКЦИЯ СМЕНЫ АКЦЕНТА

                            // Кнопка выбора оранжевого
                            Button(
                                onClick = {
                                    onChangeAccent(accentOrange)      // ← ВЫБИРАЕМ НОВЫЙ АКЦЕНТ
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentOrange
                                )
                            ) {
                                Text("Оранжевый")
                            }

                            // Кнопка выбора синего
                            Button(
                                onClick = {
                                    onChangeAccent(accentBlue)        // ← ВЫБИРАЕМ НОВЫЙ АКЦЕНТ
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentBlue
                                )
                            ) {
                                Text("Синий")
                            }
                        }
                    }
                }
            }
        }
    )
}









