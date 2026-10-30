@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.surfaces

import androidx.compose.foundation.background
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.overlay.*
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

class SurfacesCompletionTest : ScreenshotTest() {
    @Test fun commandEndAndHomeKeepActiveItemInViewport() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command {
                CommandInput()
                CommandList { repeat(80) { CommandItem("Action $it", {}) } }
            }
        }
        val input = composeRule.onNode(hasSetTextAction())
        input.requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithText("Action 79").assertIsDisplayed()
        input.assertIsFocused().performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithText("Action 0").assertIsDisplayed()
    }

    @Test fun commandControlledValueDoesNotAcceptUnconfirmedNavigation() {
        var requested = ""
        var selected = ""
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command(value = "A", onValueChange = { requested = it }) {
                CommandInput()
                CommandList {
                    CommandItem("A", { selected = "A" })
                    CommandItem("B", { selected = "B" })
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).requestFocus().performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle {
            assertEquals("B", requested)
            assertEquals("A", selected)
        }
    }

    @Test fun dialogInitialFocusAndEscapeRestoration() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Dialog {
                DialogTrigger("Open", Modifier.testTag("trigger"))
                DialogContent {
                    Input("", {}, modifier = Modifier.testTag("field"))
                    Button("Last")
                }
            }
        }
        composeRule.onNodeWithText("Open").performClick()
        val input = composeRule.onNode(hasSetTextAction())
        input.assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Open").assertIsFocused()
    }

    @Test fun drawerInitialFocusAndCloseRestoration() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer {
                DrawerTrigger("Open")
                DrawerContent { DrawerClose { Button("Close", onClick = close) } }
            }
        }
        composeRule.onNodeWithText("Open").performClick()
        composeRule.onNodeWithText("Close").assertIsFocused().performClick()
        composeRule.onNodeWithText("Open").assertIsFocused()
    }

    @Test fun nonModalDialogAllowsBackgroundAction() {
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            OverlayHost {
                Button("Background", onClick = { clicks++ }, modifier = Modifier.testTag("background"))
                Dialog(defaultOpen = true, modal = false) { DialogContent { Button("Foreground") } }
            }
        }
        composeRule.onNodeWithText("Foreground").assertIsDisplayed()
        composeRule.onNodeWithText("Background").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test fun contextRtlArrowsAndTypeAhead() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ContextMenu(defaultOpen = true) {
                    ContextMenuContent {
                        ContextMenuItem("Apple", {})
                        ContextMenuItem("Cherry", {})
                        ContextMenuSub {
                            ContextMenuSubTrigger("More")
                            ContextMenuSubContent { ContextMenuItem("Child", {}) }
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Apple").performKeyInput { pressKey(Key.C) }
        composeRule.onNodeWithText("Cherry").assertIsFocused()
        composeRule.onNodeWithText("More").requestFocus().performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithText("Child").assertIsFocused().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Child").assertDoesNotExist()
    }

    @Test fun contextRestoresTriggerAfterEscape() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ContextMenu {
                ContextMenuTrigger("Area")
                ContextMenuContent { ContextMenuItem("Action", {}) }
            }
        }
        composeRule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        composeRule.onNodeWithText("Action").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Area").assertIsFocused()
    }

    @Test fun windowCursorFlipsAtEdgesAndIgnoresRootOffset() {
        val provider = WindowPointPositionProvider(IntOffset(390, 290))
        assertEquals(
            IntOffset(190, 190),
            provider.calculatePosition(
                IntRect(50, 50, 200, 200),
                IntSize(400, 300),
                LayoutDirection.Ltr,
                IntSize(200, 100),
            ),
        )
        assertEquals(
            IntOffset(190, 190),
            provider.calculatePosition(
                IntRect(50, 50, 200, 200),
                IntSize(400, 300),
                LayoutDirection.Rtl,
                IntSize(200, 100),
            ),
        )
    }

    @Test fun keepMountedRetainsInputButRejectsHiddenFocus() {
        var open by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible(open, { open = it }) {
                CollapsibleTrigger("Details")
                CollapsibleContent(keepMounted = true) {
                    var value by remember { mutableStateOf("") }
                    Input(value, { value = it })
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).performTextInput("kept")
        composeRule.onNodeWithText("Details").performClick()
        composeRule.onNode(hasSetTextAction()).assertDoesNotExist()
        composeRule.onNodeWithText("Details").performClick()
        composeRule.onNode(hasSetTextAction()).assertTextEquals("kept")
    }

    @Test fun collapsibleCanReverseMidTransition() {
        var open by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible(open, { open = it }) {
                CollapsibleTrigger("Details")
                CollapsibleContent("Body")
            }
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { open = true }
        composeRule.mainClock.advanceTimeBy(80)
        composeRule.runOnIdle { open = false }
        composeRule.mainClock.advanceTimeBy(40)
        composeRule.runOnIdle { open = true }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.onNodeWithText("Body").assertIsDisplayed()
        composeRule.mainClock.autoAdvance = true
    }

    @Test fun calendarChineseLabelsAndRtlDayNavigation() {
        var selected: CalendarDate? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalCalendarLocale provides CalendarLocale.Chinese,
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                Calendar(2026, 10, { _, _ -> }, CalendarDate(2026, 10, 15), { selected = it })
            }
        }
        composeRule.onNodeWithText("2026年10月").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("上个月").assertExists()
        composeRule.onNodeWithContentDescription("2026年10月15日").requestFocus().performKeyInput {
            pressKey(Key.DirectionLeft)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals(CalendarDate(2026, 10, 16), selected) }
        composeRule.onNodeWithContentDescription("2026年10月16日").assertIsFocused()
    }

    private fun localizedRange(dark: Boolean) =
        snapshot("surfaces_completion_range", darkTheme = dark, stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalCalendarLocale provides CalendarLocale.Chinese) {
                Calendar(
                    year = 2026,
                    month = 10,
                    onMonthChange = {
                            _,
                            _,
                        ->
                    },
                    selectedRange =
                        CalendarDateRange(
                            CalendarDate(2026, 10, 12),
                            CalendarDate(2026, 10, 16),
                        ),
                    onSelectedRangeChange = {
                    },
                )
            }
        }

    @Test fun localizedRangeLight() = localizedRange(false)

    @Test fun localizedRangeDark() = localizedRange(true)

    @Test fun commandDoesNotCommitWhileImeIsComposing() {
        var composing: ((Boolean) -> Unit)? = null
        var selected = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command {
                CommandInput(icon = {
                    val observer = com.toutiaoyan.compose.ui.components.input.LocalInputCompositionObserver.current
                    SideEffect { composing = observer }
                })
                CommandList { CommandItem("中文", { selected++ }) }
            }
        }
        composeRule.onNode(hasSetTextAction()).requestFocus()
        composeRule.runOnIdle { checkNotNull(composing).invoke(true) }
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle {
            assertEquals(0, selected)
            checkNotNull(composing).invoke(false)
        }
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(1, selected) }
    }

    @Test fun modalTabCyclesWithinActions() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Button("Background")
            Dialog(defaultOpen = true) {
                DialogContent(showCloseButton = false) {
                    Button("First")
                    Button("Last")
                }
            }
        }
        composeRule.onNodeWithText("First").assertIsFocused().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithText("Last").assertIsFocused().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithText("First").assertIsFocused()
    }

    @Test fun nestedDrawerCloseReturnsToParentAction() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(defaultOpen = true) {
                DrawerContent {
                    Drawer {
                        DrawerTrigger("Nested")
                        DrawerContent { DrawerClose { Button("Child Close", onClick = close) } }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Nested").performClick()
        composeRule.onNodeWithText("Child Close").performClick()
        composeRule.onNodeWithText("Nested").assertIsFocused()
    }

    @Test fun controlledSnapChangeMovesPanelWithoutEmittingDismiss() {
        var point by mutableStateOf<DrawerSnapPoint>(DrawerSnapPoint.Absolute(200.dp))
        var dismissed = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(
                open = true,
                onOpenChange = {
                    if (!it) dismissed = true
                },
                snapPoints =
                    listOf(
                        DrawerSnapPoint.Absolute(200.dp),
                        DrawerSnapPoint.Absolute(500.dp),
                    ),
                snapPoint = point,
                snapToSequentialPoints = true,
            ) {
                DrawerContent(Modifier.testTag("snap")) { DrawerHeader("Snap") }
            }
        }
        val before = composeRule.onNodeWithText("Snap", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow.top
        composeRule.runOnIdle { point = DrawerSnapPoint.Absolute(500.dp) }
        composeRule.waitUntil(timeoutMillis = 2000) {
            composeRule.onNodeWithText("Snap", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow.top < before
        }
        val after = composeRule.onNodeWithText("Snap", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow.top
        composeRule.runOnIdle {
            assertTrue(after < before)
            assertFalse(dismissed)
        }
    }

    @Test fun externalCollapseMovesExistingContentFocusToTrigger() {
        var open by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible(open, { open = it }) {
                CollapsibleTrigger("Details")
                CollapsibleContent(keepMounted = true) { Input("", {}) }
            }
        }
        composeRule.onNodeWithText("Details").requestFocus()
        composeRule.onNode(hasSetTextAction()).requestFocus()
        composeRule.runOnIdle { open = false }
        composeRule.onNodeWithText("Details").assertIsFocused()
    }

    @Test fun defaultHighlightDoesNotScrollAnOffscreenExampleIntoView() {
        lateinit var scroll: androidx.compose.foundation.ScrollState
        setThemedContent(stylePreset = StylePreset.Nova) {
            val state = androidx.compose.foundation.rememberScrollState()
            SideEffect { scroll = state }
            Column(Modifier.height(300.dp).verticalScroll(state)) {
                Spacer(Modifier.height(600.dp))
                Command {
                    CommandInput()
                    CommandList { CommandItem("Calendar", {}) }
                }
            }
        }
        composeRule.runOnIdle { assertEquals(0, scroll.value) }
    }

    private fun backdrop(
        dark: Boolean,
        drawer: Boolean,
    ) {
        composeRule.setContent {
            com.toutiaoyan.compose.ui.theme.Theme(preset = StylePreset.Nova, isDark = dark) {
                OverlayHost {
                    Column(Modifier.fillMaxSize().background(currentTheme.colors.background).padding(24.dp)) {
                        repeat(18) { Text { "Background line $it — a sharp surface below the modal" } }
                    }
                    if (drawer) {
                        Drawer(defaultOpen = true) {
                            DrawerContent {
                                DrawerHeader("Delivery", "Blurred backdrop")
                                Button("Confirm")
                            }
                        }
                    } else {
                        Dialog(
                            defaultOpen = true,
                        ) { DialogContent("Edit profile", description = "Blurred backdrop") { Button("Save") } }
                    }
                }
            }
        }
        captureNamed(if (drawer) "surfaces_completion_drawer_backdrop" else "surfaces_completion_dialog_backdrop", dark)
    }

    @Test fun dialogBackdropLight() = backdrop(false, false)

    @Test fun dialogBackdropDark() = backdrop(true, false)

    @Test fun drawerBackdropLight() = backdrop(false, true)

    @Test fun drawerBackdropDark() = backdrop(true, true)

    @Test fun hiddenOutsideDaysAreAbsentFromFocusAndAccessibility() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(2026, 10, { _, _ -> }, CalendarDate(2026, 10, 15), {}, showOutsideDays = false)
        }
        composeRule.onNodeWithContentDescription("2026-9-27").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("2026-10-15").assertIsDisplayed()
    }

    @Test fun calendarMonthBoundsDisableNavigationAndDoNotDisableMidMonthDates() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(2026, 10, { _, _ -> }, CalendarDate(2026, 10, 15), {
            }, startMonth = CalendarDate(2026, 10, 20), endMonth = CalendarDate(2026, 10, 5))
        }
        composeRule.onNodeWithContentDescription("Previous month").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Next month").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("2026-10-1").assertIsEnabled()
        composeRule.onNodeWithContentDescription("2026-10-31").assertIsEnabled()
    }

    private fun nestedBackdrop(dark: Boolean) {
        composeRule.setContent {
            com.toutiaoyan.compose.ui.theme.Theme(preset = StylePreset.Nova, isDark = dark) {
                OverlayHost {
                    Box(
                        Modifier.fillMaxSize().background(currentTheme.colors.background),
                    ) { Text { "Underlying application" } }
                    Drawer(defaultOpen = true, swipeDirection = DrawerSwipeDirection.Right) {
                        DrawerContent {
                            DrawerHeader("Parent drawer", "Parent remains visible below its nested backdrop")
                            Drawer {
                                DrawerTrigger("Nested")
                                DrawerContent {
                                    DrawerHeader("Child drawer")
                                    Button("Child action")
                                }
                            }
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Nested").performClick()
        captureNamed("surfaces_completion_nested_backdrop", dark)
    }

    @Test fun nestedBackdropLight() = nestedBackdrop(false)

    @Test fun nestedBackdropDark() = nestedBackdrop(true)

    @Test fun nonModalContextMenuRemainsKeyboardInteractiveInHost() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            OverlayHost {
                ContextMenu(defaultOpen = true, modal = false) {
                    ContextMenuContent {
                        ContextMenuItem("First", {})
                        ContextMenuItem("Last", {})
                    }
                }
            }
        }
        composeRule.onNodeWithText("First").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Last").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("First").assertDoesNotExist()
    }

    @Test fun popupCalendarOptsIntoSelectedDayInitialFocus() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
                Calendar(2026, 10, { _, _ -> }, CalendarDate(2026, 10, 15), {})
            }
        }
        composeRule.onNodeWithContentDescription("2026-10-15").assertIsFocused()
    }
}
