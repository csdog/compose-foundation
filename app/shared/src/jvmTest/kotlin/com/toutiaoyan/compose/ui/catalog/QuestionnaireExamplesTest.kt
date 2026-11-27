package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.questionnaireDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class QuestionnaireExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }

    private fun advance() {
        rule.mainClock.advanceTimeBy(400)
        rule.waitForIdle()
    }

    private fun show() {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val window =
                remember(original, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(viewport.dp.roundToPx(), 900.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width((if (viewport >= 640) 640 else viewport).dp).height(740.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        questionnaireDoc.examples.first().RenderPreview()
                    }
                }
            }
        }
        advance()
    }

    private fun click(label: String) {
        rule.onNodeWithText(label).performClick()
        advance()
    }

    private fun capture(
        name: String,
        tag: String = "questionnaire-recipe",
    ) {
        val file = File("build/reports/questionnaire-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag(tag).captureToImage().toAwtImage(), "png", file)
    }

    @Test fun sourceDefaultHasThreeQuestionsAndOnlyTheVisibleActions() {
        assertEquals("Default", questionnaireDoc.examples.first().title)
        assertContains(questionnaireDoc.examples.first().code, "Tool call timeline")
        assertFalse(questionnaireDoc.examples.first().code.contains("questionnaireDoc"))
        show()
        rule.onNodeWithText("Question 1 of 3").assertIsDisplayed()
        rule.onNodeWithText("What should the agent build next?").assertIsDisplayed()
        listOf("Tool call timeline", "Approval checkpoints", "Sub-agent handoffs").forEach {
            rule.onNodeWithText(it).assertIsNotSelected()
        }
        listOf("A", "B", "C").forEach { rule.onNodeWithText(it, useUnmergedTree = true).assertIsDisplayed() }
        rule.onNodeWithText("Previous").assertDoesNotExist()
        rule.onNodeWithText("Skip").assertDoesNotExist()
        rule.onNodeWithText("Save plan").assertDoesNotExist()
        rule.onNodeWithText("Next").assertHeightIsEqualTo((if (viewport < 640) 44 else 32).dp)
        val expectedWidth = minOf(448, (if (viewport >= 640) 640 else viewport) - 48)
        rule.onNodeWithTag("questionnaire-recipe").assertWidthIsEqualTo(expectedWidth.dp)
        capture("initial")
    }

    @Test fun validationMultiSelectAndSubmitUseTheSourceAnswersAndToast() {
        show()
        click("Next")
        rule.onNodeWithText("Choose an answer to continue.").assertIsDisplayed()
        capture("required-error")
        click("Tool call timeline")
        rule.onNodeWithText("Tool call timeline").assertIsSelected()
        click("Next")
        rule.onNodeWithText("Question 2 of 3").assertIsDisplayed()
        click("Risks")
        click("Progress")
        rule.onNodeWithText("Risks").assertIsOn()
        rule.onNodeWithText("Progress").assertIsOn()
        capture("multiple")
        click("Next")
        rule.onNodeWithText("Question 3 of 3").assertIsDisplayed()
        rule.onNodeWithText("Skip").assertDoesNotExist()
        click("Start now")
        capture("last-selected")
        click("Save plan")
        rule.onNodeWithText("Agent plan saved").assertIsDisplayed()
        rule.onNodeWithText(
            (
                "Direction: tool-calls · Progress signals: progress, risks · " +
                    "Timing: now"
            ),
        ).assertIsDisplayed()
        capture("toast-multiple", "questionnaire-toast")
    }

    @Test fun inputShortcutPlainEnterAndExplicitSkipKeepOnlyTheFreeformAnswer() {
        show()
        click("Tool call timeline")
        rule.onNodeWithTag("questionnaire-recipe").requestFocus()
        rule.onNodeWithTag("questionnaire-recipe").performKeyInput { pressKey(Key.D) }
        advance()
        rule.onNodeWithContentDescription(
            "Another agent feature",
        ).assertIsFocused().performTextReplacement("Build reports")
        advance()
        rule.onNodeWithText("Tool call timeline").assertIsNotSelected()
        capture("freeform")
        rule.onNodeWithContentDescription("Another agent feature").performKeyInput { pressKey(Key.Enter) }
        advance()
        rule.onNodeWithText("Question 2 of 3").assertIsDisplayed()
        click("Next")
        rule.onNodeWithText("Choose an answer or skip this question.").assertIsDisplayed()
        click("Skip")
        rule.onNodeWithText("Question 3 of 3").assertIsDisplayed()
        rule.onNodeWithTag("questionnaire-recipe").requestFocus()
        rule.onNodeWithTag("questionnaire-recipe").performKeyInput { pressKey(Key.C) }
        advance()
        rule.onNodeWithText("Add it to the backlog").assertIsSelected().assertIsFocused()
        rule.onNodeWithText("Add it to the backlog").performKeyInput { pressKey(Key.Enter) }
        advance()
        rule.onNodeWithText("Direction: Build reports · Progress signals: None · Timing: backlog").assertIsDisplayed()
        capture("toast-freeform", "questionnaire-toast")
    }

    @Test fun replacingFreeformWithAFixedChoiceKeepsTheDraftButOmitsItFromSubmission() {
        show()
        rule.onNodeWithContentDescription(
            "Another agent feature",
        ).performClick().performTextReplacement("Keep this draft")
        advance()
        click("Approval checkpoints")
        rule.onNodeWithContentDescription("Another agent feature").assertTextEquals("Keep this draft")
        capture("draft-fixed")
        click("Next")
        click("Risks")
        click("Skip")
        click("Previous")
        rule.onNodeWithText("Risks").assertIsOff()
        click("Skip")
        click("Next development cycle")
        click("Save plan")
        rule.onNodeWithText("Direction: approvals · Progress signals: None · Timing: next-cycle").assertIsDisplayed()
        capture("toast-fixed", "questionnaire-toast")
    }
}
