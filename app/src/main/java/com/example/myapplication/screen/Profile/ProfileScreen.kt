package com.example.myapplication.screen.Profile

import com.example.myapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.txtGreyLight
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite


@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    Column(modifier = Modifier
        .background(color = bgGreyDark)
        .fillMaxSize()
        .padding(top = 30.dp)) {
        Header()
        AccountBaseInfo()
        AccountSettings()
        Spacer(modifier = Modifier.height(15.dp))
        Column (modifier = Modifier
            .background(color = bgGrey)
            .fillMaxSize()

        ){  }

    }


}
@Composable
fun Header(){
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(bgGreyDark)
            .fillMaxWidth()
            .height(220.dp)
    ){

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 140.dp, top = 33.dp)){
            Image (
                painter = painterResource(R.drawable.accont_profileavatar),
                contentDescription = "avatar",
                modifier =  Modifier
                    .size(120.dp)
                    .padding(bottom = 10.dp)
            )

            Text(
                text = "Денис Шпигальский",
                color = txtMainWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                softWrap = true,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(150.dp)
            )
        }
        Box(modifier = Modifier.padding(top = 12.dp, end = 12.dp)){
            Image(
                painter = painterResource(R.drawable.account_exit_button),
                contentDescription = "exit button",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
@Composable
fun AccountBaseInfo(){
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(bgGrey)
                .fillMaxWidth()
                .padding(start = 43.dp, end = 10.dp)
                .height(53.dp)
        ) {
            Text(
                text = "Аккаунт",
                color = txtMainWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Image(
                painter = painterResource(R.drawable.account_edit_button),
                contentDescription = "edit account button",
                Modifier.size(30.dp)
            )
        }
        AccountBaseInfoString("Номер телефона", "+7 906 191-18-87")
        AccountBaseInfoString("Имя пользователя", "@barbarisdance")
    }
}
@Composable
fun AccountBaseInfoString(header : String, content : String){
    Row (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 35.dp, end = 35.dp, top = 15.dp).fillMaxWidth()){
        Text(
            text = header,
            color = txtMainWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = content,
            color = txtMainSelected
        )
    }
}
@Composable
fun AccountSettings(){
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(top = 15.dp)
    ){
        Row (
//            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(bgGrey)
                .fillMaxWidth()
                .padding(start = 43.dp, end = 10.dp)
                .height(53.dp)
        ){
            Text(
                text = "Настройки",
                color = txtMainWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )  }

        AccountSettingsString(R.drawable.account_chatset, "Чаты")
        AccountSettingsString(R.drawable.account_bell, "Уведомления")
        AccountSettingsString(R.drawable.account_lock, "Конфиденциальность")
        AccountSettingsString(R.drawable.account_info, "О приложении")
    }
}
@Composable
fun AccountSettingsString(imageway : Int, settingsection : String){
    Row (
        modifier = Modifier.padding(start = 35.dp, end = 35.dp, top = 15.dp).fillMaxWidth()
    ){
        Image(
            painter = painterResource(imageway),
            contentDescription = "settings icon",
            modifier = Modifier.padding(end = 20.dp).size(30.dp)
        )
        Text(
            text = settingsection,
            color = txtMainWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
//@Composable
//fun BottomMenu(){
//    Row (modifier = Modifier
//        .fillMaxWidth()
//        .background(bgGreyDark)){
//        Column (modifier = Modifier.fillMaxWidth(0.33f).padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally){
//            Image(
//                modifier = Modifier.size(30.dp),
//                painter = painterResource(R.drawable.account_human),
//                contentDescription = "contacts"
//            )
//            Text(
//                text = "Контакты",
//                color = txtGreyLight,
//                fontSize = 10.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        Column (modifier = Modifier.fillMaxWidth(0.5f).padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally){
//            Image(
//                modifier = Modifier.size(30.dp),
//                painter = painterResource(R.drawable.account_chatsmenu),
//                contentDescription = "chats"
//            )
//            Text(
//                text = "Чаты",
//                color = txtGreyLight,
//                fontSize = 10.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        Column (modifier = Modifier.fillMaxWidth(1.0f).padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally){
//            Image(
//                modifier = Modifier.size(30.dp)
//                    .border(width = 2.dp,txtMainSelected, shape = CircleShape),
//                painter = painterResource(R.drawable.account_denis),
//                contentDescription = "profile"
//            )
//            Text(
//                text = "Денис",
//                color = txtMainSelected,
//                fontSize = 10.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}