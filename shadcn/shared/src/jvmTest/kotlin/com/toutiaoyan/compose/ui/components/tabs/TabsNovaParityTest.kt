@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class TabsNovaParityTest : ScreenshotTest() {
    @Test fun novaTriggerUsesSixByTwoPaddingAndOnePixelTransparentBorder() {
        setThemedContent(stylePreset = StylePreset.Nova, useMinimumTouchTarget = false) {
            Tabs(value = "account") {
                TabsList {
                    TabsTrigger("account", label = "Account", modifier = Modifier.testTag("trigger"))
                }
            }
        }
        val trigger = composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInRoot
        val label = composeRule.onNodeWithText("Account", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(label.width + 14f, trigger.width, 0.1f)
        assertEquals(25f, trigger.height, 0.1f)
    }

    @Test fun expandedMobileTriggersKeepTheWholeLabelOnOneLine() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(value = "last-anchor") {
                TabsList(Modifier.width(280.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        TabsTrigger("start", label = "start", modifier = Modifier.weight(1f))
                        TabsTrigger("end", label = "end", modifier = Modifier.weight(1f))
                        TabsTrigger("last-anchor", label = "last-anchor", modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        val natural = composeRule.onNodeWithText("start", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val constrained =
            composeRule.onNodeWithText(
                "last-anchor",
                useUnmergedTree =
                true,
            ).fetchSemanticsNode().boundsInRoot
        assertEquals(natural.height, constrained.height, 0.1f)
    }

    private fun nova(dark: Boolean) =
        snapshot("tabs_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Tabs(value = "last-anchor") {
                TabsList(Modifier.width(280.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        TabsTrigger("start", label = "start", modifier = Modifier.weight(1f))
                        TabsTrigger("end", label = "end", modifier = Modifier.weight(1f))
                        TabsTrigger("last-anchor", label = "last-anchor", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
