@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.tooltipDoc
import com.toutiaoyan.compose.ui.catalog.docs.TooltipDisabledRecipe
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
class TooltipExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private lateinit var inputMode: InputModeManager

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(
                    1440,
                    false,
                ),
                arrayOf<Any>(
                    1440,
                    true,
                ),
                arrayOf<Any>(
                    390,
                    false,
                ),
                arrayOf<Any>(
                    390,
                    true,
                ),
            )
    }

    private fun show(
        title: String,
        source: MutableInteractionSource? = null,
    ) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewport, 800)
                    }
                }
            inputMode = LocalInputModeManager.current
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.fillMaxSize().background(currentTheme.colors.background),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        Box(
                            Modifier.width(
                                minOf(
                                    viewport,
                                    640,
                                ).dp,
                            ).height(480.dp).padding(24.dp),
                            contentAlignment =
                                Alignment.Center,
                        ) {
                            if (source != null) {
                                TooltipDisabledRecipe(source)
                            } else {
                                tooltipDoc.examples.single { it.title == title }.preview()
                            }
                        }
                    }
                }
            }
        }
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
    }

    private fun focus(label: String) {
        rule.onNodeWithText(label).requestFocus()
        rule.waitForIdle()
    }

    private fun capture(name: String) {
        val roots = rule.onAllNodes(isRoot())
        val file = File("build/reports/tooltip-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(roots[roots.fetchSemanticsNodes().lastIndex].captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultFocusAndEscapeUseTheOfficialMessage() {
        show("Default")
        focus("Hover")
        rule.onNodeWithText("Add to library").assertIsDisplayed()
        capture("default")
        rule.onNodeWithText("Hover").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Add to library").assertDoesNotExist()
        rule.onNodeWithText("Hover").assertIsFocused()
    }

    @Test fun allFourPhysicalSidesUseTheSameSourceMessage() {
        show("Side")
        listOf("Left", "Top", "Bottom", "Right").forEach { label ->
            focus(label)
            val trigger = rule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
            val content = rule.onNodeWithText("Add to library").fetchSemanticsNode().boundsInRoot
            when (label) {
                "Left" -> assertTrue(content.right < trigger.left)
                "Right" -> assertTrue(content.left > trigger.right)
                "Top" -> assertTrue(content.bottom < trigger.top)
                "Bottom" -> assertTrue(content.top > trigger.bottom)
            }
            capture("side-${label.lowercase()}")
            rule.onNodeWithText(label).performKeyInput { pressKey(Key.Escape) }
        }
    }

    @Test fun shortcutUsesTheSaveIconAndPassiveKeycap() {
        show("With Keyboard Shortcut")
        rule.onNodeWithContentDescription("Save").requestFocus()
        rule.onNodeWithText("Save Changes").assertIsDisplayed()
        rule.onNodeWithText("S").assertIsDisplayed()
        rule.onNodeWithText("S").assertHasNoClickAction()
        capture("keyboard")
    }

    @Test fun disabledButtonStillAllowsItsWrapperTooltip() {
        val source = MutableInteractionSource()
        show("Disabled Button", source)
        rule.onNodeWithText("Disabled").assertIsNotEnabled()
        rule.runOnIdle { source.tryEmit(HoverInteraction.Enter()) }
        rule.onNodeWithText("This feature is currently unavailable").assertIsDisplayed()
        capture("disabled")
    }

    @Test fun rtlHasPhysicalAndLogicalSidesAndRealLanguageSwitching() {
        show("RTL")
        focus("بداية السطر")
        val trigger = rule.onNodeWithText("بداية السطر").fetchSemanticsNode().boundsInRoot
        val content = rule.onNodeWithText("إضافة إلى المكتبة").fetchSemanticsNode().boundsInRoot
        assertTrue(content.left > trigger.right)
        capture("rtl-arabic-inline-start")
        rule.onNodeWithText("بداية السطر").performKeyInput { pressKey(Key.Escape) }
        listOf("يسار", "أعلى", "أسفل", "يمين", "نهاية السطر").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        focus("Inline Start")
        val enTrigger = rule.onNodeWithText("Inline Start").fetchSemanticsNode().boundsInRoot
        val enContent = rule.onNodeWithText("Add to library").fetchSemanticsNode().boundsInRoot
        assertTrue(enContent.right < enTrigger.left)
        rule.onNodeWithText("Inline Start").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        focus("תחילת השורה")
        val heTrigger = rule.onNodeWithText("תחילת השורה").fetchSemanticsNode().boundsInRoot
        val heContent = rule.onNodeWithText("הוסף לספרייה").fetchSemanticsNode().boundsInRoot
        assertTrue(heContent.left > heTrigger.right)
    }
}
