package com.example.myapplication.screen.Contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.navigation.NavHostController
import com.example.myapplication.DataMessanger.chatName
import com.example.myapplication.R
import com.example.myapplication.firm.FirmOutlineTextField
import com.example.myapplication.model.Channel
import com.example.myapplication.model.UserData
import com.example.myapplication.screen.Chat.AddChannelDialog
import com.example.myapplication.screen.Chat.ChatViewModel
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.bgGreyLight
import com.example.myapplication.ui.theme.txtGreyLight
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun ContactsScreen(modifier: Modifier = Modifier,  navController: NavHostController) {
    val viewModel = hiltViewModel<ContactsScreenViewModel>()
    val chatViewModel = hiltViewModel<ChatViewModel>() // Получаем ChatViewModel
    val users = viewModel.users.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()

    ) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = bgGrey
                ),

            ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(bgGreyDark)
                        .padding(top = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ЧАТЫ",
                        fontSize = 25.sp,
                        color = txtMainWhite
                    )

                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .height(50.dp)
                        .background(bgGreyDark)
                        .padding(bottom = 10.dp, top = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var index by remember {
                        mutableStateOf("")
                    }
                    OutlinedTextField(
                        value = index,
                        onValueChange = { index = it },
                        placeholder = {Text("Поиск контакта...", color = txtGreyLight)},
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            errorTextColor = Color.Red,
                            focusedTextColor = txtMainWhite,
                            focusedBorderColor = txtMainSelected,
                            unfocusedBorderColor = bgGrey,
                            unfocusedTextColor = txtMainWhite
                        )
                    )
                }
            }
            item {
                HorizontalDivider(
                    color = bgGreyLight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
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

    Row(
        modifier = Modifier
            .background(bgGreyDark)
            .fillMaxWidth()
            .height(75.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
                .background(txtMainSelected)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
//                    .padding(top = 150.dp, bottom = 30.dp)
                    .size(30.dp),
                painter = painterResource(R.drawable.messenger_icon_round), contentDescription = null,
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
                color = txtMainWhite
            )
//            Text(
//                text = formattedDate,
//                fontSize = 16.sp,
//                color = txtMainWhite
//            )
        }
    }
    HorizontalDivider(
        color = bgGreyLight,
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    )
}
