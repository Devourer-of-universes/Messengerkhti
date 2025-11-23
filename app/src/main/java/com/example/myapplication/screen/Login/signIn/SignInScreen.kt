package com.example.myapplication.screen.Login.signIn

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.txtGreyLight
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite


@Composable
fun SignInScreen(navController: NavHostController){
    val viewModel: SignInViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value){
            is SignInState.Success -> {
                navController.navigate(route = "app")
            }

            is SignInState.Error -> {
                Toast.makeText(context, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
            }
            else -> {}

        }

    }
    Column (modifier = Modifier
        .fillMaxWidth()
        .background(color = bgGreyDark)
        .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Spacer(modifier = Modifier.padding(50.dp))
        Text(
            text = "Добро пожаловать!",
            color = txtMainSelected,
            fontSize = 20.sp
        )
        Text(
            text = "Войдите в свой аккаунт",
            color = txtMainSelected,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(50.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {Text("Электронная почта", color = txtGreyLight)},
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor= txtMainSelected,
                unfocusedBorderColor = txtMainSelected
            )
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {Text("Пароль", color = txtGreyLight)},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor= txtMainSelected,
                unfocusedBorderColor = txtMainSelected,

                )
        )
        Text(
            text = "Забыли пароль?",
            color = txtGreyLight,
            fontSize = 12.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 40.dp)

        )
        Button(
            onClick = {},
            modifier = Modifier.size(width = 220.dp, height = 48.dp).padding(bottom = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = txtMainSelected
            )
        ) {
            Text(
                text = "Авторизация",
                fontSize = 18.sp,
                color = txtMainWhite
            )
        }
        Text(
            text = "У вас нет учётной записи? Регистрация",
            color = txtGreyLight,
            fontSize = 12.sp
        )
    }
}
