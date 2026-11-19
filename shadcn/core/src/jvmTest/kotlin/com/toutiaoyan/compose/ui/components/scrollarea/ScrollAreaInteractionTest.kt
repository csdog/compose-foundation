@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import com.toutiaoyan.compose.ui.components.text.Text
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import kotlin.test.*

class ScrollAreaInteractionTest : ScreenshotTest() {
    private lateinit var state: ScrollAreaState

    private fun fixture(
        orientation: ScrollAreaOrientation = ScrollAreaOrientation.Vertical,
        rtl: Boolean = false,
        contentHeight: Int = 2000,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ScrollArea(
                Modifier.size(200.dp).testTag("area"),
                orientation = orientation,
                dir = if (rtl) ScrollDir.Rtl else ScrollDir.Ltr,
            ) {
                val current = scrollAreaState()
                SideEffect { state = current }
                Box(Modifier.size(1000.dp, contentHeight.dp))
            }
        }
        advance()
    }

    private fun advance() {
        composeRule.mainClock.advanceTimeBy(64)
        composeRule.waitForIdle()
    }

    private fun node(name: String) = composeRule.onNodeWithTag("scroll-area-$name", useUnmergedTree = true)

    private fun drag(
        name: String,
        delta: Offset,
    ) {
        node(name).performMouseInput {
            moveTo(center)
            press()
            moveBy(delta)
            release()
        }
        advance()
    }

    @Test fun novaTrackAndThumbHaveSourceInsetsAndAreVisibleBeforeHover() {
        fixture()
        node("vertical-track").assertIsDisplayed().assertWidthIsEqualTo(10.dp)
        node("vertical-thumb").assertWidthIsEqualTo(7.dp)
        node("viewport").requestFocus().assertIsFocused()
    }

    @Test fun minimumThumbStaysInsideTrackAtLastPixel() {
        fixture(contentHeight = 100000)
        composeRule.runOnIdle { state.vertical.dispatchRawDelta(state.vertical.maxValue.toFloat()) }
        advance()
        node("vertical-thumb").assertHeightIsEqualTo(16.dp)
        val track = node("vertical-track").getUnclippedBoundsInRoot()
        val thumb = node("vertical-thumb").getUnclippedBoundsInRoot()
        assertEquals(track.bottom.value - 1, thumb.bottom.value, 0.5f)
    }

    @Test(timeout = 15000)
    fun realMouseThumbDragScrollsContentByTravelRatio() {
        fixture()
        val height = node("vertical-thumb").getUnclippedBoundsInRoot().height.value
        drag("vertical-thumb", Offset(0f, 40f))
        assertEquals(40f * state.vertical.maxValue / (198 - height), state.vertical.value.toFloat(), 1f)
    }

    @Test(timeout = 15000)
    fun trackPressCentersThumbAndSupportsContinuingDrag() {
        fixture()
        node("vertical-track").performMouseInput {
            moveTo(Offset(center.x, height - 20f))
            press()
        }
        advance()
        val pressed = state.vertical.value
        assertTrue(pressed > state.vertical.maxValue * 0.8f)
        node("vertical-track").performMouseInput {
            moveBy(Offset(0f, -30f))
            release()
        }
        advance()
        assertTrue(state.vertical.value < pressed)
    }

    @Test(timeout = 15000)
    fun wheelAndTouchMoveActualViewport() {
        fixture()
        node("viewport").performMouseInput {
            moveTo(center)
            scroll(4f)
        }
        advance()
        assertTrue(state.vertical.value > 0)
        val viewportWheel = state.vertical.value
        node("vertical-track").performMouseInput {
            moveTo(center)
            scroll(4f)
        }
        advance()
        assertTrue(state.vertical.value > viewportWheel, "Wheel over scrollbar must also move the viewport")
        val wheel = state.vertical.value
        node("viewport").performTouchInput { swipeUp(durationMillis = 160) }
        advance()
        assertTrue(state.vertical.value > wheel)
    }

    @Test fun keyboardHomeEndArrowsPagesAndSpaceResizeScrollPosition() {
        fixture()
        node("viewport").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        advance()
        assertEquals(40, state.vertical.value)
        node("viewport").performKeyInput { pressKey(Key.PageDown) }
        advance()
        assertEquals(240, state.vertical.value)
        node("viewport").performKeyInput { pressKey(Key.MoveEnd) }
        advance()
        assertEquals(state.vertical.maxValue, state.vertical.value)
        node("viewport").performKeyInput { pressKey(Key.MoveHome) }
        advance()
        assertEquals(0, state.vertical.value)
        node("viewport").performKeyInput { pressKey(Key.Spacebar) }
        advance()
        assertEquals(200, state.vertical.value)
        node("viewport").performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Spacebar)
            keyUp(Key.ShiftLeft)
        }
        advance()
        assertEquals(0, state.vertical.value)
    }

    @Test(timeout = 15000)
    fun rtlHorizontalThumbAndArrowFollowPhysicalDirection() {
        fixture(ScrollAreaOrientation.Horizontal, rtl = true)
        val track = node("horizontal-track").getUnclippedBoundsInRoot()
        val thumb = node("horizontal-thumb").getUnclippedBoundsInRoot()
        assertEquals(track.right.value - 1, thumb.right.value, 0.5f)
        drag("horizontal-thumb", Offset(-30f, 0f))
        assertTrue(state.horizontal.value > 0)
        val before = state.horizontal.value
        node("viewport").requestFocus().performKeyInput { pressKey(Key.DirectionLeft) }
        advance()
        assertEquals(before + 40, state.horizontal.value)
    }

    @Test fun twoAxisTracksReserveCornerAndDoNotIntersect() {
        fixture(ScrollAreaOrientation.Both)
        val vertical = node("vertical-track").getUnclippedBoundsInRoot()
        val horizontal = node("horizontal-track").getUnclippedBoundsInRoot()
        assertEquals(190f, vertical.height.value)
        assertEquals(190f, horizontal.width.value)
        assertTrue(vertical.bottom <= horizontal.top)
        assertTrue(horizontal.right <= vertical.left)
    }

    @Test fun fittingContentHasNoScrollbarAndNoKeyboardFocusTarget() {
        fixture(contentHeight = 100)
        node("vertical-track").assertDoesNotExist()
        assertEquals(0, state.vertical.maxValue)
    }

    @Test fun descendantTextEditorKeepsHomeEndKeysInsteadOfScrollingAncestor() {
        var value by mutableStateOf(TextFieldValue("abc", TextRange(0)))
        setThemedContent(stylePreset = StylePreset.Nova) {
            ScrollArea(Modifier.size(200.dp)) {
                val current = scrollAreaState()
                SideEffect { state = current }
                Column {
                    BasicTextField(value, { value = it }, modifier = Modifier.testTag("editor"))
                    Spacer(Modifier.height(2000.dp))
                }
            }
        }
        advance()
        composeRule.onNodeWithTag("editor").requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        advance()
        assertEquals(TextRange(3), value.selection)
        assertEquals(0, state.vertical.value)
    }

    @Test
    fun lazyArrangementSpacingCanMakeOtherwiseShortItemsScrollable() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            val lazy = rememberLazyListState()
            LazyScrollArea(lazy, Modifier.size(200.dp), type = ScrollType.Auto) {
                LazyColumn(
                    state = lazy,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                ) {
                    items(4) { Text(Modifier.height(24.dp)) { "Item $it" } }
                }
            }
        }
        advance()
        node("vertical-thumb").assertIsDisplayed()
    }

    @Test
    fun reverseLazyThumbStartsAtBottomAndDragsTowardEarlierScreenRows() {
        lateinit var state: LazyListState
        setThemedContent(stylePreset = StylePreset.Nova) {
            val lazy = rememberLazyListState()
            SideEffect { state = lazy }
            LazyScrollArea(lazy, Modifier.size(200.dp), type = ScrollType.Auto) {
                LazyColumn(state = lazy, modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                    items(100) { Text(Modifier.height(24.dp)) { "Item $it" } }
                }
            }
        }
        advance()
        val track = node("vertical-track").getUnclippedBoundsInRoot()
        val thumb = node("vertical-thumb").getUnclippedBoundsInRoot()
        assertTrue(thumb.bottom in (track.bottom - 2.dp)..track.bottom)
        drag("vertical-thumb", Offset(0f, -40f))
        assertTrue(state.firstVisibleItemIndex > 0)
    }

    @Test(timeout = 15000)
    fun sharedThumbStillDragsActualLazyList() {
        lateinit var lazy: LazyListState
        setThemedContent(stylePreset = StylePreset.Nova) {
            val current = rememberLazyListState()
            SideEffect { lazy = current }
            LazyScrollArea(current, Modifier.size(200.dp), type = ScrollType.Auto) {
                LazyColumn(state = current, modifier = Modifier.fillMaxSize()) {
                    items(100) { index -> Text(Modifier.height(24.dp)) { "Item $index" } }
                }
            }
        }
        advance()
        drag("vertical-thumb", Offset(0f, 40f))
        assertTrue(lazy.firstVisibleItemIndex > 0)
        node("vertical-track").performMouseInput {
            moveTo(Offset(center.x, height - 5f))
            press()
            release()
        }
        advance()
        assertTrue(
            lazy.firstVisibleItemIndex > 80,
            "First visible item after bottom track press: ${lazy.firstVisibleItemIndex}",
        )
    }
}
