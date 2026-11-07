@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.breadcrumb

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class BreadcrumbParityTest : ScreenshotTest() {
    @Test fun listUsesSixPixelGapsAndWrapsAtomicItems() {
        var narrow by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Breadcrumb(Modifier.width(if (narrow) 100.dp else 400.dp)) {
                BreadcrumbList {
                    BreadcrumbItem(Modifier.testTag("a")) { Box(Modifier.size(70.dp, 20.dp)) }
                    BreadcrumbItem(Modifier.testTag("b")) { Box(Modifier.size(70.dp, 20.dp)) }
                }
            }
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        assertEquals(6f, bounds("b").left - bounds("a").right, .1f)
        assertEquals(bounds("a").top, bounds("b").top, .1f)
        composeRule.runOnIdle { narrow = true }
        assertEquals(6f, bounds("b").top - bounds("a").bottom, .1f)
        assertEquals(bounds("a").left, bounds("b").left, .1f)
    }

    @Test fun rtlReversesOrderAndKeepsFourPixelItemGap() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Breadcrumb {
                    BreadcrumbList {
                        BreadcrumbItem(Modifier.testTag("item")) {
                            Box(Modifier.size(20.dp).testTag("icon"))
                            Box(Modifier.size(30.dp, 20.dp).testTag("label"))
                        }
                        BreadcrumbSeparator(Modifier.testTag("divider"))
                    }
                }
            }
        }
        val icon = composeRule.onNodeWithTag("icon").fetchSemanticsNode().boundsInRoot
        val label = composeRule.onNodeWithTag("label").fetchSemanticsNode().boundsInRoot
        val divider = composeRule.onNodeWithTag("divider").fetchSemanticsNode().boundsInRoot
        assertEquals(4f, icon.left - label.right, .1f)
        assertEquals(14f, divider.width, .1f)
        assertTrue(divider.right < label.left)
    }

    @Test fun currentPageHasAccessibleNameAndDecorationsHaveNoSpokenLabels() {
        var clicked = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Breadcrumb(ariaLabel = "Page path") {
                BreadcrumbList {
                    BreadcrumbItem("Home", onClick = { clicked++ }, disabled = true)
                    BreadcrumbSeparator { Text { "hidden separator" } }
                    BreadcrumbItem { BreadcrumbEllipsis() }
                    BreadcrumbSeparator()
                    BreadcrumbItem("Current")
                }
            }
        }
        composeRule.onNodeWithContentDescription("Page path").assertExists()
        composeRule.onNodeWithText("Home").assertIsNotEnabled().performClick()
        assertEquals(0, clicked)
        composeRule.onNodeWithContentDescription("Current, current page").assertHasNoClickAction()
        composeRule.onNodeWithText("hidden separator").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("More").assertDoesNotExist()
    }

    @Test fun linksFollowTabOrderAndActivateWithKeyboard() {
        var homeClicks = 0
        var componentClicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem("Home", onClick = { homeClicks++ })
                    BreadcrumbSeparator()
                    BreadcrumbItem("Components", onClick = { componentClicks++ })
                    BreadcrumbSeparator()
                    BreadcrumbItem("Current")
                }
            }
        }

        composeRule.onRoot().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithText("Home").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        assertEquals(1, homeClicks)
        composeRule.onNodeWithText("Home").performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithText("Components").assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        assertEquals(1, componentClicks)
        composeRule.onNodeWithText("Components").performKeyInput { pressKey(Key.Tab) }
        composeRule.waitForIdle()
        // Desktop semantics still mark this accessible BasicText node as "focused"
        // after the last link, despite it having no click/selection/focus target.
        composeRule.onNodeWithText("Components").assertIsNotFocused()
        composeRule.onNodeWithContentDescription("Current, current page")
            .assertHasNoClickAction()
    }

    @Test fun linkHoverChangesActualInkInLightAndDark() = hover(false)

    @Test fun linkHoverChangesActualInkInDark() = hover(true)

    private fun hover(dark: Boolean) {
        var clicked = 0
        setThemedContent(stylePreset = StylePreset.Nova, darkTheme = dark) {
            BreadcrumbLink("Home", { clicked++ }, Modifier.testTag("link"))
        }
        val link = composeRule.onNodeWithTag("link")

        fun ink(): Float {
            val p = link.captureToImage().toPixelMap()
            return (0 until p.height).flatMap {
                    y ->
                (0 until p.width).map { x -> p[x, y].red }
            }.let { if (dark)it.max() else it.min() }
        }
        val rest = ink()
        val bounds = link.fetchSemanticsNode().boundsInRoot
        link.performMouseInput {
            enter(center)
            moveTo(center)
        }
        composeRule.mainClock.advanceTimeBy(200)
        val hovered = ink()
        assertTrue(if (dark) hovered > rest + .15f else hovered < rest - .15f, "rest=$rest hover=$hovered")
        link.performClick()
        assertEquals(1, clicked)
        assertEquals(bounds, link.fetchSemanticsNode().boundsInRoot)
    }
}
