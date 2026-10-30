package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachmentGroupFadeTest : ScreenshotTest() {
    @Test fun overflowEdgesRevealGraduallyAndMirrorInRtl() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                key(direction) {
                    Box(Modifier.size(200.dp, 48.dp).background(Color.White).testTag("surface")) {
                        AttachmentGroup(Modifier.size(200.dp, 48.dp).testTag("group")) {
                            repeat(3) { Box(Modifier.size(150.dp, 40.dp).background(Color.Red)) }
                        }
                    }
                }
            }
        }

        fun pixels() = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()

        fun scroll(amount: Float) {
            composeRule.onNodeWithTag("group").performSemanticsAction(SemanticsActions.ScrollBy) { it(amount, 0f) }
            composeRule.waitForIdle()
        }
        for (dir in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
            composeRule.runOnIdle { direction = dir }
            composeRule.waitForIdle()

            fun fromStart(x: Int) = if (dir == LayoutDirection.Ltr) x else 199 - x
            var image = pixels()
            assertEquals(0f, image[fromStart(0), 24].green, .02f, "The starting edge stays crisp")
            assertTrue(image[fromStart(199), 24].green > .9f, "Overflow fades at the opposite edge")
            assertEquals(0f, image[fromStart(174), 24].green, .02f, "12% caps the fade at 24px in a 200px viewport")
            scroll(48f)
            val halfReveal = pixels()[fromStart(6), 24].green
            assertTrue(halfReveal in 0.35f..0.65f, "Half the reveal range uses a half-width fade: $halfReveal")
            scroll(48f)
            val fullReveal = pixels()[fromStart(6), 24].green
            assertTrue(fullReveal > halfReveal + .15f, "The leading mask grows continuously over 96px")
            scroll(10000f)
            image = pixels()
            assertTrue(image[fromStart(0), 24].green > .9f, "At the end, earlier content still fades")
            assertEquals(0f, image[fromStart(199), 24].green, .02f, "The ending edge becomes crisp")
        }
    }

    @Test fun fittingContentDoesNotFade() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(200.dp, 48.dp).background(Color.White).testTag("surface")) {
                AttachmentGroup(Modifier.size(200.dp, 48.dp)) {
                    Box(Modifier.size(200.dp, 40.dp).background(Color.Red))
                }
            }
        }
        val image = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
        assertEquals(0f, image[0, 24].green, .02f)
        assertEquals(0f, image[199, 24].green, .02f)
    }
}
