package com.toutiaoyan.compose.ui.components.trace

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class TraceParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("trace_parity", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Trace(Modifier.width(384.dp)) {
                TraceLine {
                    TraceIcon { Icon(Check) }
                    TraceTitle("Searched the web")
                    TraceDescription("2.4s")
                }
                TraceLine(status = TraceStatus.Pending) { TraceTitle("Waiting for results") }
                TraceLine(status = TraceStatus.Error) { TraceTitle("Unable to read page") }
                TraceCollapsible(defaultOpen = true) {
                    TraceCollapsibleTrigger { TraceLine { TraceTitle("Read 3 pages") } }
                    TraceCollapsibleContent { Text { "Reviewed documentation and examples." } }
                }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    @Test fun uncontrolledTriggerExpandsAndCollapsesDetails() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Details").assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithText("Details").assertDoesNotExist()
    }

    @Test fun controlledTriggerOnlyRequestsStateChange() {
        var requested: Boolean? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible(open = false, onOpenChange = { requested = it }) {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").performClick()
        assertEquals(true, requested)
        composeRule.onNodeWithText("Details").assertDoesNotExist()
    }

    @Test fun keyboardEnterExpandsDetails() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test fun disabledTriggerCannotExpand() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible(enabled = false) {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").assertIsNotEnabled().performClick()
        composeRule.onNodeWithText("Details").assertDoesNotExist()
    }

    private fun extras(
        name: String,
        rtl: Boolean,
    ) {
        for (dark in listOf(false, true)) {
            snapshot(name, darkTheme = dark, stylePreset = StylePreset.Nova) {
                if (rtl) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        ExtrasMatrix()
                    }
                } else {
                    ExtrasMatrix()
                }
            }
        }
    }

    @Composable
    private fun ExtrasMatrix() {
        Trace(Modifier.width(384.dp)) {
            TraceLine {
                TraceIcon { Icon(Check) }
                TraceTitle("Edited trace.ktx")
                TraceDescription("+48 \u221212", mono = true)
                TraceIcon { Icon(ChevronRight) }
            }
            TraceCollapsible(defaultOpen = true) {
                TraceCollapsibleTrigger(Modifier.testTag("outer")) {
                    TraceLine {
                        TraceIcon { Icon(Check) }
                        TraceTitle("Read 3 pages")
                    }
                }
                TraceCollapsibleContent(keepMounted = true) {
                    Trace(Modifier.fillMaxWidth()) {
                        TraceLine {
                            TraceIcon { Icon(Check) }
                            TraceTitle("Nested row")
                        }
                    }
                    Text { "Reviewed documentation and examples." }
                }
            }
        }
    }

    @Test fun extrasMatrix() = extras("trace_parity_extras", rtl = false)

    @Test fun rtlMatrix() = extras("trace_parity_rtl", rtl = true)

    @Test fun keepMountedPreservesNestedOpenState() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace(Modifier.width(384.dp)) {
                    TraceCollapsible(defaultOpen = true) {
                        TraceCollapsibleTrigger(Modifier.testTag("outer")) {
                            TraceLine { TraceTitle("Outer") }
                        }
                        TraceCollapsibleContent(keepMounted = true) {
                            TraceCollapsible {
                                TraceCollapsibleTrigger(Modifier.testTag("inner")) {
                                    TraceLine { TraceTitle("Inner") }
                                }
                                TraceCollapsibleContent { Text { "Inner detail" } }
                            }
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("inner").performClick()
        composeRule.onNodeWithText("Inner detail").assertIsDisplayed()
        composeRule.onNodeWithTag("outer").performClick()
        composeRule.onNodeWithText("Inner detail").assertDoesNotExist()
        composeRule.onNodeWithTag("outer").performClick()
        composeRule.onNodeWithText("Inner detail").assertIsDisplayed()
    }

    @Test fun unkeptContentDropsNestedOpenStateWhenTheOuterRowCollapses() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace(Modifier.width(384.dp)) {
                    TraceCollapsible(defaultOpen = true) {
                        TraceCollapsibleTrigger(Modifier.testTag("outer")) {
                            TraceLine { TraceTitle("Outer") }
                        }
                        TraceCollapsibleContent {
                            TraceCollapsible {
                                TraceCollapsibleTrigger(Modifier.testTag("inner")) {
                                    TraceLine { TraceTitle("Inner") }
                                }
                                TraceCollapsibleContent { Text { "Inner detail" } }
                            }
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("inner").performClick()
        composeRule.onNodeWithText("Inner detail").assertIsDisplayed()
        composeRule.onNodeWithTag("outer").performClick()
        composeRule.onNodeWithText("Inner detail").assertDoesNotExist()
        composeRule.onNodeWithTag("outer").performClick()
        composeRule.onNodeWithText("Inner detail").assertDoesNotExist()
    }

    @Test fun explicitErrorStatusColorsTheAutomaticChevron() {
        var expected = androidx.compose.ui.graphics.Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                expected = currentTheme.colors.destructive
                TraceCollapsible {
                    TraceCollapsibleTrigger(status = TraceStatus.Error, modifier = Modifier.testTag("error-trigger")) {
                        Box(Modifier.size(20.dp))
                    }
                }
            }
        }
        val pixels = composeRule.onNodeWithTag("error-trigger").captureToImage().toPixelMap()
        assertTrue(
            (0 until pixels.width).any { x ->
                (0 until pixels.height).any { y -> pixels[x, y] == expected }
            },
            "The automatic chevron must use the line's explicit destructive status color",
        )
    }

    @Test fun keyboardLinkActivationWaitsForReleaseAndOpensOnce() {
        val href = "https://example.com/docs"
        val opened = mutableListOf<String>()
        val handler =
            object : UriHandler {
                override fun openUri(uri: String) {
                    opened.add(uri)
                }
            }
        setThemedContent {
            CompositionLocalProvider(LocalUriHandler provides handler) {
                TraceLink(href, Modifier.testTag("link")) { Text { "Read docs" } }
            }
        }
        val link = composeRule.onNodeWithTag("link").requestFocus()
        link.performKeyInput { keyDown(Key.Enter) }
        composeRule.runOnIdle { assertTrue(opened.isEmpty()) }
        link.performKeyInput { keyUp(Key.Enter) }
        composeRule.runOnIdle { assertEquals(listOf(href), opened) }
    }

    @Test fun linkRowKeepsTheAccessibleNameForANewTab() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceLink(
                        href = "https://example.com/docs",
                        contentDescription = "Read the docs (opens in a new tab)",
                    ) {
                        TraceLine {
                            TraceIcon { Icon(Check) }
                            TraceTitle("Read the docs")
                            TraceIcon { Icon(ChevronRight) }
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithContentDescription("Read the docs (opens in a new tab)")
            .assertIsDisplayed()
    }
}
