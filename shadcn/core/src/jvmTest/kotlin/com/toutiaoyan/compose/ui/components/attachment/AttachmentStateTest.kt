@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachmentStateTest : ScreenshotTest() {
    @Test fun rootMediaAndDescriptionFollowLiveStateAndTheme() {
        var dark by mutableStateOf(false)
        var state by mutableStateOf(AttachmentState.Error)
        var card = Color.Unspecified
        var destructive = Color.Unspecified
        var muted = Color.Unspecified
        var foreground = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(isDark = dark, preset = StylePreset.Nova) {
                val colors = currentTheme.colors
                card = colors.card
                destructive = colors.destructive
                muted = colors.mutedForeground
                foreground = colors.foreground
                Attachment(Modifier.testTag("attachment"), state = state) {
                    AttachmentMedia(Modifier.testTag("media")) {
                        Box(Modifier.size(8.dp).testTag("ink").background(LocalContentColor.current))
                    }
                    AttachmentContent {
                        AttachmentTitle("report.pdf")
                        AttachmentDescription("Upload failed", Modifier.testTag("description"))
                    }
                }
            }
        }
        for (theme in listOf(false, true, false)) for (phase in listOf(AttachmentState.Error, AttachmentState.Done)) {
            composeRule.runOnIdle {
                dark = theme
                state = phase
            }
            composeRule.waitForIdle()
            val shell = composeRule.onNodeWithTag("attachment").captureToImage().toPixelMap()
            assertColor(card, shell[4, shell.height / 2])
            val media = composeRule.onNodeWithTag("media").captureToImage().toPixelMap()
            val error = phase == AttachmentState.Error
            if (error) assertColor(destructive.copy(alpha = .1f).compositeOver(card), media[2, media.height / 2])
            val ink = composeRule.onNodeWithTag("ink").captureToImage().toPixelMap()
            assertColor(if (error) destructive else foreground, ink[4, 4])
            assertContainsColor("description", if (error) destructive.copy(alpha = .8f).compositeOver(card) else muted)
        }
    }

    @Test fun explicitDescriptionOverrideAndNestedAttachmentRemainIndependent() {
        var muted = Color.Unspecified
        var foreground = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            muted = currentTheme.colors.mutedForeground
            foreground = currentTheme.colors.foreground
            Attachment(state = AttachmentState.Error) {
                Column {
                    AttachmentDescription("Explicit neutral", Modifier.testTag("neutral"), isError = false)
                    Attachment {
                        AttachmentMedia {
                            Box(Modifier.size(8.dp).testTag("nested ink").background(LocalContentColor.current))
                        }
                        AttachmentContent("Nested file", "Independent status", Modifier.testTag("nested content"))
                        AttachmentDescription("Independent status", Modifier.testTag("nested description"))
                    }
                }
            }
        }
        assertContainsColor("neutral", muted)
        assertContainsColor("nested description", muted)
        assertColor(foreground, composeRule.onNodeWithTag("nested ink").captureToImage().toPixelMap()[4, 4])
    }

    @Test fun uploadingTitleInheritsMovingShimmer() = assertInheritedShimmer(AttachmentState.Uploading, false)

    @Test fun processingLambdaTitleInheritsMovingShimmer() = assertInheritedShimmer(AttachmentState.Processing, true)

    private fun assertInheritedShimmer(
        initial: AttachmentState,
        lambda: Boolean,
    ) {
        var state by mutableStateOf(initial)
        // InfiniteAnimationPolicy cancels infinite transitions started while the
        // test clock auto-advances. Freeze before composing the animation.
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(state = state) {
                AttachmentContent {
                    if (lambda) {
                        AttachmentTitle(Modifier.testTag("title")) { "market-research-document.pdf" }
                    } else {
                        AttachmentTitle("market-research-document.pdf", Modifier.testTag("title"))
                    }
                    AttachmentTitle("Explicit done", Modifier.testTag("static"), state = AttachmentState.Done)
                }
            }
        }
        try {
            fun pixels(tag: String): List<Color> {
                composeRule.waitForIdle()
                val image = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap()
                return List(image.width * image.height) { image[it % image.width, it / image.width] }
            }
            composeRule.mainClock.advanceTimeBy(300)
            val moving = pixels("title")
            val fixed = pixels("static")
            composeRule.mainClock.advanceTimeBy(700)
            assertTrue(
                moving != pixels("title"),
                "Root upload state must start title shimmer without repeating the state on the title",
            )
            assertEquals(fixed, pixels("static"), "Explicit Done must override root state")
            composeRule.runOnIdle { state = AttachmentState.Done }
            composeRule.mainClock.advanceTimeByFrame()
            val done = pixels("title")
            composeRule.mainClock.advanceTimeBy(700)
            assertEquals(done, pixels("title"), "Changing the root to Done must stop the shimmer")
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun imageOpacityTracksLifecycleAndCallerShapeClipsTheImage() {
        var state by mutableStateOf(AttachmentState.Done)
        var variant by mutableStateOf(AttachmentMediaVariant.Image)
        var dark by mutableStateOf(false)
        var card = Color.Unspecified
        val blue = Color(0xff3366ff)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(isDark = dark, preset = StylePreset.Nova) {
                card = currentTheme.colors.card
                Attachment(state = state) {
                    AttachmentMedia(
                        variant,
                        Modifier.testTag("image"),
                        style =
                            androidx.compose.foundation.style.Style {
                                shape(androidx.compose.foundation.shape.CircleShape)
                            },
                    ) {
                        Box(Modifier.fillMaxSize().background(blue))
                    }
                }
            }
        }
        for (night in listOf(
            false,
            true,
        )) for (type in AttachmentMediaVariant.entries) for (phase in AttachmentState.entries) {
            composeRule.runOnIdle {
                dark = night
                variant = type
                state = phase
            }
            val image = composeRule.onNodeWithTag("image").captureToImage().toPixelMap()
            val opacity =
                if (type == AttachmentMediaVariant.Image &&
                    phase != AttachmentState.Done &&
                    phase != AttachmentState.Idle
                ) {
                    0.6f
                } else {
                    1f
                }
            assertColor(blue.copy(alpha = opacity).compositeOver(card), image[image.width / 2, image.height / 2])
            assertColor(card, image[0, 0])
        }
    }

    private fun assertContainsColor(
        tag: String,
        expected: Color,
    ) {
        val image = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap()
        assertTrue(
            (0 until image.height).any { y -> (0 until image.width).any { x -> close(expected, image[x, y]) } },
            "$tag must paint the inherited color $expected",
        )
    }

    private fun close(
        a: Color,
        b: Color,
    ) = abs(a.red - b.red) < .03f && abs(a.green - b.green) < .03f && abs(a.blue - b.blue) < .03f

    private fun assertColor(
        expected: Color,
        actual: Color,
    ) = assertTrue(
        close(expected, actual),
        "$expected != $actual",
    )
}
