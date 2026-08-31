@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.separator
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

enum class SeparatorOrientation { Horizontal, Vertical }

@Composable
fun SeparatorOrientation.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            SeparatorOrientation.Horizontal ->
                Style {
                    width(1f)
                    height(1.dp)
                    background(colors.border)
                }

            SeparatorOrientation.Vertical ->
                Style {
                    height(1f)
                    width(1.dp)
                    background(colors.border)
                }
        }
    }
}
