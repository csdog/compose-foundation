@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import kotlin.test.Test

class TextColorInheritanceTest : ScreenshotTest() {
    @Test fun ancestorStyleUpdatesWithoutThemeOrTextChangeAndCanBeRemoved() {
        var tint by mutableStateOf<Color?>(Color.Red)
        setThemedContent {
            val state = remember { MutableStyleState(null) }
            CompositionLocalProvider(LocalContentColor provides Color.Blue) {
                Column(Modifier.styleable(state, Style { tint?.let { contentColor(it) } })) {
                    Text { "Inherited" }
                    Text(textStyle = TextStyle(color = Color.Green)) { "Explicit" }
                }
            }
        }
        assertColor("Inherited", Color.Red)
        composeRule.runOnIdle { tint = Color.Magenta }
        composeRule.waitForIdle()
        assertColor("Inherited", Color.Magenta)
        assertColor("Explicit", Color.Green)
        composeRule.runOnIdle { tint = null }
        composeRule.waitForIdle()
        assertColor("Inherited", Color.Blue)
        assertColor("Explicit", Color.Green)
    }

    private fun assertColor(
        text: String,
        expected: Color,
    ) {
        composeRule.onNodeWithText(text).assertPaintedTextColor(expected)
    }
}
