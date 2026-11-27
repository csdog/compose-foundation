package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.emptyDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class EmptyBrowserParityTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private var opens = 0

    private fun show(title: String) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(width, 1000)
                    }
                }
            CompositionLocalProvider(
                LocalCatalogViewportWidth provides width.dp,
                LocalWindowInfo provides window,
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            assertEquals("#", uri)
                            opens++
                        }
                    },
            ) {
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
                                (if (title == "RTL") 352 else 600).dp,
                            ),
                            contentAlignment = Alignment.Center,
                        ) {
                            emptyDoc.examples.single { it.title == title }.RenderPreview()
                        }
                    }
                }
            }
        }
    }

    @Test fun outlineKeepsResponsiveInsetAndNaturalComponentHeight() {
        show("Outline")
        val frameWidth = if (width < 640) 325f else 638f
        val inset = if (width < 768) 24f else 40f
        val root = rule.onNodeWithTag("empty-outline-root").fetchSemanticsNode().boundsInRoot
        assertEquals(frameWidth - inset * 2, root.width, 0.5f)
        assertTrue(root.height < 270f, "Dashed border should hug natural content, not stretch through the preview")
        rule.onNodeWithText("Upload Files").assertIsDisplayed()
    }

    @Test fun learnMoreIsOneLinkAndItsPaddingAndKeyboardActivateExactlyOnce() {
        show("Default")
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("Learn More").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val text = layouts.single().layoutInput.text
        assertEquals("#", (text.getLinkAnnotations(0, text.length).single().item as LinkAnnotation.Url).url)
        assertEquals(
            Role.Button,
            rule.onNodeWithTag("empty-learn-more").fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role),
        )
        rule.onNodeWithTag("empty-learn-more").assertHeightIsEqualTo(28.dp)
        rule.onRoot().performKeyInput {
            repeat(3) { pressKey(Key.Tab) }
            pressKey(Key.Enter)
        }
        assertEquals(1, opens)
        rule.onNodeWithText("Learn More").performTouchInput { click() }
        assertEquals(2, opens)
        rule.onNodeWithTag("empty-learn-more").performTouchInput { click(Offset(3f, center.y)) }
        assertEquals(3, opens)
    }

    @Test fun rtlToolbarBodyAndArabicLineBoxesFitTheActualPreview() {
        show("RTL")
        val toolbar = rule.onNodeWithTag("rtl-preview-toolbar").fetchSemanticsNode().boundsInRoot
        val body = rule.onNodeWithTag("empty-rtl-body").fetchSemanticsNode().boundsInRoot
        assertEquals(63f, toolbar.height, 0.5f)
        assertEquals(288f, body.height, 0.5f)
        assertEquals(toolbar.bottom + 1f, body.top, 0.5f)
        rule.onNodeWithText("لا توجد مشاريع بعد").assertIsDisplayed().assertHeightIsEqualTo(20.dp)
        rule.onNodeWithText("إنشاء مشروع").assertIsDisplayed()
        rule.onNodeWithText("استيراد مشروع").assertIsDisplayed()
        val title = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(
            "لا توجد مشاريع بعد",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(title) }
        assertNotNull(title.single().layoutInput.style.fontFamily)
        assertEquals(20, title.single().size.height)
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
