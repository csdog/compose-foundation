@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

@PreviewTest
@AndroidScreenshotConfigurations
@Composable
fun ButtonShapesPreview() {
    ScreenshotSurface {
        Text { "Buttons and shape overrides" }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(text = "Primary")
            Button(text = "Outline", variant = ButtonVariant.Outline)
            Button(text = "Disabled", enabled = false)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(text = "+", size = ButtonSize.Icon, style = Style { shape(CircleShape) })
            Button(text = "Asymmetric", style = Style { shape(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)) })
            Button(text = "Ghost", variant = ButtonVariant.Ghost)
        }
    }
}

@PreviewTest
@AndroidScreenshotConfigurations
@Composable
fun FormStatesPreview() {
    ScreenshotSurface {
        Text { "Form fields and selected states" }
        Input(value = "hello@example.com", onValueChange = {})
        Input(value = "", onValueChange = {}, placeholder = "Email address")
        Checkbox(text = "Receive notifications", checked = true, onCheckedChange = {})
        Checkbox(text = "Optional setting", checked = false, onCheckedChange = {})
        Checkbox(text = "Disabled setting", checked = true, enabled = false)
    }
}

@Composable
private fun ScreenshotSurface(content: @Composable () -> Unit) {
    Theme(preset = StylePreset.Nova, isDark = isSystemInDarkTheme()) {
        Column(
            modifier = Modifier.fillMaxWidth().background(currentTheme.colors.background).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) { content() }
    }
}
