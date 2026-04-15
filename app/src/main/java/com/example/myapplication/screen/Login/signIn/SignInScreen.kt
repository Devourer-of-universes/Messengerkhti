package com.example.myapplication.screen.Login.signIn

import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.bgMainDarkTheme
import com.example.myapplication.ui.theme.bgSecDarkTheme
import com.example.myapplication.ui.theme.txtMainGrey

import com.example.myapplication.ui.theme.txtMainWhite


@Composable
fun SignInScreen(navController: NavController) {
    Log.d("proverka", "signinScreen открылся")
    val viewModel: SignInViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Обработка состояния
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            SignInState.Success -> {
                Toast.makeText(context, "Вход выполнен", Toast.LENGTH_SHORT).show()
                navController.navigate("app") {
                    popUpTo("signin") { inclusive = true }
                }
            }
            is SignInState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = bgSecDarkTheme)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(50.dp))

        Text(
            text = "Добро пожаловать!",
            color = txtMainGrey,
            fontSize = 20.sp
        )
        Text(
            text = "Войдите в свой аккаунт",
            color = txtMainGrey,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.padding(50.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Электронная почта", color = txtMainGrey) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                errorTextColor = Color.Red,
                focusedTextColor = txtMainWhite,
                unfocusedTextColor = txtMainWhite,
                focusedBorderColor = txtMainGrey,
                unfocusedBorderColor = bgMainDarkTheme,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Пароль", color = txtMainGrey) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                errorTextColor = Color.Red,
                focusedTextColor = txtMainWhite,
                unfocusedTextColor = txtMainWhite,
                focusedBorderColor = txtMainGrey,
                unfocusedBorderColor = bgMainDarkTheme,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Text(
            text = "Забыли пароль?",
            color = bgSecDarkTheme,
            fontSize = 12.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 40.dp)
        )

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.signIn(email = email, password = password)
                } else {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .size(width = 220.dp, height = 48.dp)
                .padding(bottom = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = txtMainGrey),
            enabled = uiState != SignInState.Loading
        ) {
            if (uiState == SignInState.Loading) {
                // Можно добавить CircularProgressIndicator
                Text(text = "Загрузка...", fontSize = 18.sp, color = txtMainWhite)
            } else {
                Text(text = "Авторизация", fontSize = 18.sp, color = txtMainWhite)
            }
        }

        Text(
            text = "У вас нет учётной записи?",
            color = txtMainGrey,
            fontSize = 12.sp
        )

        TextButton(onClick = { navController.navigate("signup") }) {
            Text(
                fontWeight = W700,
                color = bgSecDarkTheme,
                text = "Регистрация"
            )
        }
    }
}