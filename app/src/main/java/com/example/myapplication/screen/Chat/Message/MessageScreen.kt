package com.example.myapplication.screen.Chat.Message

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.DataMessanger
import com.example.myapplication.DataMessanger.chatName
import com.example.myapplication.R
import com.example.myapplication.model.Message
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.bgItemCurUser
import com.example.myapplication.ui.theme.bgItemSendUser
import com.example.myapplication.ui.theme.bgGreyLight
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun MessageScreen(navController: NavController, channelId: String) {

    val viewModel: MessageViewModel = hiltViewModel()
    val messages = viewModel.messages.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.listenForMessages(channelId)

    }




    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding: PaddingValues -> //системный отступ для верхнего бара

            ContentMessage(
                modifier = Modifier
                    .padding(innerPadding),
                messages, viewModel, channelId,
                navController as NavHostController
            )

        }
    )


}


@Composable
fun ContentMessage(
    modifier: Modifier = Modifier,
    messages: State<List<Message>>,
    viewModel: MessageViewModel,
    channelId: String,
    navController: NavHostController,
) {

    Box(
        modifier = Modifier
            .background(bgGreyDark)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = bgGrey
                ),

            ) {
            ChatMessages(
                messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelID = channelId, message)

                },
                navController = navController
            )
        }
    }

}

@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    navController: NavHostController,
) {
    val msg = remember {
        mutableStateOf("")
    }

    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row {
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
                        .align(
                            alignment = Alignment.CenterStart
                        )
//                            .padding(16.dp)
                        .size(50.dp),
                    onClick = {
                        navController.popBackStack()
                        chatName = ""
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }

                Text(
                    text = chatName,
                    fontSize = 25.sp,
                    color = txtMainWhite
                )
            }
        }

        LazyColumn(
            modifier = Modifier.padding(top = 50.dp, bottom = 55.dp),
            state = listState,
        ) {

            items(messages) { message ->
                ItemMessageOnChat(message = message)
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
//                .padding(8.dp)
                .background(bgGreyLight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                onClick = {

                },
            ) {
                Icon(
                    tint = txtMainSelected,
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "sendMessage"
                )
            }

            TextField(
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = bgGrey,
                    unfocusedContainerColor = bgGreyLight,
                    focusedTextColor = txtMainWhite,
                    focusedContainerColor = bgGreyLight,
                    focusedLabelColor = txtMainSelected,
                    unfocusedLabelColor = txtMainSelected,
                    cursorColor = txtMainSelected,
                    focusedIndicatorColor = txtMainSelected,

                    ),
                value = msg.value,
                onValueChange = {
                    msg.value = it
                },
                label = {
                    Text(
                        text = "Введите сообщение"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }
                )
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                onClick = {
                    onSendMessage(msg.value)
                    msg.value = ""
                },
            ) {
                Icon(
                    tint = txtMainSelected,
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "sendMessage"
                )
            }

        }
    }

}

@Composable
fun ItemMessageOnChat(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val colorItem = if (isCurrentUser) {
        bgItemCurUser
    } else {
        bgItemSendUser
    }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp * 3 / 4

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        val alignment = if (!isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd

        val date = Date(message.createdAT)
        val sdf = SimpleDateFormat("dd/MM/yy\nHH:mm ")
        val formattedDate = sdf.format(date)


        Row(
            modifier = Modifier
                .width(screenWidth)
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
                .align(alignment),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(50.dp),
                painter = painterResource(id = R.drawable.messenger_icon_round),
                contentDescription = ""
            )


            Column(
                modifier = Modifier
//                    .align(Alignment.TopStart)
                    .padding(start = 8.dp)
                    .background(colorItem, shape = RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = message.senderName,
                    color = txtMainWhite,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 10.dp,
                        end = 6.dp,
                        bottom = 5.dp
                    ),
                    fontSize = 20.sp
                )
                Text(
                    text = message.message,
                    color = txtMainWhite,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    fontSize = 15.sp,
                    lineHeight = 15.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp, bottom = 5.dp)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formattedDate,
                        color = bgGreyDark,
                        lineHeight = 11.sp,
                        fontSize = 10.sp
                    )
                }
            }


        }

    }


}