@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.focused
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius
import com.toutiaoyan.compose.ui.components.contextmenu.*
import com.toutiaoyan.compose.ui.components.menubar.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import java.io.File
import javax.imageio.ImageIO
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class DropdownMenuLiveStyleTest {
    @get:Rule val rule = createComposeRule()

    @Test fun allFourRowsFollowLiveMdCustomScaleAndCallerShapeAcrossPresetRoundtrip() {
        var base by mutableStateOf(0)
        var preset by mutableStateOf(StylePreset.Nova)
        var custom by mutableStateOf(false)
        var caller by mutableStateOf(false)
        var expected by mutableStateOf(0)
        rule.setContent {
            Theme(preset = preset, baseRadius = Radius(base.dp), minTouchTarget = null) {
                val scale = LocalTwRadius.current
                CompositionLocalProvider(LocalTwRadius provides if (custom) scale.copy(md = 12.dp) else scale) {
                    RadiusFixture(expected) { caller }
                }
            }
        }

        for (radius in listOf(0, 10, 20)) {
            rule.runOnIdle {
                base = radius
                expected = (radius * .8f).toInt()
            }
            checkCorners(expected)
        }
        rule.runOnIdle {
            custom = true
            expected = 12
        }
        checkCorners(expected)
        rule.runOnIdle {
            caller = true
            expected = 4
        }
        checkCorners(expected)
        rule.runOnIdle { preset = StylePreset.Lyra }
        checkCorners(expected)
        rule.runOnIdle {
            preset = StylePreset.Nova
            caller = false
        }
        rule.runOnIdle { expected = 12 }
        rule.onNodeWithTag("action").requestFocus()
        checkCorners(expected)
    }

    @Composable
    private fun RadiusFixture(
        referenceRadius: Int,
        callerShape: () -> Boolean,
    ) {
        Column(Modifier.background(Color.White)) {
            Box(
                Modifier.size(160.dp, 32.dp)
                    .background(Color.Black, RoundedCornerShape(referenceRadius.dp)).testTag("reference"),
            )
            DropdownMenu(open = true, onOpenChange = {}, modal = false) {
                DropdownMenuContent(
                    style =
                        Style {
                            width(168.dp)
                            background(Color.White)
                        },
                ) {
                    Spacer(Modifier.height(16.dp))
                    val rowStyle =
                        Style {
                            height(32.dp)
                            background(Color.Black)
                            focused { background(Color.Black) }
                            if (callerShape()) shape(RoundedCornerShape(4.dp))
                        }
                    RadiusRows(rowStyle)
                }
            }
        }
    }

    @Composable
    private fun DropdownMenuScope.RadiusRows(rowStyle: Style) {
        DropdownMenuItem(
            "Action",
            {},
            Modifier.testTag("action").background(Color.White),
            style = rowStyle,
        )
        DropdownMenuCheckboxItem(
            "Check",
            false,
            Modifier.testTag("check").background(Color.White),
            style = rowStyle,
            onCheckedChange = {},
        )
        DropdownMenuRadioGroup("a", {}) {
            DropdownMenuRadioItem(
                "a",
                "Radio",
                Modifier.testTag("radio").background(Color.White),
                style = rowStyle,
            )
        }
        DropdownMenuSub {
            DropdownMenuSubTrigger(
                "Sub",
                Modifier.testTag("sub").background(Color.White),
                style = rowStyle,
            )
        }
    }

    private fun checkCorners(expected: Int) {
        val report = File("build/reports/dropdown-stage20/radius").apply { mkdirs() }
        val referenceImage = rule.onNodeWithTag("reference").captureToImage()
        val reference = referenceImage.toPixelMap()
        ImageIO.write(referenceImage.toAwtImage(), "png", File(report, "reference-$expected.png"))
        for (tag in listOf("action", "check", "radio", "sub")) {
            val actualImage = rule.onNodeWithTag(tag).captureToImage()
            val actual = actualImage.toPixelMap()
            ImageIO.write(actualImage.toAwtImage(), "png", File(report, "$tag-$expected.png"))
            val node = rule.onNodeWithTag(tag).fetchSemanticsNode()
            File(report, "$tag-$expected.txt").writeText(
                "reference=${reference[0, 0]} actual=${actual[0, 0]} " +
                    "referenceSize=${reference.width}x${reference.height} " +
                    "actualSize=${actual.width}x${actual.height} " +
                    "bounds=${node.boundsInWindow} config=${node.config}",
            )
            for (y in 0..3) for (x in 0..15) {
                assertEquals(reference[x, y].red, actual[x, y].red, .06f, "$tag radius=$expected at $x,$y")
            }
        }
    }

    @Test fun novaLabelHasRealUntrackedLayoutAndShortcutKeepsEmTracking() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                DropdownMenu(open = true, onOpenChange = {}, modal = false) {
                    DropdownMenuContent {
                        DropdownMenuLabel("My Account")
                        DropdownMenuItem("Profile", {}, shortcut = "⌘P")
                    }
                }
            }
        }
        val label = layout("My Account")
        assertEquals(12.sp, label.layoutInput.style.fontSize)
        assertEquals(16.sp, label.layoutInput.style.lineHeight)
        assertEquals(FontWeight.Medium, label.layoutInput.style.fontWeight)
        assertEquals(0.sp, label.layoutInput.style.letterSpacing)
        assertTrue(label.getLineRight(0) - label.getLineLeft(0) > 0)
        assertFalse(label.hasVisualOverflow)
        assertEquals(.1f, layout("⌘P").layoutInput.style.letterSpacing.value)
    }

    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }
}

class DropdownMenuOwnershipTest {
    @get:Rule val rule = createComposeRule()

    @Test fun menubarAliasesKeepTheirOwnerButIndependentNestedDropdownGetsItsOwnRecipe() {
        var barOwner = false
        var independentOwner = true
        var contextHasDropdownMarker = true
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                DropdownMenu(open = true, onOpenChange = {}, modal = false) {
                    DropdownMenuContent {
                        Menubar(defaultValue = "file", modal = false) {
                            MenubarMenu("file") {
                                MenubarContent {
                                    val matches = isInMenubarMenu(dropdownMenuRootOwner())
                                    SideEffect { barOwner = matches }
                                    MenubarLabel("Bar heading")
                                    MenubarSub {
                                        MenubarSubTrigger("Bar sub")
                                        MenubarSubContent { MenubarItem("Bar leaf", {}) }
                                    }
                                    DropdownMenu(open = true, onOpenChange = {}, modal = false) {
                                        DropdownMenuContent {
                                            val nestedMatches = isInMenubarMenu(dropdownMenuRootOwner())
                                            SideEffect { independentOwner = nestedMatches }
                                            DropdownMenuLabel("Independent heading")
                                        }
                                    }
                                }
                            }
                        }
                        ContextMenu(open = true, onOpenChange = {}, modal = false) {
                            ContextMenuContent {
                                val marker = LocalDropdownMenuContent.current
                                SideEffect { contextHasDropdownMarker = marker }
                                ContextMenuLabel("Context heading")
                            }
                        }
                    }
                }
            }
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertTrue(barOwner)
            assertFalse(independentOwner)
            assertFalse(contextHasDropdownMarker)
        }
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("Independent heading", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(0.sp, layouts.single().layoutInput.style.letterSpacing)
        rule.onNodeWithText("Bar heading").assertExists()
        rule.onNodeWithText("Context heading").assertExists()
    }
}
