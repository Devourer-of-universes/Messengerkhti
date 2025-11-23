package com.example.myapplication.screen.Contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import com.example.myapplication.screen.Profile.HeaderProfile
//import com.example.myapplication.screen.Profile.InfoProfile
//import com.example.myapplication.screen.Profile.MenuApplication
import com.example.myapplication.ui.theme.bgGrey

@Composable
fun ContactsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier= modifier
            .fillMaxSize()
            .background(
                color = bgGrey
            ),

        ) {
        Text(text = "Контакты")

    }

}