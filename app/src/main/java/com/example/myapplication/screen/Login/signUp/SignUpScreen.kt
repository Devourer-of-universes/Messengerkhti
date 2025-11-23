package com.example.myapplication.screen.Login.signUp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.bgGreyDark
import com.example.myapplication.ui.theme.txtGreyLight
import com.example.myapplication.ui.theme.txtMainSelected
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun SignUpScreen(navController: NavHostController) {

    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when (uiState.value) {
            is SignUpState.Success -> {
                navController.navigate(route = "app")
            }

            is SignUpState.Error -> {
                Toast.makeText(context, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
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
            text = "Давайте начнём!",
            color = txtMainSelected,
            fontSize = 20.sp
        )
        Text(
            text = "Зарегистрируйте свой новый аккаунт",
            color = txtMainSelected,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(50.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = {Text("Имя пользователя", color = txtGreyLight)},
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
                .fillMaxWidth(0.8f)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor= txtMainSelected,
                unfocusedBorderColor = txtMainSelected,

                )
        )
        OutlinedTextField(
            value = passwordRepeat,
            onValueChange = { passwordRepeat = it },
            placeholder = {Text("Повторить пароль", color = txtGreyLight)},
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
            text = "Создавая учётную запись вы соглашаетесь с нашими условиями использования и политикой конфиденциальности",
            color = txtGreyLight,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 40.dp, top = 10.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier.size(width = 220.dp, height = 48.dp).padding(bottom = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = txtMainSelected
            )
        ) {
            Text(
                text = "Создать аккаунт",
                fontSize = 18.sp,
                color = txtMainWhite
            )
        }
        Text(
            text = "У вас есть учётная запись? Авторизация",
            color = txtGreyLight,
            fontSize = 12.sp
        )
    }
}
