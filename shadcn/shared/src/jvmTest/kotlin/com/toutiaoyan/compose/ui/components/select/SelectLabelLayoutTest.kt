@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import com.toutiaoyan.compose.ui.theme.Theme
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectLabelLayoutTest : ScreenshotTest() {
    @Test fun novaArabicFontMetricsDoNotEnlargeSingleLineOptions() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(
                preset = StylePreset.Nova,
                fontFamily = FontFamily(Font("fonts/noto_sans_arabic.ttf")),
                minTouchTarget = null,
            ) {
                Select(value = "منظر طبيعي جميل", onValueChange = {}, defaultOpen = true) {
                    SelectTrigger(Modifier.width(180.dp))
                    SelectContent {
                        SelectItem("منظر طبيعي جميل", Modifier.testTag("arabic row"))
                        SelectItem("English", Modifier.testTag("english row"))
                    }
                }
            }
        }
        composeRule.onNodeWithTag("arabic row").assertHeightIsEqualTo(28.dp)
            .assert(hasText("منظر طبيعي جميل")).assertIsSelected().assertHasClickAction()
        composeRule.onNodeWithTag("english row").assertHeightIsEqualTo(28.dp)
    }

    private fun arabicOptions(dark: Boolean) {
        snapshot("select_nova_arabic_options", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Theme(
                preset = StylePreset.Nova,
                isDark = dark,
                fontFamily = FontFamily(Font("fonts/noto_sans_arabic.ttf")),
                minTouchTarget = null,
            ) {
                Select(value = "منظر طبيعي جميل", onValueChange = {}, defaultOpen = true) {
                    SelectTrigger(Modifier.width(180.dp))
                    SelectContent {
                        SelectItem("منظر طبيعي جميل")
                        SelectItem("English")
                    }
                }
            }
        }
    }

    @Test fun arabicOptionsLight() = arabicOptions(false)

    @Test fun arabicOptionsDark() = arabicOptions(true)

    @Test fun novaLanguageLabelsStaySingleLineAtOfficialAnchorWidth() {
        var selected by mutableStateOf("Arabic (العربية)")
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select(value = selected, onValueChange = { selected = it }, defaultOpen = true) {
                SelectTrigger(Modifier.width(144.dp))
                SelectContent(Modifier.testTag("panel")) {
                    SelectItem("English")
                    SelectItem("Arabic (العربية)", Modifier.testTag("arabic"))
                    SelectItem("Hebrew (עברית)", Modifier.testTag("hebrew"))
                }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("Hebrew (עברית)").performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(layouts)
        }
        assertEquals(1, layouts.single().lineCount)
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(144.dp)
        composeRule.onNodeWithTag("hebrew").assertHeightIsEqualTo(28.dp)
        composeRule.onNodeWithTag("arabic").performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        assertEquals("Hebrew (עברית)", selected)
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun veryLongLabelKeepsRowHeightAndPanelWidth() {
        val label = "A very long language label that exceeds the anchor width"
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select(value = "Short", onValueChange = {}, defaultOpen = true) {
                SelectTrigger(Modifier.width(144.dp))
                SelectContent(Modifier.testTag("panel")) {
                    SelectItem("Short")
                    SelectItem(label, Modifier.testTag("long option"))
                }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(label).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        composeRule.onNodeWithTag("long option").assertHeightIsEqualTo(28.dp)
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(144.dp)
    }

    @Test fun novaSelectedValueStaysSingleLineInNarrowSmallTrigger() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select(value = "Hebrew (עברית)", onValueChange = {}) {
                SelectTrigger(Modifier.width(144.dp).testTag("small trigger"), size = SelectSize.Sm)
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("Hebrew (עברית)", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        composeRule.onNodeWithTag("small trigger").assertHeightIsEqualTo(28.dp).assertWidthIsEqualTo(144.dp)
    }

    @Test fun novaLongSelectedValueHasSingleConstrainedLine() {
        val label = "A very long selected language value that cannot fit in a small trigger"
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select(value = label, onValueChange = {}) {
                SelectTrigger(Modifier.width(120.dp).testTag("long trigger"), size = SelectSize.Sm)
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(label, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        composeRule.onNodeWithTag("long trigger").assertWidthIsEqualTo(120.dp).assertHeightIsEqualTo(28.dp)
    }
}
