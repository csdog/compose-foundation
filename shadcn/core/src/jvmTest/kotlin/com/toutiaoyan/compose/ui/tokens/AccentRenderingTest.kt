package com.toutiaoyan.compose.ui.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccentRenderingTest : ScreenshotTest() {
    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    private fun matrix(dark: Boolean) =
        snapshot("accent_official_matrix", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(Modifier.width(64.dp)) { "Accent" }
                    Text(Modifier.width(80.dp)) { "Primary" }
                    Text(Modifier.width(104.dp)) { "Secondary" }
                    Text(Modifier.width(64.dp)) { "Sidebar" }
                    Text { "Charts 1–5" }
                }
                for (accent in Accent.entries) {
                    Theme(preset = StylePreset.Nova, accent = accent, isDark = dark, minTouchTarget = null) {
                        val colors = currentTheme.colors
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(Modifier.width(64.dp)) { accent.label }
                            Button("Action", modifier = Modifier.width(80.dp))
                            Button("Secondary", variant = ButtonVariant.Secondary, modifier = Modifier.width(104.dp))
                            Box(
                                Modifier.size(64.dp, 24.dp).background(colors.sidebarPrimary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    textStyle =
                                        androidx.compose.ui.text.TextStyle(
                                            color = colors.sidebarPrimaryForeground,
                                        ),
                                ) { "Nav" }
                            }
                            Row {
                                listOf(
                                    colors.chart1,
                                    colors.chart2,
                                    colors.chart3,
                                    colors.chart4,
                                    colors.chart5,
                                ).forEach {
                                    Box(Modifier.size(20.dp, 24.dp).background(it))
                                }
                            }
                        }
                    }
                }
            }
        }

    @Test fun liveAccentAndBaseChangesRepaintWithoutResizingAndBaseResetsOverrides() {
        var dark by mutableStateOf(false)
        var accent by mutableStateOf(Accent.Base)
        var base by mutableStateOf(BaseColor.Neutral)
        var ring = Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, baseColor = base, accent = accent, isDark = dark, minTouchTarget = null) {
                ring = currentTheme.colors.ring
                Box(Modifier.background(currentTheme.colors.background).padding(12.dp)) {
                    Button(modifier = Modifier.width(100.dp).testTag("primary")) {
                        Box(Modifier.size(8.dp).background(LocalContentColor.current).testTag("ink"))
                    }
                }
            }
        }
        for (b in listOf(BaseColor.Neutral, BaseColor.Mauve)) for (mode in listOf(false, true, false)) {
            for (a in listOf(Accent.Blue, Accent.Lime, Accent.Fuchsia, Accent.Rose, Accent.Amber, Accent.Base)) {
                composeRule.runOnIdle {
                    base = b
                    dark = mode
                    accent = a
                }
                val original = if (mode) b.dark else b.light
                val expected = a.applyTo(original, mode)
                val pixels = composeRule.onNodeWithTag("primary").captureToImage().toPixelMap()
                assertColor(expected.primary, pixels[pixels.width / 2, 4])
                val ink = composeRule.onNodeWithTag("ink", useUnmergedTree = true).captureToImage().toPixelMap()
                assertColor(expected.primaryForeground, ink[4, 4])
                assertEquals(original.ring, ring, "Accent must not recolor the base focus ring")
            }
        }
    }

    private fun assertColor(
        expected: Color,
        actual: Color,
    ) {
        assertTrue(
            abs(expected.red - actual.red) < .012f &&
                abs(expected.green - actual.green) < .012f &&
                abs(expected.blue - actual.blue) < .012f,
            "Expected $expected, actual $actual",
        )
    }
}
