package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.hoverCardDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class HoverCardBrowserParityTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(width, 1000)
                    }
                }
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp, LocalWindowInfo provides window) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    OverlayHost {
                        Box(
                            Modifier.requiredSize(
                                (if (width < 640) 325 else 638).dp,
                                (if (title == "RTL") 384 else 400).dp,
                            ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                Modifier.fillMaxWidth().padding(if (title == "Sides") 40.dp else 0.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                hoverCardDoc.examples.single { it.title == title }.RenderPreview()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hover(
        text: String,
        expected: String,
    ) {
        rule.onNodeWithText(text).performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        rule.waitUntil(3000) { rule.onAllNodesWithText(expected).fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText(expected).assertIsDisplayed()
    }

    private fun layout(text: String): TextLayoutResult {
        val result = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(result) }
        return result.single()
    }

    @Test fun fourDirectionsShareOneLineAtTheOfficialPhonePreviewWidth() {
        show("Sides")
        val names = listOf("Left", "Top", "Bottom", "Right")
        val centers = names.map { rule.onNodeWithText(it).fetchSemanticsNode().boundsInWindow.center.y }
        val details =
            names.map { name ->
                val bounds = rule.onNodeWithText(name).fetchSemanticsNode().boundsInWindow
                val result = layout(name)
                val advance = result.getHorizontalPosition(name.length, true) - result.getHorizontalPosition(0, true)
                "$name: box=${bounds.width}, intrinsic=${result.multiParagraph.maxIntrinsicWidth}, " +
                    "advance=$advance, " +
                    "font=${result.layoutInput.style.fontSize}, weight=${result.layoutInput.style.fontWeight}"
            }
        assertTrue(
            centers.max() - centers.min() <= 1f,
            "At a 245dp inner width, all four source buttons share one line; $details",
        )
    }

    @Test fun defaultPreviewRetainsAllThreeCompleteLineBoxes() {
        show("Default")
        hover("Hover Here", "@nextjs")
        assertEquals(20, layout("@nextjs").size.height)
        assertEquals(40, layout("The React Framework – created and maintained by @vercel.").size.height)
        assertEquals(16, layout("Joined December 2021").size.height)
        assertEquals(FontWeight.SemiBold, layout("@nextjs").layoutInput.style.fontWeight)
    }

    @Test fun rtlUsesActualPreviewHeightAndAllThreeLanguageFontsAndLineBoxes() {
        show("RTL")
        val toolbar = rule.onNodeWithTag("rtl-preview-toolbar").fetchSemanticsNode().boundsInWindow
        val body = rule.onNodeWithTag("hover-rtl-body").fetchSemanticsNode().boundsInWindow
        assertEquals(63f, toolbar.height, .5f)
        assertEquals(320f, body.height, .5f)
        assertEquals(toolbar.bottom + 1f, body.top, .5f)
        listOf("يسار", "أعلى", "أسفل", "يمين", "بداية السطر", "نهاية السطر").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
        hover("بداية السطر", "سماعات لاسلكية")
        assertEquals(20, layout("سماعات لاسلكية").size.height)
        assertEquals(20, layout("٩٩.٩٩ $").size.height)
        assertNotNull(layout("سماعات لاسلكية").layoutInput.style.fontFamily)
        rule.onNodeWithText("بداية السطر").performMouseInput { exit(delayMillis = 0) }
        rule.mainClock.advanceTimeBy(300)
        for ((label, trigger, name, price) in listOf(
            listOf("English", "Inline End", "Wireless Headphones", "$99.99"),
            listOf("Hebrew (עברית)", "סוף השורה", "אוזניות אלחוטיות", "99.99 $"),
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onNodeWithText(label).performClick()
            hover(trigger, name)
            assertEquals(20, layout(name).size.height)
            assertEquals(20, layout(price).size.height)
            rule.onNodeWithText(trigger).performMouseInput { exit(delayMillis = 0) }
            rule.mainClock.advanceTimeBy(300)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}-{1}")
        fun parameters() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }
}
