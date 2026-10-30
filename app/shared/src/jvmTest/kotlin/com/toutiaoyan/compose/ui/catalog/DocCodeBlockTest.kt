package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.CodeBlock
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocCodeBlockTest {
    @get:Rule val rule = createComposeRule()

    @Test fun snippetUsesNumberGutterAndFloatingCopyWithoutLanguageHeader() {
        val code = "val count = 42\n// note\nprintln(count)"
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CodeBlock(code, Modifier.width(400.dp).testTag("snippet"), lineNumbers = true)
            }
        }
        rule.onNodeWithText("Kotlin").assertDoesNotExist()
        rule.onNodeWithContentDescription("Copy").assertWidthIsEqualTo(28.dp).assertHeightIsEqualTo(28.dp)
        val root = rule.onNodeWithTag("snippet").fetchSemanticsNode().boundsInRoot
        val text = rule.onNodeWithText(code).fetchSemanticsNode().boundsInRoot
        val copy = rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot
        assertEquals(64f, text.left - root.left, .5f)
        assertEquals(14f, text.top - root.top, .5f)
        assertEquals(12f, copy.top - root.top, .5f)
        assertEquals(8f, root.right - copy.right, .5f)
    }

    @Test fun longCodeScrollsWhileGutterAndCopyStayInPlace() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CodeBlock("val longLine = \"${"word ".repeat(80)}\"", Modifier.width(300.dp), lineNumbers = true)
            }
        }
        val gutter = rule.onNodeWithText("1").fetchSemanticsNode().boundsInRoot
        val copy = rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot
        val scroll = rule.onNodeWithTag("code-scroll")
        scroll.performSemanticsAction(SemanticsActions.ScrollBy) { it(240f, 0f) }
        rule.waitForIdle()
        assertTrue(scroll.fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange].value() > 0f)
        assertEquals(gutter, rule.onNodeWithText("1").fetchSemanticsNode().boundsInRoot)
        assertEquals(copy, rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot)
    }

    @Test fun actualBackgroundAndStringHighlightUpdateWithTheme() {
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null, isDark = dark) {
                CodeBlock("val label = \"visible\"", Modifier.width(300.dp).testTag("snippet"))
            }
        }

        fun assertPaint(
            background: Color,
            string: Color,
        ) {
            val pixels = rule.onNodeWithTag("snippet").captureToImage().toPixelMap()
            assertEquals(background, pixels[pixels.width / 2, pixels.height - 3])
            var count = 0
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
                val color = pixels[x, y]
                if (abs(color.red - string.red) < .015f &&
                    abs(color.green - string.green) < .015f &&
                    abs(color.blue - string.blue) < .015f
                ) {
                    count++
                }
            }
            assertTrue(count >= 3, "Expected actual string color $string; found $count pixels")
        }
        assertPaint(Color(0xFFF8F8F8), Color(0xFF0A3069))
        rule.runOnIdle { dark = true }
        assertPaint(Color(0xFF161616), Color(0xFF99FFE4))
        rule.runOnIdle { dark = false }
        assertPaint(Color(0xFFF8F8F8), Color(0xFF0A3069))
    }

    @Test fun expandedCodeScrollsVerticallyWithoutLosingCopyButton() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CodeBlock(
                    (1..30).joinToString("\n") {
                        "println($it)"
                    },
                    Modifier.width(300.dp).testTag("snippet"),
                    lineNumbers = true,
                    maxHeight = 144.dp,
                )
            }
        }
        rule.onNodeWithTag("snippet").assertHeightIsEqualTo(144.dp)
        val copy = rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot
        val scroll = rule.onNodeWithTag("code-vertical-scroll")
        scroll.performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 300f) }
        rule.waitForIdle()
        assertTrue(scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() > 0f)
        rule.onNodeWithContentDescription("Copy").assertIsDisplayed()
        assertEquals(copy, rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot)
    }
}
