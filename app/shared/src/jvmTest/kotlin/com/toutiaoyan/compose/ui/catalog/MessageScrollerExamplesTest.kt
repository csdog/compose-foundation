package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.messageScrollerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class MessageScrollerExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(600.dp).height(800.dp), contentAlignment = Alignment.TopCenter) {
                        messageScrollerDoc.examples
                            .single { it.title == "Kotlin extension: Provider controls (Kotlin)" }
                            .preview()
                    }
                }
            }
        }
    }

    @Test fun usageExplainsCanonicalCompositionAndExposesRealStateApi() {
        assertEquals(7, messageScrollerDoc.apiSections.size)
        assertTrue(messageScrollerDoc.usageCode.contains("messageId"))
        assertTrue(messageScrollerDoc.usageCode.contains("MessageScrollerButton()"))
        assertFalse(messageScrollerDoc.usageCode.contains("DocIcon"))
        assertFalse(messageScrollerDoc.usageCode.contains("components.*"))
    }

    @Test fun jumpAndLoadingEarlierKeepTheSameReadingPosition() {
        show()
        rule.onNodeWithText("Jump to message 6").performClick()
        val top = rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top
        rule.onNodeWithText("Load earlier").performClick()
        rule.onNodeWithText("Load earlier").assertIsNotEnabled()
        assertEquals(top, rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top, 0.1f)
    }

    @Test fun releasedReaderStaysPutAndExplicitButtonResumesFollowing() {
        show()
        rule.onNodeWithText("Jump to message 6").performClick()
        val top = rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top
        rule.onNodeWithText("Append reply").performClick()
        assertEquals(top, rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top, 0.1f)
        rule.onNodeWithContentDescription("Scroll to end").performClick()
        rule.onNodeWithText("Message 11").assertIsDisplayed()
        rule.onNodeWithText("Append reply").performClick()
        rule.onNodeWithText("Message 12").assertIsDisplayed()
        rule.onNodeWithContentDescription("Scroll to end").assertDoesNotExist()
    }

    @Test fun resettingTheInteractivePanelCancelsTheOldReplyBeforeReusingItsId() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(432.dp).height(720.dp)) {
                        messageScrollerDoc.examples.single {
                            it.title == "Kotlin extension: Chat panel with streaming replies"
                        }.preview()
                    }
                }
            }
        }
        rule.mainClock.autoAdvance = false
        try {
            rule.onNodeWithText("Message MessageScroller…").performTextInput("First prompt")
            rule.onNodeWithContentDescription("Send message").performClick()
            rule.mainClock.advanceTimeBy(400)
            rule.onNodeWithContentDescription("Reset chat panel").performClick()
            rule.mainClock.advanceTimeByFrame()
            rule.onNodeWithText("First prompt").assertDoesNotExist()
            rule.onNodeWithText("Message MessageScroller…").performTextInput("Second prompt")
            rule.onNodeWithContentDescription("Send message").performClick()
            rule.mainClock.advanceTimeBy(200)
            // The old stream's 500ms thinking delay has expired, but the new
            // stream is still thinking. A stale write to its reused id hides this.
            rule.onNodeWithText("Thinking…").assertExists()
            rule.onNodeWithContentDescription("Reset chat panel").performClick()
            rule.mainClock.advanceTimeBy(10000)
            rule.onNodeWithText("Second prompt").assertDoesNotExist()
            rule.onNodeWithText("Thinking…").assertDoesNotExist()
            rule.onNodeWithText("Hi! Ask me anything about MessageScroller.").assertExists()
        } finally {
            rule.mainClock.autoAdvance = true
        }
    }

    @Test fun resettingTheDemoCancelsStreamingAndRestoresTheEmptyConversation() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(432.dp).height(720.dp)) {
                        messageScrollerDoc.examples.first().preview()
                    }
                }
            }
        }
        rule.mainClock.autoAdvance = false
        try {
            repeat(2) {
                rule.onNodeWithText("Morning, shadcn!").assertExists()
                rule.onNodeWithContentDescription("Send").performClick()
                rule.mainClock.advanceTimeBy(100)
                rule.onNodeWithContentDescription("Send").assertIsNotEnabled()
                rule.onNodeWithContentDescription("Reset conversation").performClick()
                rule.mainClock.advanceTimeByFrame()
                rule.onNodeWithText("Morning, shadcn!").assertExists()
                rule.onNodeWithContentDescription("Send").assertIsEnabled()
                // Start a fresh stream before the cancelled one would finish. Its
                // old completion must not enable Send while this stream is active.
                rule.onNodeWithContentDescription("Send").performClick()
                rule.mainClock.advanceTimeBy(100)
                rule.onNodeWithContentDescription("Send").assertIsNotEnabled()
                // A cancelled stream must neither write to this reused reply id
                // nor finish this new stream early. Advance only the test clock.
                var completed = false
                repeat(800) {
                    if (!completed) {
                        rule.mainClock.advanceTimeByFrame()
                        completed =
                            !rule.onNodeWithContentDescription("Send")
                                .fetchSemanticsNode().config.contains(SemanticsProperties.Disabled)
                    }
                }
                assertTrue(completed, "The new demo stream should finish")
                rule.mainClock.advanceTimeByFrame()
                rule.onNodeWithText("their position is preserved.", substring = true).assertExists()
                rule.onNodeWithContentDescription("Reset conversation").performClick()
                rule.mainClock.advanceTimeBy(6000)
                rule.onNodeWithText("Morning, shadcn!").assertExists()
                rule.onNodeWithContentDescription("Send").assertIsEnabled()
            }
        } finally {
            rule.mainClock.autoAdvance = true
        }
    }

    @Test fun officialPageOrderGuidesAndKotlinExtensions() {
        assertEquals(
            listOf(
                "Default",
                "Anchoring Turns",
                "Group Chat",
                "Keeping Context Visible",
                "Following the Live Edge",
                "Opening Saved Threads",
                "Avoiding a Flash on Reload",
                "Loading Earlier Messages",
                "Animating New Messages",
                "Jumping to Messages",
                "Tracking the Reader's Position",
                "Reading Scroll State",
            ),
            messageScrollerDoc.examples.take(12).map { it.title },
        )
        assertTrue(
            messageScrollerDoc.examples.drop(12).all { it.title.startsWith("Kotlin extension: ") },
        )
        assertEquals(
            "A chat scroll container that anchors turns, opens saved transcripts, " +
                "follows streamed responses, loads history without jumping, and jumps " +
                "to any message.",
            messageScrollerDoc.description,
        )
        val markdown = componentPageMarkdown(messageScrollerDoc)
        assertFalse(markdown.contains("## Examples"))
        val order =
            listOf(
                "## What Makes a Great Streaming Chat Experience",
                "## MessageScroller",
                "## Installation",
                "## Usage",
                "## Composition",
                "## Core Concepts",
                "### Anchoring Turns",
                "### Group Chat",
                "### Keeping Context Visible",
                "### Following the Live Edge",
                "### Opening Saved Threads",
                "### Avoiding a Flash on Reload",
                "### Loading Earlier Messages",
                "### Animating New Messages",
                "### Jumping to Messages",
                "### Tracking the Reader's Position",
                "### Reading Scroll State",
                "## Performance",
                "## Virtualization",
                "## Accessibility",
                "## Unstyled",
                "## API Reference",
            ).map { markdown.indexOf("\n" + it + "\n") }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "## Kotlin extension: Provider controls (Kotlin)")
        assertContains(markdown, "Never move the reader against their intent.")
        assertContains(markdown, "it has no lazy-layout adapter")
        assertContains(markdown, "the controller lifecycle and is required for scroll commands")
        assertContains(markdown, "this does not implement the web")
        assertFalse(markdown.contains("content is announced as a live region with additions semantics"))
    }
}
