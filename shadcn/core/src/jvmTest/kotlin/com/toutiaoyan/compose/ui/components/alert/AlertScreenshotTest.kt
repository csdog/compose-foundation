@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import kotlin.test.Test

class AlertScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("alert_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Alert(title = "Heads up!", description = "You can add components to your app.")
                Alert(
                    variant = AlertVariant.Destructive,
                    title = "Error",
                    description = "Your session has expired. Please log in again.",
                )
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun action(darkTheme: Boolean) {
        snapshot("alert_action", darkTheme = darkTheme) {
            Alert(modifier = Modifier.width(400.dp)) {
                AlertTitle("Dark mode is now available")
                AlertDescription("Enable it under your profile settings to get started.")
                AlertAction {
                    Button("Enable", onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline)
                }
            }
        }
    }

    @Test
    fun action_light() = action(darkTheme = false)

    @Test
    fun action_dark() = action(darkTheme = true)
}
