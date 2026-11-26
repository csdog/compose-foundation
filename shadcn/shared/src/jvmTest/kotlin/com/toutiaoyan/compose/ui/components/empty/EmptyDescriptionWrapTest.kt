@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EmptyDescriptionWrapTest : ScreenshotTest() {
    @Test
    fun engineGroupsMatchExhaustiveSearchAndReuseParagraphs() {
        lateinit var measurer: TextMeasurer
        setThemedContent { measurer = rememberTextMeasurer(cacheSize = 128) }
        composeRule.runOnIdle {
            val style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, textAlign = TextAlign.Center)
            val rich =
                buildAnnotatedString {
                    append("aaa bbbbb cc dddddd ee ffff g hhhhh ii jjjjj k")
                    addLink(LinkAnnotation.Url("#"), 4, 9)
                }

            fun measured(
                text: AnnotatedString,
                width: Int,
                direction: LayoutDirection = LayoutDirection.Ltr,
            ) = measurer.measure(text, style, constraints = Constraints(maxWidth = width), layoutDirection = direction)

            val fullWidth = measured(AnnotatedString("aaa bbbbb cc dddddd ee ffff"), 1000).size.width
            val original = measured(rich, fullWidth)
            assertEquals(2, original.lineCount)
            val exhaustive =
                (1..fullWidth).map { measured(rich, it) }
                    .filter { it.lineCount == original.lineCount && !it.hasVisualOverflow }
            val paragraphs = mutableMapOf<Int, TextLayoutResult>()

            fun select(): Int =
                emptyDescriptionWidth(original, pretty = false) { width ->
                    measured(rich, width).also { next ->
                        paragraphs[width]?.let { assertSame(it.multiParagraph, next.multiParagraph) }
                        paragraphs[width] = next
                    }
                }
            val chosenWidth = select()
            val count = paragraphs.size
            assertTrue(count < 64, "Search stays within the paragraph budget")
            assertEquals(chosenWidth, select())
            assertEquals(count, paragraphs.size, "Identical layout reuses the same cached paragraphs")
            val chosen = measured(rich, chosenWidth)
            assertEquals(exhaustive.minOf(::variance), variance(chosen), 0.01)
            assertEquals(rich, chosen.layoutInput.text)
            assertFalse(chosen.hasVisualOverflow)

            // Find a real engine widow, not a platform-specific width or Web endpoint.
            val widowWidth = measured(AnnotatedString(rich.text.substringBeforeLast(" ")), 1000).size.width + 1
            val widow = measured(rich, widowWidth)
            assertEquals("k", widow.lastLineText().trim())
            val prettyWidth = emptyDescriptionWidth(widow, pretty = true) { measured(rich, it) }
            val pretty = measured(rich, prettyWidth)
            assertEquals(widow.lineCount, pretty.lineCount)
            assertTrue(pretty.lastLineText().trim().contains(' '))
            assertEquals(prettyWidth, emptyDescriptionWidth(pretty, pretty = true) { error("No widow: no search") })
            val arabic = AnnotatedString("لم تقم بإنشاء أي مشاريع بعد. ابدأ بإنشاء مشروعك الأول.")
            val rtl = measured(arabic, fullWidth, LayoutDirection.Rtl)
            val rtlWidth = emptyDescriptionWidth(rtl, pretty = false) { measured(arabic, it, LayoutDirection.Rtl) }
            val result = measured(arabic, rtlWidth, LayoutDirection.Rtl)
            assertEquals(LayoutDirection.Rtl, result.layoutInput.layoutDirection)
            assertEquals(arabic, result.layoutInput.text)
            assertEquals(rtl.lineCount, result.lineCount)
        }
    }

    @Test
    fun actualTextShellAndFallbackKeepCallerConstraintsAndTypography() {
        val cases =
            listOf(
                "Short",
                "alpha beta gamma delta alpha beta gamma delta",
                "alpha beta gamma delta",
                "alpha beta gamma delta alpha beta gamma delta",
            )
        lateinit var measurer: TextMeasurer
        setThemedContent {
            measurer = rememberTextMeasurer(cacheSize = 128)
            CompositionLocalProvider(
                LocalDefaultTextStyle provides TextStyle(fontFamily = FontFamily.Monospace, letterSpacing = 1.sp),
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    for ((index, text) in cases.withIndex()) {
                        val rich = AnnotatedString(text)
                        val shell =
                            Style {
                                contentPadding(7.dp)
                                width(220.dp)
                            }
                        val policy =
                            when (index) {
                                2 -> LineBreak.Simple
                                3 -> LineBreak.Heading
                                else -> LineBreak.Paragraph
                            }
                        val bounds = Modifier.width(260.dp).heightIn(min = 40.dp, max = 120.dp)
                        EmptyDescription(rich, bounds.testTag("base$index"), shell, policy)
                        EmptyDescriptionWrapping(rich, enabled = true) {
                            EmptyDescription(rich, bounds.testTag("wrap$index"), shell, policy)
                        }
                    }
                    EmptyDescription("Short", Modifier.width(240.dp).testTag("baseString"))
                    EmptyDescriptionWrapping(AnnotatedString("Short"), enabled = true) {
                        EmptyDescription("Short", Modifier.width(240.dp).testTag("wrapString"))
                    }
                    val balanced = AnnotatedString("aaa bbbbb cc dddddd ee ffff g hhhhh ii jjjjj k")
                    val shell =
                        Style {
                            contentPadding(11.dp)
                            width(240.dp)
                        }
                    EmptyDescription(balanced, Modifier.testTag("baseBalance"), shell, LineBreak.Heading)
                    EmptyDescriptionWrapping(balanced, enabled = true) {
                        EmptyDescription(balanced, Modifier.testTag("wrapBalance"), shell, LineBreak.Heading)
                    }
                }
            }
        }
        composeRule.runOnIdle { assertTextShellAndFallback(cases.indices, measurer) }
    }

    private fun assertTextShellAndFallback(
        cases: IntRange,
        measurer: TextMeasurer,
    ) {
        for (index in cases) {
            val baseline = paragraph("base$index")
            val wrapped = paragraph("wrap$index")
            assertEquals(baseline.layoutInput.constraints, wrapped.layoutInput.constraints)
            assertEquals(baseline.size, wrapped.size)
            assertEquals(baseline.layoutInput.style, wrapped.layoutInput.style)
            assertTrue(baseline.layoutInput.constraints.minWidth > 0)
            assertTrue(baseline.layoutInput.constraints.hasBoundedHeight)
            if (index == 1 || index == 3) {
                assertEquals(2, baseline.lineCount)
                assertTrue(baseline.lastLineText().trim().contains(' '))
            }
        }
        val plain = paragraph("baseString")
        val wrappedPlain = paragraph("wrapString")
        assertEquals(plain.layoutInput.constraints, wrappedPlain.layoutInput.constraints)
        assertEquals(plain.layoutInput.style, wrappedPlain.layoutInput.style)
        assertEquals(plain.size, wrappedPlain.size)
        val baseline = paragraph("baseBalance")
        val wrapped = paragraph("wrapBalance")
        val input = baseline.layoutInput
        val selected =
            emptyDescriptionWidth(baseline, pretty = false) { width ->
                measurer.measure(
                    input.text,
                    input.style,
                    constraints = input.constraints.copy(minWidth = 0, maxWidth = width),
                    layoutDirection = input.layoutDirection,
                )
            }
        assertEquals(selected, wrapped.layoutInput.constraints.maxWidth)
        assertEquals(baseline.lineCount, wrapped.lineCount)
        assertEquals(input.style, wrapped.layoutInput.style)
        assertEquals(FontFamily.Monospace, wrapped.layoutInput.style.fontFamily)
        assertEquals(1.sp, wrapped.layoutInput.style.letterSpacing)
    }

    private fun paragraph(tag: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        val node = composeRule.onNodeWithTag(tag).fetchSemanticsNode()
        assertTrue(node.config[SemanticsActions.GetTextLayoutResult].action!!.invoke(layouts))
        return layouts.single()
    }

    private fun variance(result: TextLayoutResult): Double {
        val widths = (0 until result.lineCount).map { result.getLineRight(it) - result.getLineLeft(it) }
        val mean = widths.average()
        return widths.sumOf { (it - mean) * (it - mean) }
    }

    private fun TextLayoutResult.lastLineText(): String =
        layoutInput.text.text.substring(getLineStart(lineCount - 1), getLineEnd(lineCount - 1, visibleEnd = true))
}
