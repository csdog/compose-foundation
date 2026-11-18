@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.alert

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.text.assertPaintedTextColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AlertNovaTest : ScreenshotTest() {
    @Test fun legacyActionSurvivesAlertWidthChange() {
        var width by mutableStateOf(200.dp)
        var showAction by mutableStateOf(true)
        val content: @Composable AlertScope.() -> Unit = {
            AlertTitle("Notice")
            if (showAction) AlertAction { Button("Action", onClick = {}) }
        }
        setThemedContent(stylePreset = StylePreset.Lyra) {
            Alert(Modifier.width(width).testTag("alert"), content = content)
        }
        composeRule.onNodeWithText("Action").assertIsDisplayed()
        composeRule.runOnIdle { width = 240.dp }
        composeRule.onNodeWithTag("alert").assertWidthIsEqualTo(240.dp)
        composeRule.onNodeWithText("Action").assertIsDisplayed()
        composeRule.runOnIdle { showAction = false }
        composeRule.onNodeWithText("Action").assertDoesNotExist()
    }

    @Test fun actionMatchesOfficialBorderRelativeInsetsAndFixedReserve() {
        var wide by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Alert(Modifier.width(300.dp).testTag("alert")) {
                AlertTitle(Modifier.testTag("title")) { "A long heading that fills the available body column" }
                AlertDescription("Description")
                AlertAction { Box(Modifier.size(if (wide) 136.dp else 56.dp, 24.dp).testTag("action")) }
            }
        }
        val root = composeRule.onNodeWithTag("alert").fetchSemanticsNode().boundsInRoot
        val title = composeRule.onNodeWithTag("title").fetchSemanticsNode().boundsInRoot

        fun action() = composeRule.onNodeWithTag("action").fetchSemanticsNode().boundsInRoot
        assertEquals(9f, root.right - action().right, .5f)
        assertEquals(9f, action().top - root.top, .5f)
        assertEquals(216f, title.width, .5f)
        composeRule.runOnIdle { wide = true }
        val wideTitle = composeRule.onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        assertEquals(title.width, wideTitle.width, .5f, "The upstream fixed reserve does not grow for wide actions")
        assertEquals(
            72f,
            wideTitle.right - action().left,
            .5f,
            "Wide overlap is an upstream constraint, not automatic body reflow",
        )
    }

    @Test fun callerBodyPaddingSupportsMultipleActionsAcrossDirectionAndRecomposition() {
        var rtl by mutableStateOf(false)
        var updates by mutableStateOf(0)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                Alert(Modifier.width(292.dp).testTag("alert")) {
                    Column(Modifier.padding(end = 80.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        AlertTitle(Modifier.testTag("title")) { "Changes awaiting review" }
                        AlertDescription(
                            Modifier.testTag("body"),
                        ) { "Review the draft before saving. Updates: $updates" }
                    }
                    AlertAction {
                        Row(
                            Modifier.width(136.dp).testTag("actions"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                "Save",
                                onClick = { updates++ },
                                size = ButtonSize.Xs,
                                style = Style { width(64.dp) },
                            )
                            Button(
                                "Undo",
                                onClick = { updates++ },
                                size = ButtonSize.Xs,
                                style = Style { width(64.dp) },
                            )
                        }
                    }
                }
            }
        }

        fun check() {
            val action = composeRule.onNodeWithTag("actions").fetchSemanticsNode().boundsInRoot
            val root = composeRule.onNodeWithTag("alert").fetchSemanticsNode().boundsInRoot
            for (tag in listOf("title", "body")) {
                val text = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
                assertTrue(
                    if (rtl) text.left >= action.right + 7.5f else text.right <= action.left - 7.5f,
                    "Body must reserve the caller's action width",
                )
            }
            assertEquals(9f, if (rtl) action.left - root.left else root.right - action.right, .5f)
        }
        check()
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(1, updates)
        check()
        composeRule.runOnIdle { rtl = true }
        check()
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(2, updates)
        check()
    }

    @Test fun actionHasReservedSpaceAndSurvivesBodyRecomposition() {
        var longer by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Alert(Modifier.width(300.dp).testTag("alert")) {
                AlertTitle(Modifier.testTag("title")) { "A long title that must wrap before the action" }
                AlertDescription(if (longer) "A changed description with more content" else "Description")
                AlertAction { Button("Undo", onClick = {}, size = ButtonSize.Xs) }
            }
        }

        fun check() {
            val title = composeRule.onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
            val action = composeRule.onNodeWithText("Undo").fetchSemanticsNode().boundsInRoot
            assertTrue(title.right <= action.left, "Title must not overlap the action")
        }
        check()
        composeRule.runOnIdle { longer = true }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Undo").assertIsDisplayed()
        check()
        composeRule.onNodeWithTag(
            "alert",
        ).assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
    }

    @Test fun destructiveDescriptionUsesNinetyPercentDestructiveAndFourteenPixelText() {
        var expected = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova, darkTheme = true) {
            expected = currentTheme.colors.destructive.copy(alpha = .9f)
            Alert(title = "Error", description = "Details", variant = AlertVariant.Destructive)
        }
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Details",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(expected, results.single().layoutInput.style.color)
        assertEquals(14.sp, results.single().layoutInput.style.fontSize)
        assertEquals(20.sp, results.single().layoutInput.style.lineHeight)
    }

    @Test fun titleAndDescriptionHaveTwoPixelGap() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Alert(title = "Title", description = "Description", modifier = Modifier.testTag("line box"))
        }
        composeRule.onNodeWithTag("line box").assertHeightIsEqualTo(60.dp)
        val title = composeRule.onNodeWithText("Title").fetchSemanticsNode().boundsInRoot
        val body = composeRule.onNodeWithText("Description").fetchSemanticsNode().boundsInRoot
        assertEquals(2f, body.top - title.bottom, .5f)
    }

    @Test fun titleInheritsCallerContentColor() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Alert(title = "Custom title", style = Style { contentColor(Color.Magenta) })
        }
        composeRule.onNodeWithText("Custom title").assertPaintedTextColor(Color.Magenta)
    }

    @Test fun unboundedWidthStillMeasuresVisibleContent() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
            ) { Alert(title = "Visible title", description = "Description") }
        }
        val bounds = composeRule.onNodeWithText("Visible title").fetchSemanticsNode().boundsInRoot
        assertTrue(bounds.width > 20f && bounds.height > 0f)
        composeRule.onNodeWithText("Description").assertIsDisplayed()
    }

    @Test fun rtlPlacesIconAtStartAndReservesEndForAction() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Alert(Modifier.width(300.dp), icon = { Box(Modifier.size(16.dp).testTag("icon")) }) {
                    AlertTitle("RTL heading")
                    AlertDescription("RTL description")
                    AlertAction { Button("Undo", onClick = {}, size = ButtonSize.Xs) }
                }
            }
        }
        val icon = composeRule.onNodeWithTag("icon").fetchSemanticsNode().boundsInRoot
        val title = composeRule.onNodeWithText("RTL heading").fetchSemanticsNode().boundsInRoot
        val action = composeRule.onNodeWithText("Undo").fetchSemanticsNode().boundsInRoot
        assertTrue(icon.left >= title.right)
        assertTrue(action.right <= title.left)
    }
}
