package com.example.myapplication.screen.Contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.DataMessanger.chatName
import com.example.myapplication.R
import com.example.myapplication.firm.FirmOutlineTextField
import com.example.myapplication.model.Channel
import com.example.myapplication.model.UserData
import com.example.myapplication.screen.Chat.AddChannelDialog
import com.example.myapplication.screen.Chat.ChatViewModel
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.bgMainDarkTheme
import com.example.myapplication.ui.theme.bgSecDarkTheme
import com.example.myapplication.ui.theme.txtMainGrey

import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun ContactsScreen(modifier: Modifier = Modifier,
                   navController: NavController,
) {
    val viewModel = hiltViewModel<ContactsScreenViewModel>()
    val chatViewModel = hiltViewModel<ChatViewModel>() // Получаем ChatViewModel
    val users = viewModel.users.collectAsState()
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier.fillMaxSize()

    ) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = c_bg
                ),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(110.dp)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(c_acc),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    var index by remember {
                        mutableStateOf("")
                    }
                    OutlinedTextField(
                        value = index,
                        onValueChange = { index = it },
                        placeholder = {
                            Text(
                                text = "Поиск контакта...",
                                color = c_surftxt,
                                fontSize = 16.sp,
                                maxLines = 1

                            )
                        },
                        // Параметр leadingIcon вынесен из placeholder
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Поиск",
                                modifier = Modifier.padding(start = 12.dp),
                                tint = c_bgtxt
                            )
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(0.75f), // Немного увеличил ширину для удобства
                        shape = RoundedCornerShape(50),
                        singleLine = true, // Чтобы текст не переносился
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = c_surf,
                            unfocusedContainerColor = c_surf,
                            focusedTextColor = c_bgtxt,
                            unfocusedTextColor = c_bgtxt,
                            focusedBorderColor = Color.Transparent, // Убираем рамку, если нужен стиль "капсулы"
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.padding(10.dp))
            }

            items(users.value) { users ->
                Column {
                    ItemUser(users, onClick = {

                        // Создаем или получаем индивидуальный чат
                        chatViewModel.getOrCreateIndividualChat(users.uid, users.name)

                        // Получаем ID чата
                        val chatId = viewModel.getIndividualChatId(users.uid)

                        // Переходим на экран сообщений
                        navController.navigate("chat/$chatId")
//                        navController.navigate("chat/${users.uid}")
                    })
                }
            }

        }

    }
}

@Composable
fun ItemUser(users: UserData, onClick: () -> Unit) {

    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary
    Row(
        modifier = Modifier

            .fillMaxWidth(0.9f)
            .height(75.dp)
            .padding(start = 2.dp)
            .clickable {
                onClick()
            }
            .clip(RoundedCornerShape(20.dp))
            .background(c_surf),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .size(59.dp)
                .background(c_accmin)//txtMainSelected)
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
//                    .padding(top = 150.dp, bottom = 30.dp)
                    .size(30.dp),
                painter = painterResource(R.drawable.user), contentDescription = null,
            )
//            Text(
//                text = users.name[0].uppercase(),
//                color = txtMainWhite,
//                fontSize = 30.sp,
//            )
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = users.name,
                fontSize = 20.sp,
                color = c_bgtxt
            )
            Text(
                text = '@'+users.userName,
                fontSize = 14.sp,
                color = c_surftxt
            )
        }
    }
    Spacer(modifier = Modifier.padding(4.dp))
}