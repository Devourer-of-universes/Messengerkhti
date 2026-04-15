package com.example.myapplication.screen.Profile

import androidx.compose.foundation.BorderStroke
import com.example.myapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.bgMainDarkTheme
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun AppInfoScreen(navController: NavController) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground

    // Убираем Scaffold, используем простую Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgMainDarkTheme)
    ) {
        // Верхняя панель - убрали лишние отступы
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Стандартная высота
                .background(bgMainDarkTheme),
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
                text = "О приложении",
                fontSize = 22.sp,
                color = txtMainWhite,
                fontWeight = FontWeight.W500
            )
        }

        // Контент с прокруткой
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Фоновое изображение
            Image(
                painter = painterResource(R.drawable.fon_pin),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Контент поверх фона
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Уменьшили отступы
            ) {
                Text(
                    text = "В создании принимали участие:",
                    fontSize = 24.sp,
                    color = txtMainWhite,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Карточки создателей
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        CreatorCard("Артём", R.drawable.accont_profileavatar)
                        CreatorCard("Андрей", R.drawable.profile_avatar_andrey)
                        CreatorCard("Дава", R.drawable.profile_avatar_dava)
                        CreatorCard("Илья", R.drawable.profile_avatar_ilya)
                        CreatorCard("Руслан", R.drawable.profile_avatar_ruslan)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        CreatorCard("Димас", R.drawable.profile_avatar_dimas)
                        CreatorCard("Егор", R.drawable.profile_avatar_egor)
                        CreatorCard("Никита", R.drawable.profile_avatar_nikita)
                        CreatorCard("Витёк", R.drawable.profile_avatar_vitek)
                        CreatorCard("Эдик")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        CreatorCard("Максим О.", R.drawable.profile_avatar_osha)
                        CreatorCard("Ярик", R.drawable.profile_avatar_yorik)
                        CreatorCard("Сундуй", R.drawable.profile_avatar_sunduy)
                        CreatorCard("Саня")
                        CreatorCard("Максим И.")
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorCard(name: String, image: Int? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp) // Уменьшили отступы между карточками
            .background(
                color = bgMainDarkTheme.copy(alpha = 0.8f), // Добавили прозрачность
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                border = BorderStroke(1.dp, txtMainWhite.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            )
            .height(140.dp), // Уменьшили высоту
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (image != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    contentDescription = "creator_profile_image",
                    painter = painterResource(image),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = txtMainWhite.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.first().toString().uppercase(),
                        fontSize = 36.sp,
                        color = txtMainWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = name,
            fontSize = 14.sp,
            color = txtMainWhite,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}