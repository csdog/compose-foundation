package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.messageScrollerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class MessageScrollerRecipesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private lateinit var inputMode: InputModeManager
    private var expectedAccent = Color.Unspecified

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(
                    432,
                    false,
                ),
                arrayOf<Any>(
                    432,
                    true,
                ),
                arrayOf<Any>(
                    360,
                    false,
                ),
                arrayOf<Any>(
                    360,
                    true,
                ),
            )
    }

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                inputMode = LocalInputModeManager.current
                expectedAccent = currentTheme.colors.accent
                CatalogToastHost(Modifier.width(width.dp).height(720.dp)) {
                    OverlayHost {
                        Box(
                            Modifier.testTag("recipe-capture").width(width.dp).height(720.dp)
                                .background(currentTheme.colors.background).padding(24.dp),
                            contentAlignment = Alignment.TopCenter,
                        ) { messageScrollerDoc.examples.single { it.title == title }.RenderPreview() }
                    }
                }
            }
        }
        rule.waitForIdle()
        rule.onNodeWithTag("scroller-recipe-card").assertHeightIsEqualTo(560.dp)
            .assertWidthIsEqualTo((width - 48).dp)
    }

    private fun capture(
        name: String,
        tag: String = "recipe-capture",
    ) {
        val output = File("build/reports/message-scroller-recipes/$name-$width-${if (dark) "dark" else "light"}.png")
        output.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag(tag).captureToImage().toAwtImage(), "png", output)
    }

    @Test fun openingTabsRepositionTheSameSavedTranscriptAndCanRepeat() {
        show("Opening Saved Threads")
        rule.onNodeWithText("last-anchor").assertIsSelected()
        capture("opening")
        rule.onNodeWithText("start", substring = false).performClick()
        rule.onNodeWithTag("open-1").assertIsDisplayed()
        val viewport = rule.onNodeWithTag("opening-viewport").fetchSemanticsNode().boundsInRoot
        assertEquals(viewport.top + 16f, rule.onNodeWithTag("open-1").fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithText("end", substring = false).performClick()
        rule.onNodeWithContentDescription("Scroll to end").assertDoesNotExist()
        rule.onNodeWithText("last-anchor").performClick()
        val before = rule.onNodeWithTag("open-3").fetchSemanticsNode().boundsInRoot.top
        assertEquals(viewport.top + 80f, before, 1f)
        rule.onNodeWithTag("opening-viewport").requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("last-anchor").performClick()
        assertEquals(before, rule.onNodeWithTag("open-3").fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithText("last-anchor").requestFocus().performKeyInput { pressKey(Key.DirectionLeft) }
        rule.onNodeWithText("end", substring = false).assertIsSelected()
        rule.onNodeWithText("end", substring = false).performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("start", substring = false).assertIsSelected()
        rule.onNodeWithTag("open-1").assertIsDisplayed()
    }

    @Test fun jumpMenuClosesAndSmoothlyAlignsTheChosenOriginalTurn() {
        show("Jumping to Messages")
        capture("commands")
        rule.onNodeWithText("Jump to...").performClick()
        rule.onNodeWithText("Conversations").assertIsDisplayed()
        rule.onNodeWithText("We're seeing activation dip after works...").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Conversations").assertDoesNotExist()
        val viewport = rule.onNodeWithTag("commands-viewport").fetchSemanticsNode().boundsInRoot
        assertEquals(
            viewport.top +
                16f,
            rule.onNodeWithTag("command-activation").fetchSemanticsNode().boundsInRoot.top,
            1f,
        )
        rule.onNodeWithText("Jump to...").performClick()
        rule.onNodeWithText("What's the risk if we delay the invite ...").performClick()
        rule.waitForIdle()
        assertEquals(viewport.top + 16f, rule.onNodeWithTag("command-risk").fetchSemanticsNode().boundsInRoot.top, 1f)
    }

    @Test fun footerTracksTopMiddleAndBottomWithRealKeyboardScrolling() {
        show("Reading Scroll State")
        rule.onNodeWithText("You are at the top. You can only scroll down.").assertIsDisplayed()
        capture("scrollable")
        rule.onNodeWithTag("scrollable-viewport").requestFocus().assertIsFocused()
            .performKeyInput { pressKey(Key.PageDown) }
        rule.onNodeWithText("You can scroll both ways.").assertIsDisplayed()
        rule.onNodeWithTag("scrollable-viewport").assertIsFocused()
            .performKeyInput { pressKey(Key.MoveEnd) }
        val range =
            rule.onNodeWithTag("scrollable-viewport").fetchSemanticsNode()
                .config[SemanticsProperties.VerticalScrollAxisRange]
        assertEquals(range.maxValue(), range.value(), 1f, "End must reach the native scroll maximum")
        rule.onNodeWithText("You are at the bottom. You can only scroll up.").assertIsDisplayed()
        rule.onNodeWithTag("scrollable-viewport").performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("You are at the top. You can only scroll down.").assertIsDisplayed()
    }

    @Test fun outlineTracksAndJumpsBetweenOriginalAnchoredTurns() {
        show("Tracking the Reader's Position")
        capture("visibility")
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        rule.onNodeWithContentDescription("Open transcript outline").requestFocus()
        rule.mainClock.advanceTimeBy(700)
        rule.waitForIdle()
        rule.onNodeWithTag("outline-vis-checklist").assertIsSelected()
        capture("visibility-outline", "transcript-outline-panel")
        val panel = rule.onNodeWithTag("transcript-outline-panel").fetchSemanticsNode().boundsInRoot
        val currentRow = rule.onNodeWithTag("outline-vis-checklist").fetchSemanticsNode().boundsInRoot
        val mode = if (dark) "dark" else "light"
        val panelPath = "build/reports/message-scroller-recipes/visibility-outline-$width-$mode.png"
        val panelImage = ImageIO.read(File(panelPath))
        val actual =
            Color(
                panelImage.getRGB(
                    (currentRow.right - panel.left - 4).toInt(),
                    (currentRow.center.y - panel.top).toInt(),
                ),
            )
        println(
            "OUTLINE_PALETTE width=$width dark=$dark expected=" +
                expectedAccent.toArgb() +
                " actual=" +
                actual.toArgb(),
        )
        if (kotlin.math.abs(expectedAccent.red - actual.red) > 0.01f) {
            val file = File("build/reports/message-scroller-recipes/visibility-diagnostic-scene-$width-$dark.png")
            ImageIO.write(rule.onRoot().captureToImage().toAwtImage(), "png", file)
        }
        assertEquals(expectedAccent.red, actual.red, 0.01f)
        assertEquals(expectedAccent.green, actual.green, 0.01f)
        assertEquals(expectedAccent.blue, actual.blue, 0.01f)

        rule.onNodeWithTag("outline-vis-brief").performClick()
        rule.waitForIdle()
        val viewport = rule.onNodeWithTag("visibility-viewport").fetchSemanticsNode().boundsInRoot
        assertEquals(viewport.top + 16f, rule.onNodeWithTag("vis-brief").fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithTag("outline-vis-brief").assertIsSelected()
        rule.onNodeWithTag("outline-vis-actions").performClick()
        rule.waitForIdle()
        assertEquals(viewport.top + 28f, rule.onNodeWithTag("vis-actions").fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithTag("outline-vis-actions").assertIsSelected()
    }

    @Test fun loadingOriginalHistoryKeepsTheVisibleParagraphAndResetReopensAtEnd() {
        show("Loading Earlier Messages")
        capture("history")
        rule.onNodeWithContentDescription("Reset loaded messages").assertIsNotEnabled()
        rule.onNodeWithTag("history-1").assertDoesNotExist()
        val paragraph = (
            "If those stay quiet through the next batch window, we can " +
                "close this as an internal degradation."
        )
        val top = rule.onNodeWithText(paragraph).fetchSemanticsNode().boundsInRoot.top
        rule.onNodeWithContentDescription("Load History").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("history-1").assertExists()
        rule.onNodeWithText("History Loaded").assertIsNotEnabled()
        rule.onNodeWithText("History loaded", substring = false).assertIsDisplayed()
        rule.onNodeWithText("Scroll up to see earlier messages.").assertIsDisplayed()
        assertEquals(top, rule.onNodeWithText(paragraph).fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithContentDescription("Reset loaded messages").performClick()
        rule.onNodeWithTag("history-1").assertDoesNotExist()
        rule.onNodeWithContentDescription("Reset loaded messages").assertIsNotEnabled()
        assertEquals(top, rule.onNodeWithText(paragraph).fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithText("End of Conversation").assertIsDisplayed()
    }

    @Test fun contextPeekStreamingControlsMenuAndResetUseTheRealConversation() {
        show("Keeping Context Visible")
        capture("context")
        rule.onNodeWithText("64px").assertIsDisplayed()
        rule.onNodeWithContentDescription("Previous context peek")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(128f) }
        rule.onNodeWithText("128px").assertIsDisplayed()
        rule.mainClock.autoAdvance = false
        try {
            rule.onNodeWithContentDescription("Send").performClick()
            rule.mainClock.advanceTimeByFrame()
            rule.onNodeWithContentDescription("Send").assertIsNotEnabled()
            rule.onNodeWithContentDescription("Previous context peek").assertIsNotEnabled()
            rule.onNodeWithContentDescription("Reset context example").assertIsNotEnabled()
            rule.mainClock.advanceTimeBy(1080)
            rule.onNodeWithTag("context-4").assertExists()
            val viewport = rule.onNodeWithTag("context-viewport").fetchSemanticsNode().boundsInRoot
            assertEquals(viewport.top + 168f, rule.onNodeWithTag("context-3").fetchSemanticsNode().boundsInRoot.top, 1f)
            rule.mainClock.advanceTimeBy(6000)
        } finally {
            rule.mainClock.autoAdvance = true
        }
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Send").assertIsEnabled()
        rule.onNodeWithContentDescription("Previous context peek").assertIsEnabled()
        rule.onNodeWithContentDescription("Reset context example").assertIsEnabled()
        rule.onNodeWithContentDescription("Previous context peek")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(64f) }
        val viewport = rule.onNodeWithTag("context-viewport").fetchSemanticsNode().boundsInRoot
        assertEquals(viewport.top + 104f, rule.onNodeWithTag("context-3").fetchSemanticsNode().boundsInRoot.top, 1f)
        rule.onNodeWithContentDescription("Add files").performClick()
        rule.onNodeWithText("Add Photos & Files").assertIsDisplayed()
        rule.onNodeWithText("Create Image").assertIsDisplayed()
        rule.onNodeWithText("Deep Research").assertIsDisplayed()
        rule.onNodeWithText("Web Search").assertIsDisplayed()
        rule.onNodeWithText("Add Photos & Files").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithContentDescription("Reset context example").performClick()
        rule.onNodeWithText("64px").assertIsDisplayed()
        rule.onNodeWithTag("context-3").assertDoesNotExist()
        rule.onNodeWithContentDescription("Send").assertIsEnabled()
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        rule.onNodeWithContentDescription("Previous context peek").requestFocus()
            .performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("65px").assertIsDisplayed()
    }

    @Test fun contextSendsAllRemainingTurnsThenDisablesSendUntilReset() {
        show("Keeping Context Visible")
        rule.mainClock.autoAdvance = false
        try {
            repeat(3) {
                rule.onNodeWithContentDescription("Send").performClick()
                rule.mainClock.advanceTimeBy(6500)
            }
        } finally {
            rule.mainClock.autoAdvance = true
        }
        rule.waitForIdle()
        rule.onNodeWithTag("context-8").assertExists()
        rule.onNodeWithText("No messages queued. Reset the context.").assertIsDisplayed()
        rule.onNodeWithContentDescription("Send").assertIsNotEnabled()
        capture("context-complete")
        rule.onNodeWithContentDescription("Reset context example").performClick()
        rule.onNodeWithTag("context-8").assertDoesNotExist()
        rule.onNodeWithContentDescription("Send").assertIsEnabled()
    }
}
