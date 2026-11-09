@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class MenubarParityTest : ScreenshotTest() {
    private var open: String? = null
    private var activateKeyboard: () -> Unit = {}

    @Composable
    private fun Sample(direction: LayoutDirection = LayoutDirection.Ltr) {
        var selected by remember { mutableStateOf<String?>(null) }
        open = selected
        val inputMode = LocalInputModeManager.current
        activateKeyboard = {
            inputMode.requestInputMode(InputMode.Keyboard)
        }
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Menubar(Modifier.width(288.dp).testTag("bar"), value = selected, onValueChange = { selected = it }) {
                MenubarMenu("file") {
                    MenubarTrigger("File", Modifier.testTag("file"))
                    MenubarContent(Modifier.testTag("file-panel")) {
                        MenubarItem("New Tab", onClick = {})
                        MenubarItem("Disabled", onClick = {}, disabled = true)
                        MenubarItem("Print", onClick = {})
                        MenubarSub {
                            MenubarSubTrigger("Share")
                            MenubarSubContent { MenubarItem("Email link", onClick = {}) }
                        }
                    }
                }
                MenubarMenu("disabled") { MenubarTrigger("Unavailable", disabled = true) }
                MenubarMenu("edit") {
                    MenubarTrigger("Edit", Modifier.testTag("edit"))
                    MenubarContent { MenubarItem("Undo", onClick = {}) }
                }
            }
        }
    }

    private fun show(direction: LayoutDirection = LayoutDirection.Ltr) {
        setThemedContent(stylePreset = StylePreset.Nova) { Sample(direction) }
        composeRule.runOnIdle { activateKeyboard() }
    }

    @Test fun novaBarHas32PixelHeightTwoPixelGapAnd24PixelTrigger() {
        show()
        val bar = composeRule.onNodeWithTag("bar").assertHeightIsEqualTo(32.dp).fetchSemanticsNode().boundsInRoot
        val file = composeRule.onNodeWithTag("file").assertHeightIsEqualTo(24.dp).fetchSemanticsNode().boundsInRoot
        assertEquals(4f, file.left - bar.left, 0.1f)
        val next = composeRule.onNodeWithText("Unavailable").fetchSemanticsNode().boundsInRoot
        assertEquals(2f, next.left - file.right, 0.1f)
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("File", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(14f, results.single().layoutInput.style.fontSize.value)
        assertEquals(20f, results.single().layoutInput.style.lineHeight.value)
        assertEquals(FontWeight.Medium, results.single().layoutInput.style.fontWeight)
    }

    @Test fun horizontalKeysSkipDisabledTriggersAndWrapWithoutOpening() {
        show()
        composeRule.onNodeWithTag("file").requestFocus()
        composeRule.onNodeWithTag("file").performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithTag("edit").assertIsFocused()
        composeRule.onNodeWithTag("edit").performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithTag("file").assertIsFocused()
        assertNull(open)
    }

    @Test fun rtlPhysicalLeftAdvancesToTheNextEnabledTrigger() {
        show(LayoutDirection.Rtl)
        composeRule.onNodeWithTag("file").requestFocus()
        composeRule.onNodeWithTag("file").performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithTag("edit").assertIsFocused()
        assertNull(open)
    }

    @Test fun downOpensWithMenuNavigationAndEscapeRestoresTriggerFocus() {
        show()
        composeRule.onNodeWithTag("file").requestFocus()
        composeRule.onNodeWithTag("file").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("file-panel").assertWidthIsEqualTo(144.dp)
        composeRule.onNodeWithText("New Tab").assertIsFocused()
        composeRule.onNodeWithText("New Tab").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Print").assertIsFocused()
        composeRule.onNodeWithText("Print").performKeyInput { pressKey(Key.Escape) }
        composeRule.waitForIdle()
        assertNull(open)
        composeRule.onNodeWithTag("file").assertIsFocused()
    }

    @Test fun enterOpensAndFocusesTheFirstItem() = opensAndFocusesTheFirstItem(Key.Enter)

    @Test fun spaceOpensAndFocusesTheFirstItem() = opensAndFocusesTheFirstItem(Key.Spacebar)

    private fun opensAndFocusesTheFirstItem(key: Key) {
        show()
        composeRule.onNodeWithTag("file").requestFocus().performKeyInput { pressKey(key) }
        composeRule.onNodeWithText("New Tab").assertIsFocused()
        assertEquals("file", open)
    }

    @Test fun horizontalKeyInAnOpenMenuSwitchesMenuAndFocusesItsFirstItem() {
        show()
        composeRule.onNodeWithTag("file").performClick()
        composeRule.onNodeWithText("New Tab").performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Undo").assertIsFocused()
        assertEquals("edit", open)
    }

    @Test fun upOpensAtTheLastItemAndHomeEndMoveBetweenTriggers() {
        show()
        composeRule.onNodeWithTag("file").requestFocus()
        composeRule.onNodeWithTag("file").performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithTag("edit").assertIsFocused()
        composeRule.onNodeWithTag("edit").performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithTag("file").assertIsFocused()
        composeRule.onNodeWithTag("file").performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.onNodeWithText("Share").assertIsFocused()
    }

    @Test fun submenuGetsTheOpenArrowBeforeTopLevelSwitching() {
        show()
        composeRule.onNodeWithTag("file").performClick()
        composeRule.onNodeWithText("Share").requestFocus()
        composeRule.onNodeWithText("Share").performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Email link").assertIsDisplayed()
        assertEquals("file", open)
    }

    @Test fun escapeInSubmenuClosesOnlyThatLayerBeforeRestoringBarFocus() {
        show()
        composeRule.onNodeWithTag("file").performClick()
        composeRule.onNodeWithText("Share").requestFocus()
        composeRule.onNodeWithText("Share").performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Email link").assertIsFocused()
            .performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Email link").assertDoesNotExist()
        assertEquals("file", open)
        composeRule.onNodeWithText("Share").assertIsFocused()
            .performKeyInput { pressKey(Key.Escape) }
        assertNull(open)
        composeRule.onNodeWithTag("file").assertIsFocused()
    }

    @Test fun itemContentSlotForwardsRichLabels() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Menubar(defaultValue = "file") {
                MenubarMenu("file") {
                    MenubarTrigger("File")
                    MenubarContent {
                        MenubarItem("New File", onClick = {}) { Text { "Rich label" } }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Rich label").assertIsDisplayed()
    }

    @Test fun labelUsesFourteenTwentyMediumAndCheckboxExposesIndeterminateState() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Menubar(defaultValue = "view") {
                MenubarMenu("view") {
                    MenubarTrigger("View")
                    MenubarContent {
                        MenubarLabel("Appearance", Modifier.testTag("label"), inset = true)
                        MenubarCheckboxItem(
                            "Mixed",
                            checked =
                            false,
                            modifier =
                                Modifier.testTag("mixed"),
                            indeterminate =
                            true,
                            onCheckedChange =
                                {},
                        )
                    }
                }
            }
        }
        val mixed = composeRule.onNodeWithTag("mixed").fetchSemanticsNode().boundsInRoot
        val mixedText = composeRule.onNodeWithText("Mixed", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(28f, mixedText.left - mixed.left, 0.1f)
        composeRule.onNodeWithTag("label").assertHeightIsEqualTo(28.dp)
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Appearance",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(14f, layouts.single().layoutInput.style.fontSize.value)
        assertEquals(20f, layouts.single().layoutInput.style.lineHeight.value)
        assertEquals(FontWeight.Medium, layouts.single().layoutInput.style.fontWeight)
        composeRule.onNodeWithText("Mixed").assert(
            SemanticsMatcher.expectValue(
                androidx.compose.ui.semantics.SemanticsProperties.ToggleableState,
                androidx.compose.ui.state.ToggleableState.Indeterminate,
            ),
        )
    }

    private fun nova(dark: Boolean) =
        snapshot("menubar_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Menubar(Modifier.width(288.dp)) {
                listOf("File", "Edit", "View", "Profiles").forEach { label ->
                    MenubarMenu(label) { MenubarTrigger(label) }
                }
            }
        }

    private fun menuNova(dark: Boolean) =
        snapshot(
            "menubar_menu_nova",
            darkTheme =
            dark,
            stylePreset =
                StylePreset.Nova,
        ) {
            Box(Modifier.width(288.dp).height(240.dp)) {
                Menubar(defaultValue = "file") {
                    MenubarMenu("file") {
                        MenubarTrigger("File")
                        MenubarContent {
                            MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                            MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                            MenubarItem("New Incognito Window", onClick = {}, disabled = true)
                            MenubarSeparator()
                            MenubarSub {
                                MenubarSubTrigger("Share")
                                MenubarSubContent { MenubarItem("Email link", onClick = {}) }
                            }
                        }
                    }
                }
            }
        }

    private fun selectionNova(dark: Boolean) =
        snapshot(
            "menubar_selection_nova",
            darkTheme =
            dark,
            stylePreset =
                StylePreset.Nova,
        ) {
            Box(Modifier.width(288.dp).height(300.dp)) {
                Menubar(defaultValue = "view") {
                    MenubarMenu("view") {
                        MenubarTrigger("View")
                        MenubarContent {
                            MenubarCheckboxItem("Bookmarks Bar", checked = false, onCheckedChange = {})
                            MenubarCheckboxItem("Full URLs", checked = true, onCheckedChange = {})
                            MenubarSeparator()
                            MenubarLabel("Profiles", inset = true)
                            MenubarRadioGroup("benoit", onValueChange = {}) {
                                MenubarRadioItem("andy", "Andy")
                                MenubarRadioItem("benoit", "Benoit")
                                MenubarRadioItem("luis", "Luis")
                            }
                        }
                    }
                }
            }
        }

    @Test fun selection_nova_light() = selectionNova(false)

    @Test fun selection_nova_dark() = selectionNova(true)

    @Test fun menu_nova_light() = menuNova(false)

    @Test fun menu_nova_dark() = menuNova(true)

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
