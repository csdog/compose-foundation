@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import kotlin.test.*

class ResizableInteractionTest : ScreenshotTest() {
    private fun fixture(
        orientation: Orientation = Orientation.Horizontal,
        rtl: Boolean = false,
        disabled: Boolean =
            false,
        initial: Float =
            0.5f,
        min: Float =
            0f,
        max: Float =
            1f,
        handleStyle: Style =
            Style,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                ResizablePanelGroup(
                    Modifier.size(320.dp, 200.dp),
                    orientation = orientation,
                    initialFraction = initial,
                    minFraction = min,
                    maxFraction = max,
                    disabled = disabled,
                ) { first, second, _ ->
                    ResizablePanel(first.testTag("first")) { Box(Modifier.size(10.dp)) }
                    ResizableHandle(modifier = Modifier.testTag("handle"), withHandle = true, style = handleStyle)
                    ResizablePanel(second.testTag("second")) { Box(Modifier.size(10.dp)) }
                }
            }
        }
    }

    private fun fraction() =
        composeRule.onNodeWithTag("handle").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo].current

    private fun mouseDrag(
        delta: Offset,
        startOffset: Offset = Offset.Zero,
    ) {
        composeRule.onNodeWithTag("handle").performMouseInput {
            moveTo(center + startOffset)
            press()
            moveBy(delta, delayMillis = 16)
            release()
        }
        composeRule.waitForIdle()
    }

    @Test(timeout = 15000)
    fun mouseDragWorksFourPixelsAwayFromTheHairline() {
        fixture()
        val before = composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot().width.value
        mouseDrag(Offset(40f, 0f), Offset(-4f, 0f))
        val after = composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot().width.value
        assertEquals(40f, after - before, 1f)
        assertEquals(0.5f + 40f / 319f, fraction(), 0.001f)
    }

    @Test(timeout = 15000)
    fun coarseTouchTargetWorksNinePixelsAwayFromTheHairline() {
        fixture()
        composeRule.onNodeWithTag("handle").performTouchInput {
            down(center + Offset(-9f, 0f))
            moveBy(Offset(40f, 0f))
            up()
        }
        composeRule.waitForIdle()
        assertEquals(0.5f + 40f / 319f, fraction(), 0.001f)
    }

    @Test(timeout = 15000)
    fun rtlPhysicalRightDragShrinksTheLogicalFirstPanel() {
        fixture(rtl = true)
        mouseDrag(Offset(40f, 0f))
        assertEquals(0.5f - 40f / 319f, fraction(), 0.001f)
    }

    @Test(timeout = 15000)
    fun verticalMouseDragChangesOnlyTheVerticalSplit() {
        fixture(orientation = Orientation.Vertical)
        val before = composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot().height.value
        mouseDrag(Offset(0f, 30f))
        val after = composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot().height.value
        assertEquals(30f, after - before, 1f)
        assertEquals(0.5f + 30f / 199f, fraction(), 0.001f)
    }

    @Test fun arrowKeysUseFivePercentAndHomeEndReachZeroAndFullWithoutWeightErrors() {
        fixture()
        val handle = composeRule.onNodeWithTag("handle")
        handle.requestFocus()
        handle.performKeyInput { pressKey(Key.DirectionRight) }
        assertEquals(0.55f, fraction(), 0.001f)
        handle.performKeyInput { pressKey(Key.DirectionUp) }
        assertEquals(0.55f, fraction(), 0.001f)
        handle.performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.DirectionRight)
            keyUp(Key.ShiftLeft)
        }
        assertEquals(0.6f, fraction(), 0.001f)
        handle.performKeyInput { pressKey(Key.MoveHome) }
        assertEquals(0f, fraction(), 0.001f)
        composeRule.onNodeWithTag("first").assertWidthIsEqualTo(0.dp)
        handle.performKeyInput { pressKey(Key.MoveEnd) }
        assertEquals(1f, fraction(), 0.001f)
        composeRule.onNodeWithTag("second").assertWidthIsEqualTo(0.dp)
    }

    @Test fun verticalKeysAndAccessibleAdjustmentUseTheSameLimits() {
        fixture(orientation = Orientation.Vertical)
        val handle = composeRule.onNodeWithTag("handle")
        handle.requestFocus()
        handle.performKeyInput { pressKey(Key.DirectionDown) }
        assertEquals(0.55f, fraction(), 0.001f)
        handle.performKeyInput { pressKey(Key.DirectionRight) }
        assertEquals(0.55f, fraction(), 0.001f)
        handle.performSemanticsAction(SemanticsActions.SetProgress) { assertTrue(it(0.75f)) }
        assertEquals(0.75f, fraction(), 0.001f)
    }

    @Test(timeout = 15000)
    fun disabledGroupCannotResizeAndHasNoAccessibleAdjustmentAction() {
        fixture(disabled = true)
        composeRule.onNodeWithTag("handle").assertIsNotEnabled()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.SetProgress))
        mouseDrag(Offset(40f, 0f))
        assertEquals(0.5f, fraction())
    }

    @Test fun customLimitsClampTheInitialValueAndKeyboardExtremes() {
        fixture(initial = 1.5f, min = 0.25f, max = 0.75f)
        assertEquals(0.75f, fraction())
        val handle = composeRule.onNodeWithTag("handle")
        handle.requestFocus()
        handle.performKeyInput { pressKey(Key.MoveHome) }
        assertEquals(0.25f, fraction())
        handle.performKeyInput { pressKey(Key.MoveEnd) }
        assertEquals(0.75f, fraction())
    }

    @Test(timeout = 15000)
    fun customDividerThicknessIsExcludedFromTheResizeDenominator() {
        fixture(handleStyle = Style { width(5.dp) })
        mouseDrag(Offset(31.5f, 0f))
        assertEquals(0.6f, fraction(), 0.001f)
    }

    @Test(timeout = 15000)
    fun changingOrientationCancelsAnAlreadyPressedPointer() {
        var orientation by mutableStateOf(Orientation.Horizontal)
        setThemedContent(stylePreset = StylePreset.Nova) {
            ResizablePanelGroup(Modifier.size(320.dp, 200.dp), orientation = orientation) { first, second, _ ->
                ResizablePanel(first) {}
                ResizableHandle(modifier = Modifier.testTag("handle"))
                ResizablePanel(second) {}
            }
        }
        composeRule.onNodeWithTag("handle").performMouseInput {
            moveTo(center)
            press()
        }
        composeRule.runOnIdle { orientation = Orientation.Vertical }
        composeRule.onNodeWithTag("handle").performMouseInput {
            moveBy(Offset(0f, 30f))
            release()
        }
        assertEquals(0.5f, fraction(), 0.001f)
        mouseDrag(Offset(0f, 20f))
        assertEquals(0.5f + 20f / 199f, fraction(), 0.001f)
    }
}
