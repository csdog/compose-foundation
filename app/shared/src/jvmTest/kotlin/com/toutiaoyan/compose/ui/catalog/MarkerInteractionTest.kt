package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.markerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs
import kotlin.test.*

@RunWith(Parameterized::class)
class MarkerInteractionTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private val opened = mutableListOf<String>()
    private var foreground = Color.Unspecified
    private var muted = Color.Unspecified
    private var background = Color.Unspecified

    private fun show() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                foreground = currentTheme.colors.foreground
                muted = currentTheme.colors.mutedForeground
                background = currentTheme.colors.background
                CompositionLocalProvider(
                    LocalUriHandler provides
                        object : UriHandler {
                            override fun openUri(uri: String) {
                                opened += uri
                            }
                        },
                ) {
                    OverlayHost {
                        Box(Modifier.width((if (width < 640) 325 else 638).dp).padding(40.dp)) {
                            markerDoc.examples.single { it.title == "Links and Buttons" }.RenderPreview()
                        }
                    }
                }
            }
        }
    }

    private fun layout(text: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }

    private fun iconContrast(tag: String): Float {
        val pixels = rule.onNodeWithTag(tag).captureToImage().toPixelMap()
        return (0 until pixels.height).sumOf { y ->
            (0 until 16).sumOf { x -> abs(pixels[x, y].compositeOver(background).red - background.red).toDouble() }
        }.toFloat()
    }

    @Test fun hoverChangesBothTheTextAndTheIconToForeground() {
        show()
        for ((tag, label) in listOf(
            "marker-link" to "View the pull request",
            "marker-button" to "Revert this change",
        )) {
            assertEquals(muted, layout(label).layoutInput.style.color)
            val before = iconContrast(tag)
            rule.onNodeWithTag(tag).performMouseInput {
                enter(center)
                moveTo(center)
            }
            assertEquals(foreground, layout(label).layoutInput.style.color)
            assertTrue(iconContrast(tag) > before * 1.3f, "The icon must brighten with the label")
            rule.onNodeWithTag(tag).performMouseInput { exit() }
        }
    }

    @Test fun buttonColorsPassThroughIntermediateValuesOnHoverAndLeave() {
        show()
        val button = rule.onNodeWithTag("marker-button")
        val label = "Revert this change"
        rule.mainClock.autoAdvance = false
        button.performMouseInput { enter(center) }
        rule.mainClock.advanceTimeBy(64)
        val entering = layout(label).layoutInput.style.color
        assertTrue(entering.red > minOf(muted.red, foreground.red))
        assertTrue(entering.red < maxOf(muted.red, foreground.red))
        rule.mainClock.advanceTimeBy(160)
        assertEquals(foreground, layout(label).layoutInput.style.color)
        button.performMouseInput { exit() }
        rule.mainClock.advanceTimeBy(64)
        val leaving = layout(label).layoutInput.style.color
        assertTrue(leaving.red > minOf(muted.red, foreground.red))
        assertTrue(leaving.red < maxOf(muted.red, foreground.red))
        rule.mainClock.advanceTimeBy(160)
        assertEquals(muted, layout(label).layoutInput.style.color)
    }

    @Test fun rootPointerAndEnterActivateTheLocalAnnotatedLinkExactlyOnce() {
        show()
        val text = layout("View the pull request").layoutInput.text
        assertEquals(
            "#links-and-buttons",
            (text.getLinkAnnotations(0, text.length).single().item as LinkAnnotation.Url).url,
        )
        rule.onNodeWithTag("marker-link").performTouchInput { click(Offset(width - 2f, center.y)) }
        assertEquals(listOf("#links-and-buttons"), opened)
        rule.onNodeWithTag("marker-link").performKeyInput { pressKey(Key.Enter) }
        assertEquals(listOf("#links-and-buttons", "#links-and-buttons"), opened)
        rule.onNodeWithTag("marker-button").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        rule.onNodeWithTag("marker-button").performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("You clicked the revert button").assertIsDisplayed()
    }

    @Test fun textAndDecorativeIconEachActivateTheLinkOnce() {
        show()
        rule.onNodeWithText("View the pull request").performTouchInput { click(center) }
        assertEquals(listOf("#links-and-buttons"), opened)
        rule.onNodeWithTag("marker-link").performKeyInput { pressKey(Key.Enter) }
        assertEquals(List(2) { "#links-and-buttons" }, opened)
        rule.onNodeWithTag("marker-link").performTouchInput { click(Offset(8f, center.y)) }
        assertEquals(List(3) { "#links-and-buttons" }, opened)
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
