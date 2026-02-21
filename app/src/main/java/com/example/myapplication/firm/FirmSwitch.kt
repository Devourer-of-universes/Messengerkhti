package com.example.myapplication.firm

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SettingsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String? = null,
    description: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val accentColor = MaterialTheme.colorScheme.primary
    val disabledColor = Color.Gray.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                onCheckedChange(!checked)
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Левая часть с текстом
        if (title != null) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )

                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = if (enabled)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Кастомный переключатель
        CustomSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            accentColor = accentColor,
            disabledColor = disabledColor
        )
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    accentColor: Color,
    disabledColor: Color
) {
    val width = 52.dp
    val height = 32.dp
    var internalChecked by remember { mutableStateOf(checked) }

    // Анимация цвета
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> disabledColor.copy(alpha = 0.1f)
            internalChecked -> accentColor.copy(alpha = 0.5f)
            else -> Color.LightGray.copy(alpha = 0.3f)
        },
        animationSpec = tween(200)
    )

    // Анимация положения кружка
    val circleOffset by animateDpAsState(
        targetValue = if (internalChecked) {
            width - height + 2.dp
        } else {
            2.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(backgroundColor)
            .clickable(enabled) {
                internalChecked = !internalChecked
                onCheckedChange(internalChecked)
            }
    ) {
        // Кружок-ползунок
        Box(
            modifier = Modifier
                .size(height - 4.dp)
                .offset(x = circleOffset)
                .align(Alignment.CenterStart)
                .clip(CircleShape)
                .background(
                    when {
                        !enabled -> disabledColor
                        internalChecked -> accentColor
                        else -> Color.White
                    }
                )
                .shadow(
                    elevation = if (enabled && internalChecked) 4.dp else 2.dp,
                    shape = CircleShape
                )
        )

    }
}

//// Упрощенная версия для частого использования
//@Composable
//fun SettingsSwitch(
//    title: String,
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    SettingsSwitch(
//        title = title,
//        description = null,
//        checked = checked,
//        onCheckedChange = onCheckedChange,
//        modifier = modifier
//    )
//}