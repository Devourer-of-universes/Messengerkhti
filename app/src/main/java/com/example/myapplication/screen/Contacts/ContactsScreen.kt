package com.example.myapplication.screen.Contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
//import com.example.myapplication.screen.Profile.HeaderProfile
//import com.example.myapplication.screen.Profile.InfoProfile
//import com.example.myapplication.screen.Profile.MenuApplication
import com.example.myapplication.ui.theme.bgGrey
import com.example.myapplication.ui.theme.bgGreyLight
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun ContactsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier= modifier
            .fillMaxSize()
            .background(
                color = bgGrey
            ),

        ) {
        Text(modifier = Modifier.fillMaxWidth()
            .padding(top = 37.dp, bottom = 37.dp),
            text = "Контакты",
            color = txtMainSelected,
            fontSize = 25.sp,
            textAlign = TextAlign.Center)
        HorizontalDivider(
            color = bgGreyLight,
            thickness = 1.dp,)
        ContactElement(1)

    }

}

@Composable
fun ContactElement(contact_id: Int){
    Column() {
        Row(
            modifier = Modifier
                .background(color = bgGreyLight)
                .fillMaxWidth()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.accont_profileavatar),
                contentDescription = "icon_profile",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(80.dp)
                    .padding(start = 12.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            )
            Column() {
                Text(
                    text = "Денис Шпигальский",
                    color = txtMainWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold

                )
                Text(
                    text = "в сети: час назад",
                    color = txtMainWhite,
                    fontSize = 20.sp
                )
            }
        }
        HorizontalDivider(
            color = bgGreyLight,
            thickness = 1.dp,)
    }

}