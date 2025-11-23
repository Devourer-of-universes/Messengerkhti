package com.example.myapplication.screen.Login.signUp

@Composable
fun SignUpScreen(navController: NavHostController) {

    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirmPassword by remember {
        mutableStateOf("")
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGreyDark),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 150.dp)
        )


        Text(

            color = txtIcMainSelected,
            text = "Давайте начнем!",
            fontWeight = W700,
            fontSize = 22.sp
        )
        Spacer(
            modifier = Modifier
                .padding(top = 10.dp)
        )
        Text(
            color = txtIcMainSelected,
            text = "Зарегистрируйте свой новый аккаунт",
            fontWeight = W700,
            fontSize = 20.sp
        )

        var isError by remember {
            mutableStateOf(false)
        }
        isError = password != confirmPassword

        Spacer(
            modifier = Modifier
                .padding(top = 100.dp)
        )

        FirmOutlineTextField(
            label = "Имя пользователя",
            value = { name = it },
            paddingTop = 10.dp
        )
        FirmOutlineTextField(
            label = "Электронная почта",
            value = { email = it },
            paddingTop = 10.dp
        )
        FirmOutlineTextField(
            label = "Пароль",
            value = { password = it },
            paddingTop = 10.dp,
            password = true
        )
        FirmOutlineTextField(
            label = "Подтвердите пароль",
            value = { confirmPassword = it },
            paddingTop = 10.dp,
            password = true,
            error = isError
        )

        if (uiState.value == SignUpState.Loading) {
            CircularProgressIndicator(
                color = txtIcMainSelected,
                modifier = Modifier
                    .padding(top = 40.dp)
            )
        } else {
            Button(
                modifier = Modifier
                    .padding(top = 40.dp),
                colors = btnMainOrange,
                onClick = {
                    viewModel.signUp(
                        name = name,
                        email = email,
                        password = password
                    )
                },
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 40.dp, vertical = 5.dp),
                    fontWeight = W700,
                    fontSize = 18.sp,
                    text = "Регистрация"
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                color = txtIcMainGrey,
                text = "У вас есть аккаунт?"
            )
            TextButton(onClick = { navController.popBackStack() }) {
                Text(fontWeight = W700, color = txtIcMainGrey, text = "Авторизация")
            }
        }
    }
}