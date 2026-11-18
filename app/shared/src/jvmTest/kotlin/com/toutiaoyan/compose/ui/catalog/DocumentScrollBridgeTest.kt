package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentScrollBridgeTest {
    @get:Rule val rule = createComposeRule()

    private val state = ScrollState(0)
    private var height by mutableStateOf(600)
    private var bound by mutableStateOf(true)
    private lateinit var bridge: DocumentScrollBridge

    private fun document(restore: Double? = 490.0) {
        bridge = DocumentScrollBridge(state, 1f, restore)
        rule.setContent {
            if (bound) {
                LaunchedEffect(bridge) {
                    snapshotFlow { state.maxValue }.first { it != Int.MAX_VALUE }
                    snapshotFlow { Triple(state.maxValue, state.value, state.isScrollInProgress) }.collect {
                        bridge.updateCompose()
                    }
                }
            }
            Box(Modifier.requiredSize(200.dp).verticalScroll(state)) {
                Spacer(Modifier.height(height.dp))
            }
        }
        rule.waitForIdle()
    }

    @Test fun repeatedEarlyReloadRetainsOriginalTargetThroughPublishedClamp() {
        document()
        var captured = 490.0
        repeat(2) {
            rule.runOnIdle {
                bridge.publishedNative(400.0)
                assertNull(bridge.nativeScroll(400.0))
                captured = bridge.positionForCapture(400.0)
                assertEquals(490.0, captured)
                assertEquals(490.0, bridge.positionForDeparture())
                bound = false
            }
            rule.waitForIdle()
            rule.runOnIdle {
                bridge = DocumentScrollBridge(state, 1f, captured)
                bound = true
            }
            rule.waitForIdle()
            assertEquals(400, state.value)
        }
        rule.runOnIdle { height = 800 }
        rule.waitForIdle()
        assertEquals(490, state.value)
        rule.runOnIdle {
            bridge.publishedNative(490.0)
            state.dispatchRawDelta(20f)
            assertEquals(510.0, bridge.positionForCapture(490.0))
            assertEquals(510.0, bridge.positionForDeparture())
        }
    }

    @Test fun clampedTraversalDepartureRetainsOwnedTargetWithoutDestinationNativeSample() {
        document()
        val positions = DocumentHistoryPositions()
        rule.runOnIdle {
            bridge.publishedNative(400.0)
            positions.record("outgoing", bridge.positionForDeparture())
            positions.depart("outgoing", sameRouteTraversal = true)
            assertEquals(490.0, positions.restoreForTraversal("outgoing", true, false, null))
            bound = false
        }
        rule.waitForIdle()
        rule.runOnIdle {
            bridge = DocumentScrollBridge(state, 1f, positions.restoreForTraversal("outgoing", true, false, null))
            bound = true
        }
        rule.waitForIdle()
        rule.runOnIdle { height = 800 }
        rule.waitForIdle()
        assertEquals(490, state.value)
    }

    @Test fun nativeInputBeforeCallbackCancelsPendingRestoreAtCapture() {
        document()
        rule.runOnIdle {
            bridge.publishedNative(400.0)
            assertEquals(360.0, bridge.positionForCapture(360.0))
            height = 800
        }
        rule.waitForIdle()
        assertEquals(400, state.value, "Captured real native input releases the old restore before its callback")
    }

    @Test fun captureReadsLiveReturnToPublishedPositionWhileDepartureUsesLatestCallback() {
        document()
        rule.runOnIdle {
            bridge.publishedNative(400.0)
            assertEquals(360, bridge.nativeScroll(360.0))
            assertEquals(400.0, bridge.positionForCapture(400.0), "Live position can return to the last publication")
            assertEquals(360.0, bridge.positionForDeparture())
            assertEquals(350.0, bridge.positionForCapture(350.0), "Actual newer native position before callback")
            assertEquals(400, bridge.nativeScroll(400.0), "A return callback supersedes queued native input")
            assertEquals(400.0, bridge.positionForDeparture())
            state.dispatchRawDelta(-20f)
            assertEquals(380.0, bridge.positionForCapture(400.0))
            assertEquals(380.0, bridge.positionForDeparture())
            height = 800
        }
        rule.waitForIdle()
        assertEquals(380, state.value)
    }

    @Test fun newerComposeInputBeforeCollectorReleasesRestoreAndShortPageReconstructsInput() {
        document()
        var captured = 0.0
        rule.runOnIdle {
            bridge.publishedNative(400.0)
            state.dispatchRawDelta(-30f)
            captured = bridge.positionForCapture(400.0)
            assertEquals(370.0, captured)
            assertEquals(370.0, bridge.positionForDeparture())
            bound = false
        }
        rule.waitForIdle()
        rule.runOnIdle {
            bridge = DocumentScrollBridge(state, 1f, captured)
            bound = true
        }
        rule.waitForIdle()
        assertEquals(370, state.value)
        rule.runOnIdle { height = 800 }
        rule.waitForIdle()
        assertEquals(370, state.value, "A canceled target must not return after reconstruction and growth")
    }

    @Test fun shortExtentRetainsRestoreThroughEchoAndGrowthThenReleasesIt() {
        document()
        assertEquals(400, state.value)
        rule.runOnIdle {
            bridge.publishedNative(400.0)
            assertNull(bridge.nativeScroll(400.0), "Own native echo must not cancel a pending restore")
            height = 650
        }
        rule.waitForIdle()
        assertEquals(450, state.value)
        rule.runOnIdle { height = 800 }
        rule.waitForIdle()
        assertEquals(490, state.value)
        rule.runOnIdle {
            state.dispatchRawDelta(20f)
            height = 900
        }
        rule.waitForIdle()
        assertEquals(510, state.value, "Reached restore must not reclaim the position")
    }

    @Test fun permanentlyShortDocumentRemainsInteractiveAndInputCancelsPendingRestore() {
        document()
        rule.runOnIdle { state.dispatchRawDelta(-30f) }
        rule.waitForIdle()
        assertEquals(370, state.value)
        rule.runOnIdle { height = 900 }
        rule.waitForIdle()
        assertEquals(370, state.value)
    }

    @Test fun nativeInputCancelsBeforeComposeFeedbackAndBindingDisposalStopsRetries() {
        document()
        rule.runOnIdle {
            bridge.publishedNative(400.0)
            assertEquals(360, bridge.nativeScroll(360.0))
            height = 800
        }
        rule.waitForIdle()
        assertEquals(400, state.value, "Pending native feedback must cancel restore synchronously")
        rule.runOnIdle { runBlocking { state.scrollTo(360) } }
        rule.waitForIdle()
        rule.runOnIdle {
            bound = false
            height = 600
        }
        rule.waitForIdle()
        rule.runOnIdle { height = 1000 }
        rule.waitForIdle()
        assertEquals(360, state.value)
    }

    @Test fun disposalWhileRestoreIsPendingPreventsLateGrowthFromSeeking() {
        document()
        assertEquals(400, state.value)
        rule.runOnIdle { bound = false }
        rule.waitForIdle()
        rule.runOnIdle { height = 1000 }
        rule.waitForIdle()
        assertEquals(400, state.value)
    }

    @Test fun traversalDepartureKeepsOnlyAuthorizedPendingNativeAndRetriesSavedExtent() {
        document(restore = null)
        val positions = DocumentHistoryPositions()
        rule.runOnIdle {
            bridge.publishedNative(0.0)
            assertEquals(370, bridge.nativeScroll(370.0))
            // Native destination y must not be sampled after popstate; this needs no window read.
            positions.record("outgoing", bridge.positionForDeparture())
            positions.depart("outgoing", sameRouteTraversal = true)
            assertEquals(370.0, positions.restoreForTraversal("outgoing", true, false, null))
            state.dispatchRawDelta(40f)
            assertEquals(40.0, bridge.positionForDeparture(), "New Compose movement supersedes pending native")
            bound = false
            height = 500
        }
        rule.waitForIdle()
        rule.runOnIdle {
            bridge = DocumentScrollBridge(state, 1f, positions.restoreForTraversal("outgoing", true, false, null))
            bound = true
        }
        rule.waitForIdle()
        assertEquals(300, state.value, "Traversal restore first clamps to current extent")
        rule.runOnIdle {
            bridge.publishedNative(300.0)
            assertNull(bridge.nativeScroll(300.0))
            height = 800
        }
        rule.waitForIdle()
        assertEquals(370, state.value, "Shared pending restore follows growth to traversal sample")
    }

    @Test fun pagehideReconcilesNativeBeforeCollectorAndRejectsOldEchoAfterComposeAdvance() {
        document(restore = null)
        rule.runOnIdle {
            bridge.publishedNative(0.0)
            assertEquals(170.0, bridge.positionForCapture(170.0), "Even before the native event callback")
            assertEquals(170, bridge.nativeScroll(170.0))
            assertEquals(170.0, bridge.positionForCapture(170.0), "Before asynchronous feedback commits")
            state.dispatchRawDelta(40f)
            assertEquals(40.0, bridge.positionForCapture(0.0), "Old native echo must not replace newer Compose")
            assertEquals(80, bridge.nativeScroll(80.0))
            assertEquals(80.0, bridge.positionForCapture(80.0), "Observed native movement follows Compose")
        }
    }
}
