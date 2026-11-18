package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DocAnchorScrollTest {
    @get:Rule val rule = createComposeRule()

    private val scroll = ScrollState(120)
    private lateinit var scope: CoroutineScope

    private fun setDocument(
        anchors: DocAnchors? = null,
        attach: () -> Boolean = { false },
    ) {
        rule.setContent {
            scope = rememberCoroutineScope()
            Column(
                Modifier.requiredSize(200.dp).verticalScroll(scroll)
                    .onGloballyPositioned { anchors?.content = it },
            ) {
                Spacer(Modifier.height(500.dp))
                if (anchors != null && attach()) Spacer(Modifier.height(20.dp).docAnchor(anchors, "missing"))
                Spacer(Modifier.height(1500.dp))
            }
        }
        rule.waitForIdle()
    }

    private fun activeWheel(): Job =
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            scroll.scroll(MutatePriority.UserInput) {
                scrollBy(-10f)
                awaitCancellation()
            }
        }

    @Test fun lateAnchorLayoutFollowsUntilUserScrollAndSameFragmentCanSeekAgain() {
        val anchors = DocAnchors()
        val document = ScrollState(0)
        var above by mutableStateOf(400)
        var revision by mutableStateOf(0)
        rule.setContent {
            scope = rememberCoroutineScope()
            LaunchedEffect(revision) {
                document.followDocumentAnchor(anchors, "target", animated = false)
            }
            Column(
                Modifier.requiredSize(200.dp).verticalScroll(document)
                    .onGloballyPositioned { anchors.content = it },
            ) {
                Spacer(Modifier.height(above.dp))
                Spacer(Modifier.height(20.dp).docAnchor(anchors, "target"))
                Spacer(Modifier.height(1600.dp))
            }
        }
        rule.waitForIdle()
        assertEquals(400, document.value)
        rule.runOnIdle { above += 15 }
        rule.waitForIdle()
        assertEquals(415, document.value, "Late reflow must correct a resting automatic seek")
        rule.runOnIdle { above += 41 }
        rule.waitForIdle()
        assertEquals(456, document.value)
        rule.runOnIdle {
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                document.scroll(MutatePriority.UserInput) { scrollBy(170f) }
            }
        }
        rule.waitForIdle()
        rule.runOnIdle { above += 60 }
        rule.waitForIdle()
        assertEquals(626, document.value, "Later layout must not reclaim the user's position")
        rule.runOnIdle { revision++ }
        rule.waitForIdle()
        assertEquals(516, document.value, "An explicit same-fragment navigation starts a new seek")
    }

    @Test fun freshSeekYieldsBeforeAnchorAttachmentAndCanceledNavigationCannotSeekLater() {
        val anchors = DocAnchors()
        var attached by mutableStateOf(false)
        setDocument(anchors) { attached }
        lateinit var seek: Job
        rule.runOnIdle {
            seek =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    scroll.followDocumentAnchor(anchors, "missing", animated = false)
                }
            scroll.dispatchRawDelta(30f)
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertTrue(seek.isCancelled, "User movement cancels the pending first anchor")
            assertEquals(150, scroll.value)
            seek =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    scroll.followDocumentAnchor(anchors, "missing", animated = false)
                }
            seek.cancel()
            attached = true
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertTrue(seek.isCancelled, "Location effect cancellation must dispose the pending seek")
            assertEquals(150, scroll.value)
        }
    }

    @Test fun anchorReplacesActiveWheelAndReachesItsTarget() {
        setDocument()
        lateinit var wheel: Job
        lateinit var legacySeek: Job
        lateinit var anchorSeek: Job
        rule.runOnIdle {
            wheel = activeWheel()
            legacySeek = scope.launch(start = CoroutineStart.UNDISPATCHED) { scroll.animateScrollTo(800) }
        }
        rule.runOnIdle {
            assertTrue(legacySeek.isCancelled, "Default-priority animation loses to an active UserInput mutation")
            assertTrue(wheel.isActive)
            assertEquals(110, scroll.value)
            anchorSeek = scope.launch { scroll.seekDocumentAnchor(800, animated = true) }
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertTrue(wheel.isCancelled)
            assertTrue(anchorSeek.isCompleted)
            assertFalse(anchorSeek.isCancelled)
            assertEquals(800, scroll.value)
        }
    }

    @Test fun laterWheelInterruptsAnchorAndInstantAnchorStillAcceptsLaterInput() {
        setDocument()
        rule.mainClock.autoAdvance = false
        lateinit var anchorSeek: Job
        lateinit var laterWheel: Job
        var interruptedAt = 0
        var consumed = 0f
        rule.runOnIdle {
            anchorSeek =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    scroll.seekDocumentAnchor(800, animated = true)
                }
        }
        rule.mainClock.advanceTimeBy(64)
        rule.runOnIdle {
            assertTrue(anchorSeek.isActive)
            assertTrue(scroll.value in 121..799, "Interrupt only after the anchor animation has advanced")
            interruptedAt = scroll.value
            laterWheel =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    scroll.scroll(MutatePriority.UserInput) { consumed = scrollBy(20f) }
                }
        }
        rule.mainClock.advanceTimeUntil { anchorSeek.isCompleted && laterWheel.isCompleted }
        lateinit var wheel: Job
        lateinit var instantSeek: Job
        rule.runOnIdle {
            assertTrue(anchorSeek.isCancelled, "Later input must remain able to interrupt the anchor animation")
            assertFalse(laterWheel.isCancelled)
            assertEquals(20f, consumed)
            assertEquals(interruptedAt + 20, scroll.value)
            wheel = activeWheel()
            instantSeek =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    scroll.seekDocumentAnchor(800, animated = false)
                }
        }
        rule.mainClock.advanceTimeUntil { wheel.isCompleted && instantSeek.isCompleted }
        lateinit var independentWheel: Job
        rule.runOnIdle {
            assertTrue(wheel.isCancelled)
            assertFalse(instantSeek.isCancelled)
            assertEquals(800, scroll.value)
            independentWheel =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    scroll.scroll(MutatePriority.UserInput) { consumed = scrollBy(120f) }
                }
        }
        rule.mainClock.advanceTimeUntil { independentWheel.isCompleted }
        rule.runOnIdle {
            assertFalse(independentWheel.isCancelled)
            assertEquals(120f, consumed)
            assertEquals(920, scroll.value)
        }
    }
}
