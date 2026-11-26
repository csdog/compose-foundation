package com.toutiaoyan.compose.ui.components.carousel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.input.Input
import kotlin.test.Test
import kotlin.test.assertEquals

class CarouselKeyboardTest : ScreenshotTest() {
    @Test fun reachingEitherEdgeMovesFocusToTheEnabledControl() {
        setThemedContent {
            val state = rememberPagerState { 2 }
            CarouselRoot(state, Modifier.size(260.dp, 100.dp)) {
                CarouselContent {}
                CarouselPrevious()
                CarouselNext()
            }
        }
        val next = composeRule.onNodeWithContentDescription("Next slide")
        val previous = composeRule.onNodeWithContentDescription("Previous slide")

        previous.assertIsNotEnabled()
        next.requestFocus().performKeyInput { pressKey(Key.Enter) }
        next.assertIsNotEnabled()
        previous.assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        previous.assertIsNotEnabled()
        next.assertIsFocused()
    }

    @Test fun anEditableSlideHandlesItsOwnArrowKey() {
        var value by mutableStateOf("")
        var activePage = -1
        setThemedContent {
            val state = rememberPagerState(initialPage = 1) { 3 }
            androidx.compose.runtime.SideEffect { activePage = state.currentPage }
            CarouselRoot(state, Modifier.size(260.dp, 100.dp)) {
                CarouselContent { page ->
                    if (page == 1) {
                        Input(value, { value = it }, Modifier.testTag("editable"))
                    }
                }
            }
        }
        val input = composeRule.onNodeWithTag("editable")
        input.performClick().performTextInput("abc")
        input.performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.runOnIdle { assertEquals(1, activePage) }
        input.performTextInput("X")
        composeRule.runOnIdle { assertEquals("abXc", value) }
    }

    @Test fun thePreviousArrowBubblesWhenThePagerIsAlreadyAtItsStart() {
        var parentPresses = 0
        setThemedContent {
            val state = rememberPagerState { 3 }
            Box(
                Modifier.onKeyEvent {
                    if (it.key == Key.DirectionLeft && it.type == KeyEventType.KeyDown) {
                        parentPresses++
                        true
                    } else {
                        false
                    }
                },
            ) {
                CarouselRoot(state, Modifier.size(260.dp, 100.dp)) {
                    CarouselContent {}
                    CarouselNext()
                }
            }
        }
        composeRule.onNodeWithContentDescription("Next slide")
            .requestFocus()
            .performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.runOnIdle { assertEquals(1, parentPresses) }
    }
}
