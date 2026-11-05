package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
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
                        messageScrollerDoc.examples.single { it.title == "Kotlin extension: Provider controls (Kotlin)" }.preview()
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
        val order = listOf(
            "## What Makes a Great Streaming Chat Experience",
            "## MessageScroller",
            "## Core Concepts",
            "## Anchoring Turns",
            "## Group Chat",
            "## Keeping Context Visible",
            "## Following the Live Edge",
            "## Opening Saved Threads",
            "## Avoiding a Flash on Reload",
            "## Loading Earlier Messages",
            "## Animating New Messages",
            "## Jumping to Messages",
            "## Tracking the Reader's Position",
            "## Reading Scroll State",
            "## Performance",
            "## Virtualization",
            "## Accessibility",
            "## Unstyled",
            "## API Reference",
        ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "## Kotlin extension: Provider controls (Kotlin)")
        assertContains(markdown, "Never move the reader against their intent.")
    }
}
