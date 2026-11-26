@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.surfaces

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.calendar.*
import com.toutiaoyan.compose.ui.components.collapsible.*
import com.toutiaoyan.compose.ui.components.command.*
import com.toutiaoyan.compose.ui.components.contextmenu.*
import com.toutiaoyan.compose.ui.components.dialog.*
import com.toutiaoyan.compose.ui.components.drawer.*
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SurfacesNovaParityTest : ScreenshotTest() {
    private fun content(dark: Boolean) =
        snapshot("surfaces_nova_inline", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Collapsible(defaultOpen = true) {
                    CollapsibleTrigger("Order details")
                    CollapsibleContent("Shipped")
                }
                Command {
                    CommandInput()
                    CommandList {
                        CommandEmpty()
                        CommandGroup(heading = "Suggestions") {
                            CommandItem("Calendar", {})
                            CommandItem("Calculator", {}, disabled = true)
                        }
                        CommandSeparator()
                        CommandGroup(heading = "Settings") { CommandItem("Profile", {}, shortcut = "⌘P") }
                    }
                }
                Calendar(
                    2026,
                    10,
                    { _, _ -> },
                    CalendarDate(2026, 10, 15),
                    {},
                    today = CalendarDate(2026, 10, 29),
                    fixedWeeks = false,
                )
            }
        }

    @Test fun inlineLight() = content(false)

    @Test fun inlineDark() = content(true)

    private fun dialog(dark: Boolean) =
        snapshot("surfaces_nova_dialog", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Dialog(defaultOpen = true) {
                DialogContent(
                    "Edit profile",
                    description = "Make changes to your profile here. Click save when you're done.",
                ) {
                    Input("Pedro Duarte", {})
                    DialogFooter {
                        Button("Cancel", variant = ButtonVariant.Outline)
                        Button("Save changes")
                    }
                }
            }
        }

    @Test fun dialogLight() = dialog(false)

    @Test fun dialogDark() = dialog(true)

    private fun drawer(dark: Boolean) =
        snapshot("surfaces_nova_drawer", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Drawer(defaultOpen = true, showSwipeHandle = true) {
                DrawerContent {
                    DrawerHeader("Pick a delivery time", "We'll prepare your order as soon as possible.")
                    Text(Modifier.padding(16.dp)) { "Standard delivery" }
                    DrawerFooterColumn(modifier = Modifier.fillMaxWidth()) {
                        Button("Confirm Delivery Time", modifier = Modifier.fillMaxWidth())
                        Button("Cancel", variant = ButtonVariant.Outline, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

    @Test fun drawerLight() = drawer(false)

    @Test fun drawerDark() = drawer(true)

    private fun menu(dark: Boolean) =
        snapshot("surfaces_nova_context", darkTheme = dark, stylePreset = StylePreset.Nova) {
            ContextMenu(defaultOpen = true) {
                ContextMenuTrigger("Right click here")
                ContextMenuContent {
                    ContextMenuItem("Back", {}, shortcut = "⌘[")
                    ContextMenuItem("Forward", {}, disabled = true)
                    ContextMenuSeparator()
                    ContextMenuCheckboxItem("Show Bookmarks", checked = true, onCheckedChange = {})
                }
            }
        }

    @Test fun menuLight() = menu(false)

    @Test fun menuDark() = menu(true)

    @Test fun collapsibleDisabledRootDisablesItsButton() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible(disabled = true) {
                CollapsibleTrigger("Details")
                CollapsibleContent("Hidden")
            }
        }
        composeRule.onNodeWithText("Details").assertIsNotEnabled()
        composeRule.onNodeWithText("Hidden").assertDoesNotExist()
    }

    @Test fun collapsibleControlledOnlyEmitsIntent() {
        var requested: Boolean? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible(open = false, onOpenChange = { requested = it }) {
                CollapsibleTrigger("Details")
                CollapsibleContent("Hidden")
            }
        }
        composeRule.onNodeWithText("Details").performClick()
        composeRule.runOnIdle { assertEquals(true, requested) }
        composeRule.onNodeWithText("Hidden").assertDoesNotExist()
    }

    @Test fun collapsibleUncontrolledExpandsAndCloses() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible {
                CollapsibleTrigger("Details")
                CollapsibleContent("Hidden", enter = EnterTransition.None, exit = ExitTransition.None)
            }
        }
        composeRule.onNodeWithText("Details").performClick()
        composeRule.onNodeWithText("Hidden").assertIsDisplayed()
        composeRule.onNodeWithText("Details").performClick()
        composeRule.onNodeWithText("Hidden").assertDoesNotExist()
    }

    @Test fun commandPointerReturnsCustomValue() {
        var value = ""
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command(
                onValueChange = { value = it },
            ) { CommandList { CommandItem("Calendar", {}, value = "calendar-id") } }
        }
        composeRule.onNodeWithText("Calendar").performClick()
        composeRule.runOnIdle { assertEquals("calendar-id", value) }
    }

    @Test fun commandKeyboardSkipsDisabledAndSupportsHomeEnd() {
        var picked = ""
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command {
                CommandInput(Modifier.testTag("search"))
                CommandList {
                    CommandItem("A", { picked = "A" })
                    CommandItem("B", { picked = "B" }, disabled = true)
                    CommandItem("C", { picked = "C" })
                }
            }
        }
        val input = composeRule.onNode(hasSetTextAction())
        input.requestFocus().performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("C", picked) }
        input.performKeyInput {
            pressKey(Key.MoveHome)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("A", picked) }
        input.performKeyInput {
            pressKey(Key.MoveEnd)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("C", picked) }
    }

    @Test fun commandFiltersHeadingAndShowsEmpty() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command {
                CommandInput()
                CommandList {
                    CommandEmpty()
                    CommandGroup(heading = "Suggestions") { CommandItem("Calendar", {}) }
                    CommandGroup(heading = "Settings") { CommandItem("Profile", {}) }
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).performTextInput("cal")
        composeRule.onNodeWithText("Calendar").assertIsDisplayed()
        composeRule.onNodeWithText("Settings").assertDoesNotExist()
        composeRule.onNode(hasSetTextAction()).performTextReplacement("unknown")
        composeRule.onNodeWithText("No results found.").assertIsDisplayed()
        composeRule.onNodeWithText("Suggestions").assertDoesNotExist()
    }

    @Test fun commandRecompositionRetainsKeyboardHighlight() {
        var render by mutableIntStateOf(0)
        var selected = ""
        setThemedContent(stylePreset = StylePreset.Nova) {
            val count = render
            Command {
                CommandInput()
                CommandList {
                    CommandItem("A", { selected = "A$count" })
                    CommandItem("B", { selected = "B$count" })
                }
            }
        }
        val input = composeRule.onNode(hasSetTextAction())
        input.requestFocus().performKeyInput { pressKey(Key.MoveHome) }
        composeRule.runOnIdle { render++ }
        input.performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals("A1", selected) }
    }

    @Test fun contextKeyboardOpensAndSkipsDisabled() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ContextMenu {
                ContextMenuTrigger("Area", Modifier.testTag("area"))
                ContextMenuContent {
                    ContextMenuItem("A", {})
                    ContextMenuItem("B", {}, disabled = true)
                    ContextMenuItem("C", {})
                }
            }
        }
        composeRule.onNodeWithTag("area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        composeRule.onNodeWithText("A").assertIsDisplayed().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("C").assertIsFocused()
    }

    @Test fun contextCheckboxStaysOpenAndUpdates() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            var checked by remember { mutableStateOf(false) }
            ContextMenu(defaultOpen = true) {
                ContextMenuContent { ContextMenuCheckboxItem("Bookmarks", checked, onCheckedChange = { checked = it }) }
            }
        }
        composeRule.onNodeWithContentDescription(
            "Bookmarks",
        ).performClick().assert(
            SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Checked"),
        )
    }

    @Test fun dialogEscapeDismisses() {
        var open by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Dialog(open, { open = it }) { DialogContent { Button("Action", modifier = Modifier.testTag("action")) } }
        }
        composeRule.onNodeWithTag("action").requestFocus().performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertFalse(open) }
    }

    @Test fun calendarDayIs28PixelsInNova() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(year = 2026, month = 10, onMonthChange = {
                    _,
                    _,
                ->
            }, selected = null, onSelectedChange = {})
        }
        composeRule.onNodeWithContentDescription("2026-10-15").assertWidthIsEqualTo(28.dp).assertHeightIsEqualTo(28.dp)
    }

    @Test fun calendarSelectionEmitsDate() {
        var selected: CalendarDate? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(year = 2026, month = 10, onMonthChange = {
                    _,
                    _,
                ->
            }, selected = null, onSelectedChange = { selected = it })
        }
        composeRule.onNodeWithContentDescription("2026-10-15").performClick()
        composeRule.runOnIdle { assertEquals(CalendarDate(2026, 10, 15), selected) }
    }

    @Test fun drawerDesktopPanelIs384PixelsWide() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(defaultOpen = true, swipeDirection = DrawerSwipeDirection.Right, showSwipeHandle = false) {
                DrawerContent(modifier = Modifier.testTag("drawer")) {
                    DrawerHeader("Delivery")
                    Text { "Options" }
                }
            }
        }
        composeRule.onNodeWithTag("drawer", useUnmergedTree = true).assertWidthIsEqualTo(384.dp)
    }

    @Test fun contextSubmenuArrowNavigationIsIndependent() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ContextMenu(defaultOpen = true) {
                ContextMenuContent {
                    ContextMenuSub {
                        ContextMenuSubTrigger("More")
                        ContextMenuSubContent {
                            ContextMenuItem("Child A", {})
                            ContextMenuItem("Child B", {})
                        }
                    }
                    ContextMenuItem("Parent", {})
                }
            }
        }
        composeRule.onNodeWithText("More").requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Child A").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Child B").assertIsFocused().performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithText("Child A").assertDoesNotExist()
    }

    @Test fun commandStartsWithFirstEnabledItemHighlighted() {
        var selected = ""
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command {
                CommandInput()
                CommandList {
                    CommandItem("Disabled", {}, disabled = true)
                    CommandItem("Calendar", { selected = "Calendar" })
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).requestFocus().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals("Calendar", selected) }
    }
}
