@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class AlertNovaActionScreenshotTest : ScreenshotTest() {
    @Test fun actionsLight() = actions(false)

    @Test fun actionsDark() = actions(true)

    private fun actions(dark: Boolean) =
        snapshot("alert_nova_actions", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Alert {
                    AlertTitle("Action available")
                    AlertDescription("Choose the compact action.")
                    AlertAction { Button("Enable", {}, size = ButtonSize.Xs) }
                }
                Alert {
                    Column(Modifier.padding(end = 80.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        AlertTitle("Review changes")
                        AlertDescription("Choose an action for this draft.")
                    }
                    AlertAction {
                        Row(Modifier.width(136.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button("Save", {}, size = ButtonSize.Xs, style = Style { width(64.dp) })
                            Button(
                                "Undo",
                                {},
                                size = ButtonSize.Xs,
                                variant = ButtonVariant.Outline,
                                style = Style { width(64.dp) },
                            )
                        }
                    }
                }
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Alert {
                        AlertTitle("RTL logical end")
                        AlertDescription("The action stays at the logical end.")
                        AlertAction { Button("Undo", {}, size = ButtonSize.Xs, variant = ButtonVariant.Outline) }
                    }
                }
            }
        }
}
