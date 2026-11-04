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
import com.toutiaoyan.compose.ui.catalog.docs.accordionDoc
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
class AccordionPreviewFrameTest(private val title: String, private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},viewport={1},dark={2}")
        fun modes() =
            listOf("Default", "Basic", "Multiple", "Disabled", "Borders", "Card").flatMap { title ->
                listOf(1440, 390).flatMap {
                        width ->
                    listOf(false, true).map { dark -> arrayOf<Any>(title, width, dark) }
                }
            }
    }

    @Test fun contentStaysTopAlignedAndFrameDoesNotGrowWhenAnItemToggles() {
        val example = accordionDoc.examples.single { it.title == title }
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize =
                            IntSize(
                                viewport,
                                1000,
                            )
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    PreviewCodeSection(
                        example.code,
                        modifier = Modifier.width(if (viewport == 390) 342.dp else 640.dp).testTag("frame"),
                        previewHeight = example.previewHeight,
                        previewVerticalAlignment = example.previewVerticalAlignment,
                        previewPadding = example.previewPadding,
                    ) { Box(Modifier.testTag("content")) { example.preview() } }
                }
            }
        }
        val expected =
            when (title) {
                "Multiple" -> 450f
                "Card" -> 435f
                else -> 300f
            }

        fun assertFrame() {
            val frame = rule.onNodeWithTag("frame").fetchSemanticsNode().boundsInRoot
            val content = rule.onNodeWithTag("content").fetchSemanticsNode().boundsInRoot
            assertEquals(expected + 111, frame.height)
            assertEquals(frame.top + 41, content.top, .5f, "Content stays40 below the border instead of recentering")
        }

        fun capture(state: String) {
            val file =
                File(
                    "build/reports/accordion-preview-r7",
                    "${title.lowercase()}-$state-$viewport-${if (dark) "dark" else "light"}.png",
                )
            file.parentFile.mkdirs()
            ImageIO.write(rule.onNodeWithTag("frame").captureToImage().toAwtImage(), "png", file)
        }
        assertFrame()
        capture("initial")
        rule.onAllNodes(hasClickAction() and hasAnyAncestor(hasTestTag("content"))).onFirst().performClick()
        rule.waitForIdle()
        assertFrame()
        capture("toggled")
    }
}
