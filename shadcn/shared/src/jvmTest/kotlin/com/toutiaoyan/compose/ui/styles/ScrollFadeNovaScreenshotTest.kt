@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.border
import kotlin.test.Test

class ScrollFadeNovaScreenshotTest : ScreenshotTest() {
    private fun frame(
        dark: Boolean,
        position: String,
    ) {
        lateinit var state: ScrollState
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            val current = rememberScrollState()
            val colors = currentTheme.colors
            val outer = RoundedCornerShape(twRadius.xl2)
            val inner = RoundedCornerShape(twRadius.lg)
            SideEffect { state = current }
            Box(Modifier.width(320.dp).border(1.dp, colors.border, outer).clip(outer).padding(1.dp)) {
                Column(
                    Modifier.height(288.dp).scrollFade(current).verticalScroll(current).padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    repeat(12) { index ->
                        Box(
                            Modifier.fillMaxWidth().clip(inner).background(colors.muted)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                textStyle =
                                    TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        lineHeightStyle =
                                            LineHeightStyle(
                                                LineHeightStyle.Alignment.Center,
                                                LineHeightStyle.Trim.None,
                                            ),
                                    ),
                            ) { "Item ${index + 1}" }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            state.dispatchRawDelta(
                when (position) {
                    "middle" ->
                        state.maxValue / 2f
                    "end" ->
                        state.maxValue.toFloat()
                    else
                    -> 0f
                },
            )
        }
        composeRule.waitForIdle()
        captureNamed("scroll_fade_nova_$position", dark)
    }

    @Test fun startLight() = frame(false, "start")

    @Test fun startDark() = frame(true, "start")

    @Test fun middleLight() = frame(false, "middle")

    @Test fun middleDark() = frame(true, "middle")

    @Test fun endLight() = frame(false, "end")

    @Test fun endDark() = frame(true, "end")
}
