package com.example.myapplication.screen.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.DataMessanger.chatName
import com.example.myapplication.model.Channel
import com.example.myapplication.model.indivMessage
import com.example.myapplication.ui.theme.txtMainWhite


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(modifier: Modifier = Modifier,
               navController: NavController,
) {
    val viewModel = hiltViewModel<ChatViewModel>()
    val channels = viewModel.channels.collectAsState()
    val individualMessages = viewModel.individualMessages.collectAsState()
    val addChannel = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    val isActiveChannel = remember {
        mutableStateOf(true)
    }
    val isActivePrivate = remember {
        mutableStateOf(true)
    }
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary     //- это акцент с прозрачностью 0.5
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
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(30.dp)
//                        .background(bgGreyDark)
//                        .padding(top = 5.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "ЧАТЫ",
//                        fontSize = 25.sp,
//                        color = txtMainWhite
//                    )
//
//                }
//            }
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
                                text = "Поиск чата...",
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
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    val colorSelected = c_acc //selected
                    val colorUnSelected = c_surftxt


                    if (isActiveChannel.value == true) {
                        TextButton(onClick = { isActiveChannel.value = true; isActivePrivate.value = false }) {
                            Text(
                                fontWeight = W700,
                                color = colorSelected,
                                text = "Каналы",
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { isActiveChannel.value = false; isActivePrivate.value = true}) {
                            Text(
                                fontWeight = W700,
                                color = colorUnSelected,
                                text = "Проекты",
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { isActiveChannel.value = false; isActivePrivate.value = false }) {
                            Text(
                                fontWeight = W700,
                                color = colorUnSelected,
                                text = "Личные",
                                fontSize = 14.sp
                            )
                        }
                    } else if(isActivePrivate.value == true){
                        TextButton(onClick = { isActiveChannel.value = true; isActivePrivate.value = false }) {
                            Text(
                                fontWeight = W700,
                                color = colorUnSelected,
                                text = "Каналы",
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { isActiveChannel.value = false; isActivePrivate.value = true}) {
                            Text(
                                fontWeight = W700,
                                color = colorSelected,
                                text = "Проекты",
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { isActiveChannel.value = false; isActivePrivate.value = false }) {
                            Text(
                                fontWeight = W700,
                                color = colorUnSelected,
                                text = "Личные",
                                fontSize = 14.sp
                            )
                        }
                    }else{
                        TextButton(onClick = { isActiveChannel.value = true; isActivePrivate.value = false }) {
                            Text(
                                fontWeight = W700,
                                color = colorUnSelected,
                                text = "Каналы",
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { isActiveChannel.value = false; isActivePrivate.value = true}) {
                            Text(
                                fontWeight = W700,
                                color = colorUnSelected,
                                text = "Проекты",
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { isActiveChannel.value = false; isActivePrivate.value = false }) {
                            Text(
                                fontWeight = W700,
                                color = colorSelected,
                                text = "Личные",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }


//            item {
//                HorizontalDivider(
//                    color = c_bgtxt, //bgGreyLight,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(2.dp)
//                )
//            }
            if (isActiveChannel.value) {
                items(channels.value) { channel ->
                    Column {
                        ItemChatChannel(channel, onClick = {
                            navController.navigate(
                                "chat/${channel.id}"
                            )
                            chatName = channel.name
                        })
                    }
                }
            } else if(isActivePrivate.value){
                items(channels.value) { channel ->
                    Column {
                        ItemChatProject(channel, onClick = {
                            navController.navigate(
                                "chat/${channel.id}"
                            )
                            chatName = channel.name
                        })
                    }
                }
            }else{
                items(individualMessages.value) { inMessage ->
                    Column {
                        ItemChatIndMassage(inMessage, onClick = {
                            navController.navigate(
                                "chat/${inMessage.id}"
                            )
                            chatName = inMessage.name
                        })
                    }
                }
            }


        }
        if (isActiveChannel.value) {
            FloatingActionButton(
                contentColor = c_bgtxt,
                containerColor = c_acc,
                shape = CircleShape,
                modifier = modifier
                    .align(
                        alignment = Alignment.BottomEnd
                    )
                    .padding(16.dp)
                    .size(50.dp),
                onClick = {
                    addChannel.value = true
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = ""
                )
            }
        }
    }

    if (addChannel.value) {
        ModalBottomSheet(
            containerColor = c_surf,

            onDismissRequest = { addChannel.value = false },
            sheetState = sheetState
        ) {
            AddChannelDialog {
                viewModel.addChannel(it)
                addChannel.value = false
            }
        }
    }

}



@Composable
fun ItemChatChannel(channel: Channel, onClick: () -> Unit) {

//    val date = Date(channel.createdAT)
//    val sdf = SimpleDateFormat("dd/MM/yy HH:mm ")
//    val formattedDate = sdf.format(date)
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
            modifier = Modifier.padding(start = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .size(59.dp)
                .background(c_accmin)//txtMainSelected)
                ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channel.name[0].uppercase(),
                color = c_bgtxt, //txtMainWhite,
                fontSize = 30.sp,
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = channel.name,
                fontSize = 20.sp,
                color = c_bgtxt //txtMainWhite
            )
            Text(
                text = "Иван: ну и бредятина...",
                fontSize = 14.sp,
                color = c_surftxt
            )
//            Text(
//                text = formattedDate,
//                fontSize = 16.sp,
//                color = txtMainWhite
//            )
        }
        Box(modifier = Modifier.padding(bottom = 40.dp, start = 75.dp)){//костыль бахнуть с добавлением пробелов хехе
            Box(
                modifier = Modifier.clip(CircleShape)
                    .size(24.dp)
                    .background(color = c_acc),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "6",
                    fontSize = 20.sp,
                    color = txtMainWhite,

                    )
            }
        }

    }
    Spacer(modifier = Modifier.padding(4.dp))
}

@Composable
fun ItemChatProject(channel: Channel, onClick: () -> Unit) {

//    val date = Date(channel.createdAT)
//    val sdf = SimpleDateFormat("dd/MM/yy HH:mm ")
//    val formattedDate = sdf.format(date)
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary
    Row(
        modifier = Modifier
            .background(c_surf)
            .fillMaxWidth(0.9f)
            .height(75.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                onClick()
            }
            .clip(RoundedCornerShape(24.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .size(50.dp)
                .background(c_accmin)//txtMainSelected)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channel.name[0].uppercase(),
                color = c_bgtxt, //txtMainWhite,
                fontSize = 30.sp,
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = channel.name,
                fontSize = 20.sp,
                color = c_bgtxt //txtMainWhite
            )
//            Text(
//                text = formattedDate,
//                fontSize = 16.sp,
//                color = txtMainWhite
//            )
        }
    }
    HorizontalDivider(
        color = c_surftxt, //bgGreyLight,
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    )
}

@Composable
fun ItemChatIndMassage(indivMessage: indivMessage, onClick: () -> Unit) {

//    val date = Date(channel.createdAT)
//    val sdf = SimpleDateFormat("dd/MM/yy HH:mm ")
//    val formattedDate = sdf.format(date)
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary
    Row(
        modifier = Modifier
            .background(color = c_surf)
            .fillMaxWidth()
            .height(75.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
                .background(c_acc)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = indivMessage.name[0].uppercase(),
                color = c_bgtxt,//txtMainWhite,
                fontSize = 30.sp,
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = indivMessage.name,
                fontSize = 20.sp,
                color = c_bgtxt //txtMainWhite
            )
//            Text(
//                text = formattedDate,
//                fontSize = 16.sp,
//                color = txtMainWhite
//            )
        }
    }
//    HorizontalDivider(
//        color = c_surftxt,//= bgGreyLight,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(2.dp)
//    )
}

@Composable
fun AddChannelDialog(onAddChannel: (String) -> Unit) {
    val channelName = remember {
        mutableStateOf("")
    }
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон //bgGrey
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный //txtMainWhite
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее).bgGreyLight
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст txtMainSelected)
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет /btnMainOrange
    val c_accmin = MaterialTheme.colorScheme.secondary
    Column(

        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Добавить канал",
            color = c_bgtxt, //txtMainWhite,
            fontSize = 25.sp
        )
        Spacer(
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = c_bg, //bgGrey,
                unfocusedContainerColor = c_surf,
                focusedTextColor = c_bgtxt,
                focusedContainerColor = c_surf,
                focusedLabelColor = c_bgtxt,
                unfocusedLabelColor = c_bgtxt,
                cursorColor = c_surftxt,
                focusedIndicatorColor = c_surftxt,

                ),
            value = channelName.value,
            onValueChange = {
                channelName.value = it
            },
            label = {
                Text(
                    text = "Название канала"
                )
            },
            singleLine = true,
        )

        Spacer(
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = {
                onAddChannel(channelName.value)

            },
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 5.dp),
            colors = ButtonDefaults.buttonColors(
                c_acc
            )//btnMainOrange
        ) {
            Text(text = "Добавить")
        }

    }
}

