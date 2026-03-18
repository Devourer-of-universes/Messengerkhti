package com.example.myapplication.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.bottomNavigation.BottomItem
import com.example.myapplication.screen.Chat.ChatScreen
import com.example.myapplication.screen.Contacts.ContactsScreen
import com.example.myapplication.screen.Profile.ProfileScreen
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.txtMainWhite


@Composable
fun AppScreen(navController: NavController) {
    Log.d("proverka","appScreen открылся")

    val navItems = listOf(
        BottomItem(title = "Контакты", iconId = Icons.Default.AccountCircle),
        BottomItem(title = "Чаты", iconId = Icons.Default.Home, badgeCount = 5),
        BottomItem(title = "Профиль", iconId = Icons.Default.Info)
    )

    var selectedIndexed by remember {
        mutableIntStateOf(1)
    }
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary     //- это акцент с прозрачностью 0.5
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = c_accmin,
                tonalElevation = 0.dp,

                ) {
                navItems.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent), //удаление рамки вокруг выбранного элемента
                        selected = selectedIndexed == index,
                        onClick = {
                            selectedIndexed = index
                        },
                        icon = {
                            BadgedBox(
                                badge =
                                    {
                                        if (navItem.badgeCount != 0)
                                            Badge {
                                                Text(text = navItem.badgeCount.toString())
                                            }
                                    }
                            ) {
                                if (selectedIndexed == index) {
                                    Box(modifier = Modifier.background(c_acc, shape = CircleShape)
                                        .size(70.dp),
                                        contentAlignment = Alignment.Center
                                        ){
                                        Icon(
                                            imageVector = navItem.iconId,
                                            contentDescription = "",

                                            tint = txtMainWhite
                                        )
                                    }

                                } else {
                                    Icon(
                                        imageVector = navItem.iconId,
                                        contentDescription = "",
                                        tint = txtMainWhite
                                    )
                                }
                            }

                        },
                        label = {
                            if (selectedIndexed == index) {
                                Text(
                                    color = txtMainWhite, text = navItem.title
                                )
                            } else {
                                Text(
                                    color = txtMainWhite, text = navItem.title
                                )
                            }

                        },

                        )
                }
            }
        },
        content = { innerPadding: PaddingValues -> //системный отступ для верхнего бара

            ContentScreen(modifier = Modifier
                .padding(innerPadding), selectedIndexed, navController as NavHostController
            )

        }
    )
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndexed: Int,
    navController: NavHostController
) {
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    Box(
        modifier = Modifier
            .background(c_bg)
    ){
        when (selectedIndexed) {
            0 -> ContactsScreen(modifier = modifier, navController)
            1 -> ChatScreen(modifier = modifier, navController)
            2 -> ProfileScreen(modifier = modifier, navController)
        }
    }

}