@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class PopoverNovaLayoutTest : ScreenshotTest() {
    private fun panelLayout(dark: Boolean) {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Popover(open = true) {
                PopoverTrigger("Open")
                PopoverContent(modifier = Modifier.testTag("panel")) {
                    Box(Modifier.size(20.dp).testTag("first"))
                    Box(Modifier.size(20.dp).testTag("second"))
                }
            }
        }
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(288.dp).assertHeightIsEqualTo(70.dp)
        val panel = composeRule.onNodeWithTag("panel").getUnclippedBoundsInRoot()
        val first = composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot()
        val second = composeRule.onNodeWithTag("second").getUnclippedBoundsInRoot()
        assertEquals(10f, (first.top - panel.top).value, 0.5f)
        assertEquals(10f, (first.left - panel.left).value, 0.5f)
        assertEquals(10f, (second.top - first.bottom).value, 0.5f)
        assertEquals(10f, (panel.bottom - second.bottom).value, 0.5f)
    }

    @Test fun lightPanelSpacing() = panelLayout(false)

    @Test fun darkPanelSpacing() = panelLayout(true)

    @Test fun novaHeaderUsesTwentyPixelLinesAndTwoPixelGap() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            PopoverHeader("Dimensions", "Set the dimensions for the layer.", Modifier.testTag("header"))
        }
        composeRule.onNodeWithTag("header").assertHeightIsEqualTo(42.dp)
    }

    @Test fun callerCanOverridePanelWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Popover(open = true) {
                PopoverTrigger("Open")
                PopoverContent("Body", Modifier.testTag("panel"), style = Style { width(320.dp) })
            }
        }
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(320.dp)
    }

    @Test fun stringBodyKeepsTheFullTwentyPixelLineBox() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Popover(open = true) {
                PopoverTrigger("Open")
                PopoverContent("Body", Modifier.testTag("body-panel"))
            }
        }
        composeRule.onNodeWithTag("body-panel").assertHeightIsEqualTo(40.dp)
    }
}
