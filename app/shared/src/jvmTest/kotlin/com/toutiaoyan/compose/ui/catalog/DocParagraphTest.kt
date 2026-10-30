package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.DocParagraph
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*
import kotlin.math.ceil

class DocParagraphTest {
    @get:Rule val rule = createComposeRule()

    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }

    @Test fun codeWrapsWithOriginalTextAndRoundedBackground() {
        val plain = "Use bg-amber-50 dark:bg-amber-950 here."
        var muted = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                muted = currentTheme.colors.muted
                Box(
                    Modifier.width(200.dp).background(Color.White),
                ) { DocParagraph("Use `bg-amber-50 dark:bg-amber-950` here.") }
            }
        }
        val result = layout(plain)
        assertEquals(plain, result.layoutInput.text.text)
        assertTrue(result.lineCount > 1)
        val rects = result.placeholderRects.filterNotNull()
        assertTrue(rects.map { it.top }.distinct().size > 1, "Code fragments must wrap with the paragraph")
        val first = rects.first()
        val image = rule.onNodeWithText(plain).captureToImage().toPixelMap()
        assertEquals(
            Color.White,
            image[ceil(first.left - .5f).toInt(), ceil(first.top - .5f).toInt()],
            "Rounded corner must expose the page",
        )
        assertEquals(
            muted,
            image[(first.left + 8).toInt(), (first.top + 1).toInt()],
            "Padding must paint code background",
        )
    }

    @Test fun longIdentifierNeverDisappearsInNarrowParagraph() {
        val identifier = "VeryLongIdentifier".repeat(16)
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                Box(Modifier.width(160.dp)) { DocParagraph("`$identifier`") }
            }
        }
        val result = layout(identifier)
        assertEquals(identifier, result.layoutInput.text.text)
        assertTrue(result.lineCount > 3)
        assertTrue(
            result.placeholderRects.all {
                it != null && it.right <= 160.5f
            },
            "Every original code segment must be laid out",
        )
    }

    @Test fun inlineLinksStillOpenAndCodeBackgroundTracksTheme() {
        var dark by mutableStateOf(false)
        var muted = Color.Unspecified
        var opened = ""
        val plain = "Use Style and guide."
        rule.setContent {
            CompositionLocalProvider(
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            opened = uri
                        }
                    },
            ) {
                Theme(preset = StylePreset.Nova, isDark = dark) {
                    muted = currentTheme.colors.muted
                    Box(Modifier.width(240.dp).background(if (dark) Color.Black else Color.White)) {
                        DocParagraph("Use `Style` and [guide](https://ui.shadcn.com/docs/rtl).")
                    }
                }
            }
        }
        val result = layout(plain)
        val link = result.layoutInput.text.getLinkAnnotations(0, plain.length).single()
        assertEquals("https://ui.shadcn.com/docs/rtl", (link.item as LinkAnnotation.Url).url)
        val bounds = result.getBoundingBox(link.start)
        rule.onNodeWithText(plain).performTouchInput { click(Offset(bounds.center.x, bounds.center.y)) }
        assertEquals("https://ui.shadcn.com/docs/rtl", opened)

        fun assertBackground() {
            val rect = layout(plain).placeholderRects.first()!!
            val image = rule.onNodeWithText(plain).captureToImage().toPixelMap()
            assertEquals(muted, image[(rect.left + 1).toInt(), rect.center.y.toInt()])
        }
        assertBackground()
        rule.runOnIdle { dark = true }
        assertBackground()
        rule.runOnIdle { dark = false }
        assertBackground()
    }
}
