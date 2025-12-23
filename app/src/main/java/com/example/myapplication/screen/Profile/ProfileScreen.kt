package com.example.myapplication.screen.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.DataMessanger.chatName
import com.example.myapplication.R
import com.example.myapplication.model.UserData
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.bgGreyLight
import com.example.myapplication.ui.theme.btnMainOrange
import com.example.myapplication.ui.theme.txtGreyLight
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    val viewModel: ProfileScreenViewModel = hiltViewModel()
    val userData = viewModel.userData.collectAsState()

    val isChangePhone = remember {
        mutableStateOf(false)
    }
    val isChangeUsername = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(key1 = true) {
        viewModel.initProfile()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = bgGrey
            ),

        ) {

        HeaderProfile(navController, userData)

        InfoProfile(
            userData,
            onPhoneClick = {
                isChangePhone.value = true
            },  // При клике на телефон — открываем диалог изменения телефона
            onUsernameClick = {
                isChangeUsername.value = true
            }  // При клике на имя пользователя — открываем диалог изменения имени)
        )
        MenuApplication(navController)

    }

    if (isChangePhone.value) {
        ModalBottomSheet(
            containerColor = bgGreyDark,
            modifier = Modifier
                .background(txtMainWhite),
            onDismissRequest = { isChangePhone.value = false },
            sheetState = sheetState
        ) {
            ChangePhoneDialog {
                viewModel.changePhone(it)
                isChangePhone.value = false
            }
        }
    }

    if (isChangeUsername.value) {
        ModalBottomSheet(
            containerColor = bgGreyDark,
            modifier = Modifier,
            onDismissRequest = { isChangeUsername.value = false },
            sheetState = sheetState
        ) {
            ChangeUsernameDialog {
                viewModel.changeUsername(it)
                isChangeUsername.value = false
            }
        }
    }
}

@Composable
fun HeaderProfile(navController: NavHostController, userData: State<UserData>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = bgGreyDark
            )
            .height(175.dp)

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.accont_profileavatar),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .clickable(
                            onClick = {},
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() }
                        ),

                    )
                Spacer(
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = userData.value.name,
                    color = txtMainWhite,
                    fontSize = 20.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(R.drawable.account_logout),
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp)
                    .clickable(
                        onClick = {
                            Firebase.auth.signOut()
                            navController.navigate(route = "login")
                        },
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() }
                    ),

                )
        }


    }

}

@Composable
fun InfoProfile(
    userData: State<UserData>,
    onPhoneClick: () -> Unit,
    onUsernameClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Аккаунт",
                color = txtMainWhite,
                fontSize = 20.sp,
                fontWeight = W700
            )

            Image(
                painter = painterResource(R.drawable.account_edit_button),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        onClick = {},
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

        }

        val tdxDataPhone = if(userData.value.phone == "Введите телефон") "Введите телефон" else "+7 ${userData.value.phone}"

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, color = bgGreyDark)
                .background(
                    color = bgGreyDark
                )
                .padding(vertical = 10.dp)
        ) {
            ProfileRowAccounts(
                txtHeader = "Номер телефона",
                txtData = tdxDataPhone,
                onClick = onPhoneClick
            )
            ProfileRowAccounts(
                txtHeader = "Имя пользователя",
                txtData = userData.value.userName,
                onClick = onUsernameClick
            )
        }
    }
}

@Composable
fun ProfileRowAccounts(
    txtHeader: String,
    txtData: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = txtHeader,
            color = txtMainWhite,
            fontSize = 16.sp,
        )

        TextButton(
            onClick = onClick
        ) {
            Text(
                text = txtData,
                color = txtMainSelected,
                fontSize = 16.sp,
                fontWeight = W500,
            )
        }
    }
}

@Composable
fun MenuApplication(navController: NavHostController) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Настройки",
                color = txtMainWhite,
                fontSize = 20.sp,
                fontWeight = W700
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, color = bgGreyDark)
                .background(
                    color = bgGreyDark
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            navController.navigate(
                                "appinfo"
                            )
                        },
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .padding(horizontal = 25.dp, vertical = 12.5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.account_info),
                    contentDescription = "",
                    modifier = Modifier
                        .size(35.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "О приложении",
                    color = txtMainWhite,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

//@Composable
//fun ProfileRowSettings(text: String, idPainterResource: Int,navController: NavHostController, paddingHorizontalRow: Dp = 25.dp, ) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(
//                onClick = {
//                    navController.navigate(
//                        "appinfo"
//
//                    )
//                },
//                indication = ripple(),
//                interactionSource = remember { MutableInteractionSource() }
//            )
//            .padding(horizontal = paddingHorizontalRow, vertical = 12.5.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Image(
//            painter = painterResource(idPainterResource),
//            contentDescription = "",
//            modifier = Modifier
//                .size(35.dp)
//        )
//        Spacer(modifier = Modifier.size(10.dp))
//        Text(
//            text = text,
//            color = txtMainWhite,
//            fontSize = 18.sp,
//        )
//    }
//}


@Composable
fun ChangePhoneDialog(onAddChannel: (String) -> Unit) {
    val phoneNumber = remember {
        mutableStateOf("")
    }
    Column(

        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Изменить телефон",
            color = txtGreyLight,
            fontSize = 25.sp
        )
        Spacer(
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = bgGrey,
                unfocusedContainerColor = bgGreyLight,
                focusedTextColor = txtMainWhite,
                focusedContainerColor = bgGreyLight,
                focusedLabelColor = txtGreyLight,
                unfocusedLabelColor = txtGreyLight,
                cursorColor = txtMainSelected,
                focusedIndicatorColor = txtMainSelected,

                ),
            value = phoneNumber.value,
            onValueChange = {
//                phoneNumber.value = it.filter { char -> char.isDigit() }.take(10)
                phoneNumber.value = it
                    .filter { it.isDigit() }
                    .take(10)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            visualTransformation = PhoneNumberVisualTransformation(),
            placeholder = { Text("Введите номер без +7") },
            label = {
                Text(
                    text = "Телефон"
                )
            },
            singleLine = true,
        )

        Spacer(
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = {
                onAddChannel(phoneNumber.value)
            },
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 5.dp),
            colors = btnMainOrange,
            enabled = phoneNumber.value.length == 10

        ) {
            Text(text = "Изменить")
        }

    }
}


@Composable
fun ChangeUsernameDialog(onAddChannel: (String) -> Unit) {
    val userName = remember {
        mutableStateOf("")
    }
    Column(

        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Изменить имя пользователя",
            color = txtGreyLight,
            fontSize = 25.sp
        )
        Spacer(
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = bgGrey,
                unfocusedContainerColor = bgGreyLight,
                focusedTextColor = txtMainWhite,
                focusedContainerColor = bgGreyLight,
                focusedLabelColor = txtGreyLight,
                unfocusedLabelColor = txtGreyLight,
                cursorColor = txtMainSelected,
                focusedIndicatorColor = txtMainSelected,

                ),
            value = userName.value,
            onValueChange = {
                userName.value = it
                    .take(25)
            },
            label = {
                Text(
                    text = "Имя пользователя"
                )
            },
            singleLine = true,
        )

        Spacer(
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = {
                onAddChannel(userName.value)
            },
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 5.dp),
            colors = btnMainOrange,
            enabled = userName.value.length >= 3
        ) {
            Text(text = "Изменить")
        }

    }
}


