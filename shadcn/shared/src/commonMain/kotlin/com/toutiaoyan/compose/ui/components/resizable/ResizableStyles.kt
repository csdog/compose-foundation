@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.focused
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.style.ringShadow
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberResizableHandleStyle(horizontal: Boolean): Style =
    rememberTheme(horizontal) {
        Style {
            if (horizontal) {
                height(1f)
                width(1.dp)
            } else {
                width(1f)
                height(1.dp)
            }
            background(colors.border)
            shape(RectangleShape)
            focused {
                if (LocalInputModeManager.currentValue.inputMode == InputMode.Keyboard) {
                    dropShadow(ringShadow(colors.ring, 1.dp))
                }
            }
        }
    }

@Composable
fun rememberResizableGripStyle(horizontal: Boolean): Style =
    rememberTheme(horizontal) {
        Style {
            if (theme.activePreset == StylePreset.Nova) {
                width(if (horizontal) 4.dp else 24.dp)
                height(if (horizontal) 24.dp else 4.dp)
                shape(RoundedCornerShape(radius.lg))
            } else if (horizontal) {
                width(12.dp)
                height(16.dp)
            } else {
                width(16.dp)
                height(12.dp)
            }
            if (theme.activePreset != StylePreset.Nova) shape(RoundedCornerShape(radius.xs))
            background(colors.border)
        }
    }
