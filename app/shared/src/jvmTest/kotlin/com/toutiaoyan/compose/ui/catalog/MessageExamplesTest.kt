package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.messageDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class MessageExamplesTest {
    @get:Rule val rule = createComposeRule()
    private lateinit var inputModeManager: InputModeManager

    private fun show(title: String) {
        rule.setContent {
            inputModeManager = LocalInputModeManager.current
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(1000.dp), contentAlignment = Alignment.TopCenter) {
                        messageDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
    }

    @Test fun sixOfficialRecipesAndSixApiPartsArePresent() {
        assertEquals(
            listOf(
                "Default",
                "Avatar",
                "Group",
                "Header and Footer",
                "Actions",
                "Attachment",
            ),
            messageDoc.examples.take(6).map { it.title },
        )
        assertEquals(6, messageDoc.apiSections.size)
        assertTrue(
            messageDoc.examples.drop(6).all { it.title.startsWith("Kotlin extension: ") },
        )
        assertFalse(messageDoc.usageCode.contains("components.*"))
    }

    @Test fun defaultHasTheActualConversationReactionAndLiveTypingMarker() {
        show("Default")
        listOf(
            "Deploying to prod real quick.",
            "It's 4:55 PM. On a Friday.",
            "It's a one-line change.",
            "It's always a one-line change 😭.",
            "Alright, let me take a look.",
            "Delivered",
            "Oliver is typing...",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        rule.onNodeWithContentDescription("Reactions: thumbs up").assertExists()
        rule.onAllNodes(
            SemanticsMatcher.expectValue(
                SemanticsProperties.LiveRegion,
                LiveRegionMode.Polite,
            ),
        ).assertCountEquals(1)
        rule.onAllNodesWithContentDescription("@me", useUnmergedTree = true).assertCountEquals(2)
    }

    @Test fun avatarExampleHasBothSidesAndGroupedLongText() {
        show("Avatar")
        rule.onNodeWithText("The build failed during dependency installation.").assertIsDisplayed()
        rule.onNodeWithText("Can you share the exact error?").assertIsDisplayed()
        rule.onNodeWithText(
            (
                "Something went wrong with the build. The libraries are not " +
                    "installed correctly. Try running the build again."
            ),
        ).assertIsDisplayed()
    }

    @Test fun groupKeepsBothBubblesAlignedDespiteTheFirstEmptyAvatar() {
        show("Group")
        val first = rule.onNodeWithText("I checked the registry addresses.").fetchSemanticsNode().boundsInRoot
        val last =
            rule.onNodeWithText(
                "The component and example JSON now live under the UI registry.",
            ).fetchSemanticsNode().boundsInRoot
        assertEquals(first.left, last.left, 0.1f)
    }

    @Test fun actionsHaveTheirActualAccessibleLabelsAndFailureStatus() {
        show("Actions")
        listOf("Copy", "Like", "Dislike", "Retry").forEach {
            rule.onNodeWithContentDescription(it).assertHasClickAction().assertIsEnabled()
        }
        rule.onNodeWithText("Failed to send").assertIsDisplayed()
        rule.runOnIdle { inputModeManager.requestInputMode(InputMode.Keyboard) }
        rule.onNodeWithContentDescription("Copy").requestFocus()
        listOf("Copy", "Like", "Dislike").forEach { label ->
            rule.onNodeWithContentDescription(label).assertIsFocused().performKeyInput { pressKey(Key.Tab) }
        }
        rule.onNodeWithContentDescription("Retry").assertIsFocused()
    }

    @Test fun attachmentRecipeContainsTheImagePdfAndDownloadControl() {
        show("Attachment")
        rule.onNodeWithContentDescription("Workspace").assertIsDisplayed()
        rule.onNodeWithText("sales-dashboard.pdf").assertIsDisplayed()
        rule.onNodeWithText("PDF · 2.4 MB").assertIsDisplayed()
        rule.onNodeWithContentDescription("Download").assertHasClickAction()
        rule.onNodeWithText("Thanks. Looks good.").assertIsDisplayed()
    }

    @Test fun exportKeepsFeaturesAccessibilityAndOfficialRecipeOrder() {
        val markdown = componentPageMarkdown(messageDoc)
        assertFalse(markdown.contains("## Examples"))
        val order =
            listOf(
                "## Features",
                "## Avatar",
                "## Group",
                "## Header and Footer",
                "## Actions",
                "## Attachment",
                "## Accessibility",
                "### Label icon-only actions",
                "### Status updates",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "Start and end alignment for sender and receiver rows via `align`.")
        assertContains(markdown, "For AI apps, you can render reasoning steps, tool calls")
        assertContains(markdown, "[Bubble](/bubble)")
        assertContains(markdown, "[MessageScroller](/message-scroller)")
        assertContains(markdown, "Render an empty `MessageAvatar` on the earlier messages")
        assertContains(markdown, "| `Start` | Align the message to the start of the conversation. |")
        assertContains(markdown, "Customize every part through its `modifier` and `style` parameters.")
        assertContains(markdown, "## Start aligned")
        assertContains(markdown, "`role = \"status\"`")
    }
}
