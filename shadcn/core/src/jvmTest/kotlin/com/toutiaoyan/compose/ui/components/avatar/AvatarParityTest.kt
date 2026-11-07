@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.avatar

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class AvatarParityTest : ScreenshotTest() {
    @Test fun decodedImageHidesFallbackAndRemovalRestoresIt() {
        var image by mutableStateOf(true)
        val states = mutableListOf<AvatarImageLoadState>()
        val painter = ColorPainter(Color.Red)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Avatar {
                if (image) AvatarImage(painter, "Actual image", onLoadingStatusChange = { states += it })
                AvatarFallback("Fallback")
            }
        }
        composeRule.onNodeWithContentDescription("Actual image").assertIsDisplayed()
        composeRule.onNodeWithText("Fallback").assertDoesNotExist()
        assertEquals(AvatarImageLoadState.Success, states.last())
        composeRule.runOnIdle { image = false }
        composeRule.onNodeWithText("Fallback").assertIsDisplayed()
        assertEquals(AvatarImageLoadState.Idle, states.last())
    }

    @Test fun loadingDelayAndErrorAreDistinct() {
        val holder = AvatarImageLoadStateHolder().apply { state = AvatarImageLoadState.Loading }
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalAvatarImageLoadState provides holder) {
                AvatarFallback("Waiting", delayMs = 600)
            }
        }
        composeRule.onNodeWithText("Waiting").assertDoesNotExist()
        composeRule.mainClock.advanceTimeBy(650)
        composeRule.onNodeWithText("Waiting").assertIsDisplayed()
        composeRule.runOnIdle { holder.state = AvatarImageLoadState.Success }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithText("Waiting").assertDoesNotExist()
        composeRule.runOnIdle { holder.state = AvatarImageLoadState.Error }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithText("Waiting").assertIsDisplayed()
        composeRule.mainClock.autoAdvance = true
    }

    @Test fun fallbackUsesTwelveAndFourteenPixelNormalType() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Row { AvatarSize.entries.forEach { Avatar(it.name, size = it) } }
        }
        AvatarSize.entries.forEach { size ->
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText(
                size.name,
            ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
            val style = results.single().layoutInput.style
            assertEquals(if (size == AvatarSize.Sm) 12f else 14f, style.fontSize.value)
            assertEquals(FontWeight.Normal, style.fontWeight)
            assertEquals(0f, style.letterSpacing.value)
        }
    }

    @Test fun badgesMirrorToBottomLeftInRtlAndSmallHidesIcon() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row {
                    AvatarSize.entries.forEach { size ->
                        Avatar(Modifier.testTag("root-$size"), size) {
                            AvatarFallback("CN")
                            AvatarBadge(Modifier.testTag("badge-$size")) { Text { "icon-$size" } }
                        }
                    }
                }
            }
        }
        AvatarSize.entries.forEach { size ->
            val root = composeRule.onNodeWithTag("root-$size").fetchSemanticsNode().boundsInRoot
            val badge = composeRule.onNodeWithTag("badge-$size").fetchSemanticsNode().boundsInRoot
            assertEquals(root.left, badge.left, .1f)
            assertEquals(root.bottom, badge.bottom, .1f)
            assertEquals(
                when (size) {
                    AvatarSize.Sm -> 8f
                    AvatarSize.Default -> 10f
                    AvatarSize.Lg -> 12f
                },
                badge.width,
                .1f,
            )
        }
        composeRule.onNodeWithText("icon-Sm").assertDoesNotExist()
        composeRule.onNodeWithText("icon-Default").assertExists()
        composeRule.onNodeWithText("icon-Lg").assertExists()
    }

    @Test fun groupOverlapMirrorsInRtlWithoutChangingWidth() {
        var rtl by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                AvatarGroup(Modifier.testTag("group")) {
                    Avatar("First", Modifier.testTag("first"))
                    Avatar("Second", Modifier.testTag("second"))
                    AvatarGroupCount("+3")
                }
            }
        }

        fun left(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.left
        assertEquals(24f, left("second") - left("first"), .1f)
        assertEquals(80f, composeRule.onNodeWithTag("group").fetchSemanticsNode().boundsInRoot.width, .1f)
        composeRule.runOnIdle { rtl = true }
        assertEquals(-24f, left("second") - left("first"), .1f)
        assertEquals(80f, composeRule.onNodeWithTag("group").fetchSemanticsNode().boundsInRoot.width, .1f)
    }

    @Test fun groupCountTracksSingleMemberSizeAndAllowsExplicitOverride() {
        var memberSize by mutableStateOf(AvatarSize.Sm)
        setThemedContent(stylePreset = StylePreset.Nova) {
            AvatarGroup {
                Avatar(size = memberSize) { AvatarFallback("CN") }
                AvatarGroupCount("+3", Modifier.testTag("auto-count"))
                AvatarGroupCount("+4", Modifier.testTag("fixed-count"), size = AvatarSize.Lg)
            }
        }

        fun assertSizes(
            auto: Float,
            fixed: Float,
        ) {
            composeRule.onNodeWithTag("auto-count").assertWidthIsEqualTo(auto.dp).assertHeightIsEqualTo(auto.dp)
            composeRule.onNodeWithTag("fixed-count").assertWidthIsEqualTo(fixed.dp).assertHeightIsEqualTo(fixed.dp)
        }

        assertSizes(auto = 24f, fixed = 40f)
        composeRule.runOnIdle { memberSize = AvatarSize.Default }
        assertSizes(auto = 32f, fixed = 40f)
        composeRule.runOnIdle { memberSize = AvatarSize.Lg }
        assertSizes(auto = 40f, fixed = 40f)
        composeRule.runOnIdle { memberSize = AvatarSize.Sm }
        assertSizes(auto = 24f, fixed = 40f)
    }

    @Test fun groupCountUsesSmallPrecedenceWhenGroupSizesAreMixed() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AvatarGroup {
                Avatar(size = AvatarSize.Sm) { AvatarFallback("SM") }
                Avatar(size = AvatarSize.Default) { AvatarFallback("MD") }
                Avatar(size = AvatarSize.Lg) { AvatarFallback("LG") }
                AvatarGroupCount("+7", Modifier.testTag("mixed-count"))
            }
        }
        composeRule
            .onNodeWithTag("mixed-count")
            .assertWidthIsEqualTo(24.dp)
            .assertHeightIsEqualTo(24.dp)
    }
}
