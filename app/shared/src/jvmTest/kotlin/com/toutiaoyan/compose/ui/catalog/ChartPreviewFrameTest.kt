package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.chartDoc
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class ChartPreviewFrameTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(
                    1440,
                    false,
                ),
                arrayOf<Any>(
                    1440,
                    true,
                ),
                arrayOf<Any>(
                    390,
                    false,
                ),
                arrayOf<Any>(
                    390,
                    true,
                ),
            )
    }

    private fun show(content: @Composable () -> Unit) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewport, 1000)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(Modifier.width(if (viewport == 390) 342.dp else 640.dp)) { content() }
                }
            }
        }
    }

    @Test fun firstStageUsesFixedPreviewHeightAndDesktopOnlyStepInset() {
        val example = chartDoc.examples.single { it.title == "Your First Chart" }
        show {
            PreviewCodeSection(
                example.code,
                modifier = Modifier.testTag("preview-frame"),
                minPreviewHeight = example.minPreviewHeight,
                previewPadding = example.previewPadding,
                previewHeight = example.previewHeight,
                desktopPreviewInsetStart = example.desktopPreviewInsetStart,
                preview = { example.RenderPreview() },
            )
        }
        val frame = rule.onNodeWithTag("preview-frame").fetchSemanticsNode().boundsInRoot
        val chart = rule.onNodeWithTag("chart-example").fetchSemanticsNode().boundsInRoot
        assertEquals(431f, frame.height, "320 preview +108 code +divider and borders")
        val expectedWidth = if (viewport == 390) 260f else 510f
        assertEquals(expectedWidth, chart.width)
        val expectedHeight = maxOf(200f, (expectedWidth * 9 / 16).toInt().toFloat())
        assertEquals(expectedHeight, chart.height)
        assertEquals(frame.top + 1 + (320 - expectedHeight) / 2, chart.top, .5f)
        assertEquals(frame.left + if (viewport == 390) 41 else 89, chart.left, .5f)
        val file = File("build/reports/chart-preview-frame/first-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("preview-frame").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun expandingFixedPreviewSourceKeepsMountedState() {
        show {
            PreviewCodeSection("val source = 1", previewHeight = 320.dp, desktopPreviewInsetStart = 48.dp) {
                var clicks by remember { mutableIntStateOf(0) }
                Button("Preview clicks $clicks", onClick = { clicks++ })
            }
        }
        rule.onNodeWithText("Preview clicks 0").performClick()
        rule.onNodeWithText("Preview clicks 1").assertIsDisplayed()
        rule.onNodeWithText("View Code").performClick()
        rule.onNodeWithText("Preview clicks 1").assertIsDisplayed().performClick()
        rule.onNodeWithText("Preview clicks 2").assertIsDisplayed()
    }
}
