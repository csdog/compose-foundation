@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.messagescroller

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.*

class MessageScrollerParityTest : ScreenshotTest() {
    private lateinit var state: MessageScrollerState
    private lateinit var scope: CoroutineScope
    private val ids = mutableStateListOf<String>().apply { addAll((0..9).map { "m$it" }) }
    private val heights = mutableStateMapOf<String, Int>()
    private val anchors = mutableStateListOf<String>()
    private var viewport by mutableStateOf(true)
    private var frameHeight by mutableIntStateOf(200)
    private var preserve by mutableStateOf(true)

    private fun show(
        auto: Boolean = false,
        opening: MessageScrollerPosition = MessageScrollerPosition.Start,
        padding: Dp = 0.dp,
        density: Float = 1f,
        spacing: Dp = 0.dp,
        itemPadding: Dp = 0.dp,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalDensity provides Density(density)) {
                state = rememberMessageScrollerState(autoScroll = auto, defaultScrollPosition = opening)
                scope = rememberCoroutineScope()
                MessageScrollerProvider(state) {
                    MessageScroller(Modifier.width(240.dp).height(frameHeight.dp).testTag("frame")) {
                        if (viewport) {
                            MessageScrollerViewport(
                                Modifier.testTag("viewport"),
                                preserveScrollOnPrepend =
                                preserve,
                            ) {
                                MessageScrollerContent(Modifier.padding(padding), spacing = spacing) {
                                    ids.forEach { id ->
                                        key(id) {
                                            MessageScrollerItem(
                                                modifier =
                                                    Modifier.padding(itemPadding),
                                                messageId =
                                                id,
                                                scrollAnchor =
                                                    id in anchors,
                                            ) {
                                                Box(
                                                    Modifier.fillMaxWidth().height((heights[id] ?: 60).dp),
                                                ) { Text { id } }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        MessageScrollerButton()
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun command(block: suspend () -> Unit) {
        composeRule.runOnIdle { scope.launch { block() } }
        composeRule.waitForIdle()
    }

    @Test fun shrinkingViewportKeepsTheExistingEndWithoutEnablingOutputFollowing() {
        show(opening = MessageScrollerPosition.End)
        assertEquals(400, state.scrollState.value)
        composeRule.runOnIdle { frameHeight = 160 }
        composeRule.waitForIdle()
        assertEquals(440, state.scrollState.value)
        assertFalse(state.scrollable.end)
        assertFalse(state.isFollowing)
        composeRule.runOnIdle { ids.add("reply") }
        composeRule.waitForIdle()
        assertEquals(440, state.scrollState.value)
        assertTrue(state.scrollable.end)
    }

    @Test fun shrinkingViewportPreservesAReaderWhoIsAwayFromTheEnd() {
        show()
        command { state.scrollToMessage("m4") }
        composeRule.runOnIdle { frameHeight = 160 }
        composeRule.waitForIdle()
        assertEquals(240, state.scrollState.value)
        assertFalse(state.isFollowing)
    }

    @Test fun providerDefaultsOpenAtEndWithoutFollowingNewContent() {
        show(opening = MessageScrollerPosition.End)
        assertEquals(400, state.scrollState.value)
        assertFalse(state.isFollowing)
        assertFalse(state.pendingScroll)
        composeRule.runOnIdle { ids.add("new") }
        composeRule.waitForIdle()
        assertEquals(400, state.scrollState.value)
        assertTrue(state.scrollable.end)
    }

    @Test fun optedInFollowingHandlesStreamGrowthAndPointerIntentReleasesIt() {
        show(auto = true, opening = MessageScrollerPosition.End)
        assertTrue(state.isFollowing)
        composeRule.runOnIdle { heights["m9"] = 120 }
        composeRule.waitForIdle()
        assertEquals(460, state.scrollState.value)
        composeRule.onNodeWithTag("viewport").performTouchInput { click(center) }
        assertFalse(state.isFollowing)
        composeRule.runOnIdle { ids.add("new") }
        composeRule.waitForIdle()
        assertEquals(460, state.scrollState.value)
    }

    @Test fun readerScrollReleasesAndExplicitEndResumesFollowing() {
        show(auto = true, opening = MessageScrollerPosition.End)
        composeRule.onNodeWithTag("viewport").performTouchInput { swipeDown() }
        assertFalse(state.isFollowing)
        composeRule.waitForIdle()
        composeRule.waitUntil(5_000) { !state.scrollState.isScrollInProgress }
        val value = state.scrollState.value
        composeRule.runOnIdle { ids.add("new") }
        composeRule.waitForIdle()
        assertEquals(value, state.scrollState.value)
        command { assertTrue(state.scrollToEnd()) }
        assertTrue(state.isFollowing)
        composeRule.runOnIdle { ids.add("newer") }
        composeRule.waitForIdle()
        assertEquals(state.scrollState.maxValue, state.scrollState.value)
    }

    @Test fun commandsHonorContentPaddingMarginAndAllFourAlignments() {
        show(padding = 12.dp)
        command { assertFalse(state.scrollToMessage("missing")) }
        command { assertTrue(state.scrollToMessage("m5", scrollMargin = 8.dp)) }
        assertEquals(292, state.scrollState.value)
        command { state.scrollToMessage("m5", MessageScrollerScrollAlign.Center, scrollMargin = 8.dp) }
        assertEquals(234, state.scrollState.value)
        command { state.scrollToMessage("m5", MessageScrollerScrollAlign.End, scrollMargin = 8.dp) }
        assertEquals(192, state.scrollState.value)
        command { state.scrollToMessage("m5", MessageScrollerScrollAlign.Nearest) }
        assertEquals(192, state.scrollState.value)
    }

    @Test fun rowPaddingBelongsToTheMeasuredMessageBoundary() {
        show(padding = 12.dp, itemPadding = 6.dp)
        command { state.scrollToMessage("m5", scrollMargin = 8.dp) }
        assertEquals(352, state.scrollState.value)
    }

    @Test fun prependingPreservesTheSameVisibleMessagePosition() {
        show()
        command { state.scrollToMessage("m4") }
        val top = composeRule.onNodeWithText("m4").fetchSemanticsNode().boundsInRoot.top
        composeRule.runOnIdle { ids.addAll(0, listOf("older1", "older2")) }
        composeRule.waitForIdle()
        assertEquals(360, state.scrollState.value)
        assertEquals(top, composeRule.onNodeWithText("m4").fetchSemanticsNode().boundsInRoot.top, 0.1f)
    }

    @Test fun prependPreservationCanBeDisabled() {
        preserve = false
        show()
        command { state.scrollToMessage("m4") }
        composeRule.runOnIdle { ids.addAll(0, listOf("older1", "older2")) }
        composeRule.waitForIdle()
        assertEquals(240, state.scrollState.value)
    }

    @Test fun reflowAboveTheReadingRowPreservesItsPosition() {
        show()
        command { state.scrollToMessage("m4") }
        val top = composeRule.onNodeWithText("m4").fetchSemanticsNode().boundsInRoot.top
        composeRule.runOnIdle { heights["m0"] = 120 }
        composeRule.waitForIdle()
        assertEquals(300, state.scrollState.value)
        assertEquals(top, composeRule.onNodeWithText("m4").fetchSemanticsNode().boundsInRoot.top, 0.1f)
    }

    @Test fun newTurnKeepsPreviousPeekAndSpacerIsConsumedByStreaming() {
        show(auto = true)
        composeRule.runOnIdle {
            anchors.add("turn")
            ids.add("turn")
        }
        composeRule.waitForIdle()
        assertEquals(536, state.scrollState.value)
        assertEquals(76, state.spacerHeight)
        assertEquals("turn", state.visibility.currentAnchorId)
        assertFalse(state.isFollowing)
        composeRule.runOnIdle { ids.add("reply") }
        composeRule.waitForIdle()
        assertEquals(536, state.scrollState.value)
        assertEquals(16, state.spacerHeight)
        composeRule.runOnIdle { heights["reply"] = 120 }
        composeRule.waitForIdle()
        assertEquals(0, state.spacerHeight)
        assertTrue(state.isFollowing)
        assertEquals(580, state.scrollState.value)
    }

    @Test fun openingAtLastAnchorOnlyPinsATurnTallerThanTheViewport() {
        anchors.add("m2")
        show(opening = MessageScrollerPosition.LastAnchor)
        assertEquals(56, state.scrollState.value)
        assertFalse(state.pendingScroll)
    }

    @Test fun shortLastTurnOpensAtEndWithoutAddingBlankSpace() {
        anchors.add("m8")
        show(opening = MessageScrollerPosition.LastAnchor)
        assertEquals(400, state.scrollState.value)
        assertEquals(0, state.spacerHeight)
    }

    @Test fun visibilityExcludesPreviousPeekBandAndTracksCurrentTurn() {
        anchors.addAll(listOf("m0", "m3"))
        show()
        assertEquals("m0", state.visibility.currentAnchorId)
        assertEquals(listOf("m1", "m2", "m3"), state.visibility.visibleMessageIds)
        command { state.scrollToMessage("m3") }
        assertEquals("m3", state.visibility.currentAnchorId)
        assertEquals(listOf("m4", "m5", "m6"), state.visibility.visibleMessageIds)
    }

    @Test fun anonymousAnchorDoesNotEraseTheLastNamedCurrentAnchor() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            state = rememberMessageScrollerState(defaultScrollPosition = MessageScrollerPosition.Start)
            MessageScrollerProvider(state) {
                MessageScroller(Modifier.width(240.dp).height(200.dp)) {
                    MessageScrollerViewport {
                        MessageScrollerContent(spacing = 0.dp) {
                            MessageScrollerItem(
                                messageId =
                                    "earlier",
                                scrollAnchor =
                                true,
                            ) { Box(Modifier.height(60.dp)) }
                            MessageScrollerItem(scrollAnchor = true) { Box(Modifier.height(60.dp)) }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        assertEquals("earlier", state.visibility.currentAnchorId)
        assertEquals(emptyList(), state.visibility.visibleMessageIds)
    }

    @Test fun commandsReturnFalseAfterViewportUnmountAndVisibilityClears() {
        show()
        composeRule.runOnIdle { viewport = false }
        composeRule.waitForIdle()
        command {
            assertFalse(state.scrollToStart())
            assertFalse(state.scrollToEnd())
        }
        assertEquals(emptyList(), state.visibility.visibleMessageIds)
    }

    @Test fun thresholdScalesWithDensityInsteadOfUsingPhysicalPixels() {
        show(density = 2f)
        command { state.scrollState.scrollTo(state.scrollState.maxValue - 12) }
        assertFalse(state.scrollable.end)
    }

    @Test fun keyboardEndCanReachTheLiveEdgeAndFollowLaterAppends() {
        show(auto = true)
        composeRule.onNodeWithTag("viewport").requestFocus()
        composeRule.onNodeWithTag("viewport").performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.waitForIdle()
        assertTrue(state.isFollowing)
        assertEquals(state.scrollState.maxValue, state.scrollState.value)
    }

    @Test fun commandsBeforeAViewportExistsReturnFalse() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            state = rememberMessageScrollerState()
            scope = rememberCoroutineScope()
            Text { "Waiting" }
        }
        command {
            assertFalse(state.scrollToStart())
            assertFalse(state.scrollToEnd())
            assertFalse(state.scrollToMessage("m1"))
        }
    }

    @Test fun buttonStopsAcceptingInputWhileItsExitAnimationIsStillMounted() {
        var visible by mutableStateOf(true)
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            MessageScrollerButton(visible = visible, onClick = { clicks++ })
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Scroll to end").assertHeightIsEqualTo(28.dp)
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { visible = false }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.onNodeWithContentDescription(
                "Scroll to end",
            ).assertIsNotEnabled().performTouchInput { click(center) }
            assertEquals(0, clicks)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun cancellingSmoothCommandClearsItsProgrammaticState() {
        show()
        composeRule.mainClock.autoAdvance = false
        var job: Job? = null
        try {
            composeRule.runOnIdle { job = scope.launch { state.scrollToEnd(MessageScrollerScrollBehavior.Smooth) } }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.runOnIdle {
                assertTrue(state.isAutoscrolling)
                job?.cancel()
            }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
        composeRule.waitForIdle()
        assertFalse(state.isAutoscrolling)
    }

    @Test fun cancelledOpeningPositionDoesNotKeepTheTranscriptTransparent() {
        var hold: Job? = null
        try {
            setThemedContent(stylePreset = StylePreset.Nova) {
                state = rememberMessageScrollerState(defaultScrollPosition = MessageScrollerPosition.End)
                scope = rememberCoroutineScope()
                LaunchedEffect(state) {
                    hold = currentCoroutineContext()[Job]
                    state.scrollState.scroll(MutatePriority.UserInput) { awaitCancellation() }
                }
                MessageScrollerProvider(state) {
                    MessageScroller(Modifier.width(240.dp).height(200.dp)) {
                        MessageScrollerViewport {
                            MessageScrollerContent {
                                repeat(10) { index ->
                                    MessageScrollerItem(messageId = "m$index") {
                                        Box(Modifier.height(60.dp)) { Text { "Message $index" } }
                                    }
                                }
                            }
                        }
                        MessageScrollerButton()
                    }
                }
            }
            composeRule.waitForIdle()
            composeRule.runOnIdle {
                assertTrue(state.mounted)
                assertFalse(state.pendingScroll)
            }
        } finally {
            composeRule.runOnIdle { hold?.cancel() }
        }
        command { assertTrue(state.scrollToEnd()) }
        assertEquals(616, state.scrollState.value)
    }

    @Test fun newerCommandKeepsItsFollowingModeWhenTheOlderAnimationIsCancelled() {
        show(auto = true)
        composeRule.mainClock.autoAdvance = false
        var older: Job? = null
        try {
            composeRule.runOnIdle { older = scope.launch { state.scrollToEnd(MessageScrollerScrollBehavior.Smooth) } }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.runOnIdle {
                assertTrue(state.isAutoscrolling)
                scope.launch { assertTrue(state.scrollToEnd()) }
            }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.runOnIdle {
                assertTrue(older?.isCancelled == true)
                assertFalse(state.isAutoscrolling)
                assertTrue(state.isFollowing)
                assertEquals(400, state.scrollState.value)
            }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
        composeRule.runOnIdle { ids.add("reply") }
        composeRule.waitForIdle()
        assertEquals(460, state.scrollState.value)
        assertTrue(state.mounted)
    }

    @Test fun pointerIntentCancelsAnActiveSmoothCommand() {
        show(auto = true)
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { scope.launch { state.scrollToEnd(MessageScrollerScrollBehavior.Smooth) } }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.runOnIdle { assertTrue(state.isAutoscrolling) }
            composeRule.onNodeWithTag("viewport").performTouchInput { down(center) }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.runOnIdle {
                assertFalse(state.isAutoscrolling)
                assertFalse(state.isFollowing)
            }
            composeRule.onNodeWithTag("viewport").performTouchInput { up() }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun pointerIntentCancelsAnAnchorWaitingForItsSpacerFrame() {
        show()
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle {
                anchors.add("turn")
                ids.add("turn")
            }
            composeRule.mainClock.advanceTimeUntil(timeoutMillis = 1000) { state.spacerHeight > 0 }
            val before = state.scrollState.value
            composeRule.onNodeWithTag("viewport").performTouchInput { down(center) }
            composeRule.mainClock.advanceTimeBy(100)
            composeRule.runOnIdle { assertEquals(before, state.scrollState.value) }
            composeRule.onNodeWithTag("viewport").performTouchInput { up() }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
        composeRule.waitForIdle()
        assertTrue(state.mounted)
    }

    @Test fun userMutationCancelsOneCorrectionWithoutDetachingTheController() {
        show()
        var hold: Job? = null
        try {
            composeRule.runOnIdle {
                hold =
                    scope.launch {
                        state.scrollState.scroll(MutatePriority.UserInput) { awaitCancellation() }
                    }
                anchors.add("turn")
                ids.add("turn")
            }
            composeRule.waitForIdle()
            composeRule.runOnIdle { assertTrue(state.mounted) }
        } finally {
            composeRule.runOnIdle { hold?.cancel() }
        }
        command { assertTrue(state.scrollToStart()) }
        composeRule.runOnIdle { ids.addAll(0, listOf("older1", "older2")) }
        composeRule.waitForIdle()
        assertTrue(state.mounted)
        assertEquals(120, state.scrollState.value)
    }

    private fun nova(dark: Boolean) =
        snapshot(
            "message_scroller_provider_nova",
            darkTheme =
            dark,
            stylePreset =
                StylePreset.Nova,
        ) {
            MessageScrollerProvider(defaultScrollPosition = MessageScrollerPosition.Start) {
                MessageScroller(Modifier.width(280.dp).height(220.dp)) {
                    MessageScrollerViewport {
                        MessageScrollerContent {
                            repeat(8) { index ->
                                MessageScrollerItem(messageId = "message-$index") {
                                    Box(Modifier.height(40.dp)) { Text { "Message ${index + 1}" } }
                                }
                            }
                        }
                    }
                    MessageScrollerButton()
                }
            }
        }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)

    @Test fun novaContentGapIsTwentyFour() {
        show(spacing = MessageScrollerDefaults.ContentSpacing)
        val first = composeRule.onNodeWithText("m0").fetchSemanticsNode().boundsInRoot
        val next = composeRule.onNodeWithText("m1").fetchSemanticsNode().boundsInRoot
        assertEquals(84f, next.top - first.top, 0.1f)
    }
}
