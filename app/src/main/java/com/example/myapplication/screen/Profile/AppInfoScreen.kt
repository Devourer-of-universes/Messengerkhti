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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.DataMessanger.chatName
import com.example.myapplication.screen.Chat.Message.ContentMessage
import com.example.myapplication.screen.Chat.Message.MessageViewModel
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun AppInfoScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding: PaddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgGreyDark)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()

                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(bgGreyDark)
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
                            text = "О приложении",
                            fontSize = 25.sp,
                            color = txtMainWhite
                        )
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgGreyDark)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.fon_pin),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "В создании принимали участие:",
                                fontSize = 25.sp,
                                color = txtMainWhite
                            )
                            Row(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.fillMaxWidth(0.33f)) {
                                    CreatorCard("Артём", R.drawable.accont_profileavatar)
                                    CreatorCard("Андрей", R.drawable.profile_avatar_andrey)
                                    CreatorCard("Дава", R.drawable.profile_avatar_dava)
                                    CreatorCard("Илья", R.drawable.profile_avatar_ilya)
                                    CreatorCard("Руслан", R.drawable.profile_avatar_ruslan)
                                }
                                Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                                    CreatorCard("Димас", R.drawable.profile_avatar_dimas)
                                    CreatorCard("Егор", R.drawable.profile_avatar_egor)
                                    CreatorCard("Никита", R.drawable.profile_avatar_nikita)
                                    CreatorCard("Витёк", R.drawable.profile_avatar_vitek)
                                    CreatorCard("Эдик")
                                }
                                Column(modifier = Modifier.fillMaxWidth(1f)) {
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
        }
    )
}
@Composable
fun CreatorCard(name: String, image: Int? = null) {
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
            .background(
                color = bgGreyDark,
                shape = RoundedCornerShape(20.dp) // ← скругление фона
            )
            .border(
                border = BorderStroke(2.dp, bgGreyDark), // ← цвет границы
                shape = RoundedCornerShape(20.dp) // ← скругление границы
            )
            .height(150.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (image != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    contentDescription = "creator_profile_image",
                    painter = painterResource(image),
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(45.dp)), // ← скругление картинки
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            // Показываем первую букву если нет картинки
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = txtMainSelected, // любой цвет
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.first().toString().uppercase(), // первая буква
                        fontSize = 48.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        Text(
            text = name,
            fontSize = 18.sp,
            color = txtMainWhite,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}