@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.aspectratio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors

class AspectRatioScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("aspect_ratio_states", darkTheme = darkTheme) {
            AspectRatio(ratio = 16f / 9f, modifier = Modifier.width(240.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "16:9" }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
