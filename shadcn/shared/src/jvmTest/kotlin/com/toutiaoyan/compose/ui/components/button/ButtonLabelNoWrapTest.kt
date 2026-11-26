@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class ButtonLabelNoWrapTest {
    @get:Rule val rule = createComposeRule()

    @Test fun constrainedStringLabelRetainsEveryWordInBothDirections() {
        val label = "Continue with Google"
        var rtl by mutableStateOf(false)
        rule.setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Button(label, modifier = Modifier.testTag("button"), style = Style { width(80.dp) })
                }
            }
        }
        for (direction in listOf(false, true)) {
            rule.runOnIdle { rtl = direction }
            val layouts = mutableListOf<TextLayoutResult>()
            rule.onNodeWithText(label).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals(1, layouts.single().lineCount)
            assertEquals(label.length, layouts.single().getLineEnd(0, visibleEnd = true))
            rule.onNodeWithTag("button").assertWidthIsEqualTo(80.dp).assertHeightIsEqualTo(32.dp)
        }
    }

    @Test fun contentCanOverflowButTheCallerCanExplicitlyClipIt() {
        var clip by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.background(Color.White).testTag("scene").padding(40.dp)) {
                    Button(
                        modifier = if (clip) Modifier.clip(RectangleShape) else Modifier,
                        style =
                            Style {
                                width(32.dp)
                                height(32.dp)
                            },
                    ) {
                        Box(Modifier.requiredSize(80.dp, 8.dp).background(Color.Red))
                    }
                }
            }
        }
        val before = rule.onNodeWithTag("scene").captureToImage().toPixelMap()
        assertEquals(Color.Red, before[30, 56])
        rule.runOnIdle { clip = true }
        val after = rule.onNodeWithTag("scene").captureToImage().toPixelMap()
        assertEquals(Color.White, after[30, 56])
    }
}
