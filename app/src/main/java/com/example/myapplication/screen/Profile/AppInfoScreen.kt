package com.example.myapplication.screen.Profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import com.example.myapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.model.FAQItemData
import com.example.myapplication.ui.theme.bgMainDarkTheme
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun AppInfoScreen(navController: NavController,
                  fromScreen: String? = null) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // Уменьшили с 60.dp до 56.dp
                            .background(c_acc),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
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
                            text = "О приложении",
                            fontSize = 22.sp,
                            color = c_bgtxt,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,

                        ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Image(
                                painterResource(R.drawable.mes_def_icon),
                                contentDescription = "icon",
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(RoundedCornerShape(32.dp))
                            )

                            Text(
                                text = "КИС: Мессенджер",
                                fontSize = 22.sp,
                                color = c_bgtxt,
                                modifier = Modifier
                                    .padding(vertical = 8.dp),
                            )

                            Text(
                                text = "версия 0.26.7",
                                fontSize = 16.sp,
                                color = c_surftxt,
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                            )
                        }


                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        val faqItems = remember {
                            listOf(
                                FAQItemData(
                                    id = 1,
                                    question = "Что это за приложение?",
                                    answer = "Это приложение для обмена сообщениями, которое позволяет общаться с друзьями и коллегами в реальном времени."
                                ),
                                FAQItemData(
                                    id = 2,
                                    question = "Как зарегистрироваться?",
                                    answer = "Для регистрации нажмите на кнопку 'Создать аккаунт' на экране входа. Введите свой email, придумайте пароль и следуйте инструкциям."
                                ),
                                FAQItemData(
                                    id = 3,
                                    question = "Бесплатно ли приложение?",
                                    answer = "Да, приложение полностью бесплатно. Все функции доступны без ограничений."
                                ),
                                FAQItemData(
                                    id = 4,
                                    question = "Как восстановить пароль?",
                                    answer = "На экране входа нажмите 'Забыли пароль?'. Введите свой email, и мы отправим инструкции по восстановлению."
                                ),
                                FAQItemData(
                                    id = 5,
                                    question = "Поддерживает ли приложение групповые чаты?",
                                    answer = "Да, вы можете создавать групповые чаты с неограниченным количеством участников."
                                )
                            )
                        }

                        var expandedId by remember { mutableStateOf<Int?>(null) }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Заголовок
                            item {
                                Text(
                                    text = "Часто задаваемые вопросы",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = c_bgtxt,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }

                            // Список FAQ
                            items(faqItems) { item ->
                                FAQItem(
                                    question = item.question,
                                    answer = item.answer,
                                    isExpanded = expandedId == item.id,
                                    onToggle = {
                                        expandedId = if (expandedId == item.id) null else item.id
                                    }
                                )
                            }
                        }
                    }
                }

            }
        }
    )
}

@Composable
fun FAQItem(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(), // Плавное изменение размера
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 4.dp else 2.dp
        )
    ) {
        Column {
            // Вопрос
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded)
                        Icons.Outlined.ExpandLess
                    else
                        Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Ответ (с анимацией)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = answer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}