package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.bubbleDoc
import com.toutiaoyan.compose.ui.catalog.docs.bubbleLongText
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class BubbleExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(384.dp),
                    ) { bubbleDoc.examples.single { it.title == title }.preview() }
                }
            }
        }
    }

    @Test fun defaultShowsCompleteConversationAndGroupedReactionLabels() {
        show("Default")
        rule.onNodeWithText("Hey there! what's up?").assertIsDisplayed()
        rule.onNodeWithText("Sure. Hit me with your best demo.").assertIsDisplayed()
        rule.onNodeWithContentDescription("Reaction: thumbs up").assertExists()
        rule.onNodeWithContentDescription("Reactions: thumbs up, fire, eyes, and 2 more").assertExists()
    }

    @Test fun clickableBubblesShowDistinctNotifications() {
        show("Links and Buttons")
        rule.onNodeWithText("I forgot my password").performClick()
        rule.onNodeWithText("You clicked forgot password").assertIsDisplayed()
        rule.onNodeWithText("I need help with my subscription").requestFocus().performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("You clicked help with subscription").assertIsDisplayed()
    }

    @Test fun collapsedTextExpandsAndContractsWithTheActualTrigger() {
        show("Show More / Collapsible")
        rule.onNodeWithText(bubbleLongText).assertDoesNotExist()
        rule.onNodeWithText("Show more").performClick()
        rule.onNodeWithText(bubbleLongText).assertIsDisplayed()
        rule.onNodeWithText("Show less").performClick()
        rule.onNodeWithText(bubbleLongText.take(180) + "...").assertIsDisplayed()
    }

    @Test fun variantsShowEveryGhostParagraphWithoutTruncation() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(384.dp).height(600.dp)
                            .verticalScroll(rememberScrollState())
                            .testTag("variants-preview"),
                    ) {
                        bubbleDoc.examples.single { it.title == "Variants" }.preview()
                    }
                }
            }
        }
        rule.onNodeWithText(
            "Ghost bubbles work for assistant text, markdown, and other content that should not be framed.",
        ).assertExists()
        rule.onNodeWithText(
            "This is perfect for assistant messages that should not have a frame and can take the " +
                "full width of the container. You can also render code in it.",
        ).assertExists()
        rule.onNodeWithText(
            "Ghost bubbles are full width and can take the full width of the container.",
        ).assertExists()
        val last =
            rule.onNodeWithText(
                "Ghost bubbles are full width and can take the full width of the container.",
            )
        last.performScrollTo().assertIsDisplayed()
        assertTrue(last.fetchSemanticsNode().boundsInRoot.height > 0f)
    }

    @Test fun popoverShowsActualErrorAndEscapeClosesIt() {
        show("Popover")
        val trigger = rule.onNodeWithContentDescription("Show error details")
        trigger.requestFocus().performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("Command failed with exit code 1").assertIsDisplayed()
        rule.onNodeWithText("ENOENT: no such file or directory, open pnpm-lock.yaml").assertIsDisplayed()
        rule.onNodeWithText("Command failed with exit code 1").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Command failed with exit code 1").assertDoesNotExist()
        trigger.assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        rule.onNodeWithText("Command failed with exit code 1").assertIsDisplayed()
        rule.onNodeWithText("Command failed with exit code 1").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Command failed with exit code 1").assertDoesNotExist()
        trigger.assertIsFocused()
    }

    @Test fun focusedReadStatusExposesMetadataTooltip() {
        show("Tooltip")
        rule.onNodeWithContentDescription("Read status").requestFocus().performKeyInput { pressKey(Key.Enter) }
        rule.mainClock.advanceTimeBy(1000)
        rule.onNodeWithText("Read on Jan 5, 2026 at 4:32 PM").assertIsDisplayed()
        rule.onNodeWithText("Read on Jan 5, 2026 at 4:32 PM").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Read on Jan 5, 2026 at 4:32 PM").assertDoesNotExist()
        rule.onNodeWithContentDescription("Read status").assertIsFocused()
    }

    @Test fun interactiveReactionShowsSuccessNotification() {
        show("Reactions")
        rule.onNodeWithText("Yes, run it").performMouseInput { click(center) }
        rule.onNodeWithText("You clicked yes, running command...").assertIsDisplayed()
        rule.onNodeWithText("Yes, run it").requestFocus().performKeyInput { pressKey(Key.Enter) }
        rule.onAllNodesWithText("You clicked yes, running command...").assertCountEquals(2)
    }

    @Test fun docsHaveAllNineOfficialExamplesAndCurrentKotlinNames() {
        assertEquals(9, bubbleDoc.examples.size)
        assertEquals(4, bubbleDoc.apiSections.size)
        assertTrue(bubbleDoc.examples.none { "TextTypography" in it.code || "TextStyles" in it.code })
        val introduction = assertNotNull(bubbleDoc.introMarkdown)
        assertTrue("quoted replies, suggestions, and reactions" in introduction)
        assertTrue("Place avatars, names, timestamps" in introduction)
        val markdown = componentPageMarkdown(bubbleDoc)
        val introPosition = markdown.indexOf(introduction)
        assertTrue(introPosition > markdown.indexOf(bubbleDoc.examples.first().code))
        assertTrue(introPosition < markdown.indexOf("## Installation"))
    }

    @Test fun catalogNotificationsUseTheWindowHostRatherThanTheBubbleFrame() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.size(600.dp).testTag("window")) {
                    OverlayHost {
                        CatalogToastHost {
                            Box(Modifier.width(384.dp)) {
                                bubbleDoc.examples.single { it.title == "Links and Buttons" }.preview()
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("I forgot my password").performMouseInput { click(center) }
        val toast = rule.onNodeWithText("You clicked forgot password").fetchSemanticsNode().boundsInRoot
        val window = rule.onNodeWithTag("window").fetchSemanticsNode().boundsInRoot
        assertTrue(toast.center.y > window.center.y)
        assertTrue(toast.right < window.right)
        assertTrue(toast.bottom < window.bottom)
    }
}
