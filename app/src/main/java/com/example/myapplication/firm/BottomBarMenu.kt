package com.example.myapplication.firm

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.bottomNavigation.BottomItem
import com.example.myapplication.ui.theme.txtMainGreyLight
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun NavBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomItem(title = "Контакты", iconId = Icons.Default.AccountCircle, badgeCount = 0),
        BottomItem(title = "Чаты", iconId = Icons.Default.Home, badgeCount = 5),
        BottomItem(title = "Профиль", iconId = Icons.Default.Info, badgeCount = 0)
    )

    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val txtMainWhite = Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp)
            .height(70.dp)
    ) {
        // Фон
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 12.dp,
                    spotColor = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(24.dp))
                .background(c_accmin)
        )

        // Кнопки
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, navItem ->
                val isSelected = selectedIndex == index
                val hasBadge = navItem.badgeCount != null && navItem.badgeCount!! > 0

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            onClick = { onItemSelected(index) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Сначала индикатор (под контентом)
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    c_acc,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        )
                    }

                    // Контент поверх индикатора
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (hasBadge && !isSelected) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = Color.Red,
                                        contentColor = Color.White,
                                        modifier = Modifier.offset(x = 8.dp, y = (-4).dp)
                                    ) {
                                        Text(
                                            text = if (navItem.badgeCount!! > 99) "99+" else navItem.badgeCount.toString(),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = navItem.iconId,
                                    contentDescription = navItem.title,
                                    tint = if (isSelected) txtMainWhite else txtMainGreyLight,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = navItem.iconId,
                                contentDescription = navItem.title,
                                tint = if (isSelected) txtMainWhite else txtMainGreyLight,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = navItem.title,
                            color = if (isSelected) txtMainWhite else txtMainGreyLight,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}