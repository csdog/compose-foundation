package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.dropdownMenuDoc
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test

class DropdownMenuExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(
        title: String,
        trigger: String = "Open",
        openWithKeyboard: Boolean = false,
    ) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp), contentAlignment = Alignment.Center) {
                        dropdownMenuDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
        if (openWithKeyboard) {
            rule.onNodeWithText(trigger).requestFocus()
            rule.onNodeWithText(trigger).performKeyInput { pressKey(Key.DirectionDown) }
        } else {
            rule.onNodeWithText(trigger).performClick()
        }
    }

    /** Renders one preview without opening it, for tests that drive the trigger themselves. */
    private fun render(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp), contentAlignment = Alignment.Center) {
                        dropdownMenuDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun defaultIncludesDisabledApiAndSelectingAnActionClosesTheMenu() {
        show("Default")
        rule.onNodeWithText("Settings").assertIsDisplayed()
        rule.onNodeWithText("New Team").assertIsDisplayed()
        rule.onNodeWithText("⌘+T", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("API").assertIsNotEnabled()
        rule.onNodeWithText("Profile").performClick()
        rule.onNodeWithText("Profile").assertDoesNotExist()
        rule.onNodeWithText("Open").assertIsDisplayed()
    }

    @Test fun disabledNovaItemKeepsTheForegroundBeforeRowOpacity() {
        show("Basic")
        val enabled = mutableListOf<TextLayoutResult>()
        val disabled = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("Profile").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(enabled) }
        rule.onNodeWithText("API").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(disabled) }
        assertEquals(enabled.single().layoutInput.style.color, disabled.single().layoutInput.style.color)
    }

    @Test fun basicUsesOfficialTwentyEightPixelRows() {
        show("Basic")
        rule.onNodeWithText("Profile").assertHeightIsEqualTo(28.dp)
        // Text semantics measure the 16dp line; the label also has 4dp padding on each side.
        rule.onNodeWithText("My Account").assertHeightIsEqualTo(16.dp)
    }

    @Test fun checkboxSelectionStaysOpenAndPersistsAfterReopening() {
        show("Checkboxes")
        rule.onNodeWithText("Activity Bar").assertIsNotEnabled()
        rule.onNodeWithText("Panel").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Unchecked"),
        ).performClick()
        rule.onNodeWithText("Panel").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Checked"),
        )
        rule.onNodeWithText("Open").performClick()
        rule.onNodeWithText("Open").performClick()
        rule.onNodeWithText("Panel").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Checked"),
        )
    }

    @Test fun keyboardCanEnterBothSubmenusAndSelectAnAction() {
        show("Submenu", openWithKeyboard = true)
        rule.onNodeWithText("Team").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Invite users").assertIsFocused().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("Email").assertIsFocused().performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.DirectionDown)
        }
        rule.onNodeWithText("More options").assertIsFocused().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("Calendly").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("Invite users").assertDoesNotExist()
    }

    @Test fun nestedSubmenuActionClosesTheEntireMenuTree() {
        show("Submenu")
        rule.onNodeWithText("Invite users").performClick()
        rule.onNodeWithText("More options").performClick()
        rule.onNodeWithText("Webhook").assertIsDisplayed().performClick()
        rule.onNodeWithText("Webhook").assertDoesNotExist()
        rule.onNodeWithText("Invite users").assertDoesNotExist()
        rule.onNodeWithText("Open").assertIsDisplayed()
    }

    @Test fun officialRecipesAppearInTheOfficialOrder() {
        assertEquals(
            listOf(
                "Default", "Basic", "Submenu", "Shortcuts", "Icons", "Checkboxes", "Checkboxes Icons",
                "Radio Group", "Radio Icons", "Destructive", "Avatar", "Complex", "RTL",
            ),
            dropdownMenuDoc.examples.take(13).map { it.title },
        )
        assertTrue(dropdownMenuDoc.examples.single { it.title == "Checkboxes Icons" }.code.contains("leadingContent"))
        assertTrue(
            dropdownMenuDoc.examples.drop(13).all { it.title.startsWith("Kotlin extension: ") },
        )
    }

    @Test fun pointerOpenLeavesEveryRowUnhighlighted() {
        show("Basic")
        rule.onNodeWithText("Profile").assertIsNotFocused()
        rule.onNodeWithText("Billing").assertIsNotFocused()
        // The first ArrowDown from the open-but-unhighlighted menu reaches the first row.
        rule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test fun keyboardArrowDownOpenFocusesTheFirstRow() {
        render("Basic")
        rule.onNodeWithText("Open").requestFocus()
        rule.onNodeWithText("Open").performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Profile").assertIsFocused()
    }

    @Test fun keyboardArrowUpOpenFocusesTheLastEnabledRow() {
        render("Basic")
        rule.onNodeWithText("Open").requestFocus()
        rule.onNodeWithText("Open").performKeyInput { pressKey(Key.DirectionUp) }
        rule.onNodeWithText("Support").assertIsFocused()
    }

    @Test fun keyboardEnterOpenFocusesTheFirstRow() {
        render("Basic")
        rule.onNodeWithText("Open").requestFocus()
        rule.onNodeWithText("Open").performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("Profile").assertIsFocused()
    }

    @Test fun defaultContentWidthFollowsTheTriggerWithAnOfficialMinimum() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp), contentAlignment = Alignment.Center) {
                        Column {
                            DropdownMenu(open = true, onOpenChange = {}) {
                                DropdownMenuTrigger(modifier = Modifier.width(240.dp)) {
                                    Text { "Wide trigger" }
                                }
                                DropdownMenuContent {
                                    DropdownMenuItem("Profile", onClick = {})
                                }
                            }
                            DropdownMenu(open = true, onOpenChange = {}) {
                                DropdownMenuTrigger(modifier = Modifier.width(40.dp)) {
                                    Text { "Narrow trigger" }
                                }
                                DropdownMenuContent {
                                    DropdownMenuItem("Billing", onClick = {})
                                }
                            }
                        }
                    }
                }
            }
        }
        // A wide trigger widens the panel past the official 128dp minimum.
        rule.onNodeWithText("Profile").assertWidthIsAtLeast(220.dp)
        // A narrow trigger falls back to the official min-w-32 minus the panel padding.
        rule.onNodeWithText("Billing").assertWidthIsEqualTo(120.dp)
    }

    @Test fun notificationIconsKeepIndependentCheckedStates() {
        show("Checkboxes Icons", "Notifications")
        rule.onNodeWithText("Email notifications").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Checked"),
        ).performClick()
        rule.onNodeWithText("Email notifications").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Unchecked"),
        )
        rule.onNodeWithText("Push notifications").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Checked"),
        )
    }

    @Test fun paymentIconsSelectExactlyOneMethod() {
        show("Radio Icons", "Payment Method")
        rule.onNodeWithText("PayPal").performClick().assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Selected"),
        )
        rule.onNodeWithText("Credit Card").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Not selected"),
        )
    }

    @Test fun avatarTriggerOpensAccountActions() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp), contentAlignment = Alignment.Center) {
                        dropdownMenuDoc.examples.single { it.title == "Avatar" }.preview()
                    }
                }
            }
        }
        rule.onNodeWithContentDescription("Open account menu").performClick()
        rule.onNodeWithText("Account").assertIsDisplayed()
        rule.onNodeWithText("Sign Out").performClick()
        rule.onNodeWithText("Account").assertDoesNotExist()
    }

    @Test fun complexMenuScrollsToTheLastAction() {
        show("Complex", "Complex Menu")
        rule.onNodeWithText("Sign Out").performScrollTo().assertIsDisplayed().performClick()
        rule.onNodeWithText("New File").assertDoesNotExist()
    }

    @Test fun complexNestedNotificationsShareTheSidebarState() {
        show("Complex", "Complex Menu")
        rule.onNodeWithText("Show Sidebar").performScrollTo().performClick()
        rule.onNodeWithText("Settings").performScrollTo().performClick()
        rule.onNodeWithText("Notifications").performClick()
        rule.onNodeWithText("Email Notifications").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Unchecked"),
        ).performClick().assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Checked"),
        )
    }

    @Test fun rtlAccountSubmenuUsesTheArabicTranslation() {
        show("RTL", "افتح القائمة")
        rule.onNodeWithText("الحساب").performClick()
        rule.onNodeWithText("الملف الشخصي").assertIsDisplayed().performClick()
        rule.onNodeWithText("الحساب").assertDoesNotExist()
    }
}
