@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dialog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DialogFooterTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun compactActionsFitUnderUnboundedHorizontalConstraints() {
        val window =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerSize = IntSize(390, 844)
            }
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalWindowInfo provides window) {
                    Dialog(open = true) {
                        Column(Modifier.width(340.dp).horizontalScroll(rememberScrollState())) {
                            DialogFooter {
                                Button("Cancel", variant = ButtonVariant.Outline)
                                Button("Save longer changes")
                            }
                        }
                    }
                }
            }
        }
        val save = rule.onNodeWithText("Save longer changes").assertIsDisplayed().getUnclippedBoundsInRoot()
        val cancel = rule.onNodeWithText("Cancel").assertIsDisplayed().getUnclippedBoundsInRoot()
        val saveWidth = save.right - save.left
        assertEquals(saveWidth, cancel.right - cancel.left)
        assertTrue(saveWidth > 0.dp)
        assertEquals(8f, (cancel.top - save.bottom).value, 0.5f)
    }

    @Test fun phoneActionsStackInReverseAndDesktopActionsKeepLogicalOrder() {
        var viewport by mutableStateOf(IntSize(390, 844))
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var dark by mutableStateOf(false)
        var closeRequests = 0
        val window =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerSize get() = viewport
            }
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                CompositionLocalProvider(LocalWindowInfo provides window, LocalLayoutDirection provides direction) {
                    Dialog(open = true, onOpenChange = { if (!it) closeRequests++ }) {
                        Column(Modifier.width(352.dp).padding(16.dp)) {
                            DialogFooter(showCloseButton = true) {
                                Button("Cancel", variant = ButtonVariant.Outline)
                                Button("Save changes")
                            }
                        }
                    }
                }
            }
        }

        fun bounds(label: String) = rule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
        for (layoutDirection in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
            rule.runOnIdle { direction = layoutDirection }
            for (night in listOf(false, true)) {
                rule.runOnIdle { dark = night }
                assertEquals(320f, bounds("Save changes").width, .5f)
                assertEquals(8f, bounds("Save changes").top - bounds("Close").bottom, .5f)
                assertEquals(8f, bounds("Cancel").top - bounds("Save changes").bottom, .5f)
                val pixels = rule.onNodeWithText("Save changes").captureToImage().toPixelMap()
                val edge = pixels[4, pixels.height / 2]
                assertTrue(if (night) edge.red > .7f else edge.red < .3f, "Primary chrome must fill the action width")
            }
            rule.runOnIdle { viewport = IntSize(640, 844) }
            assertEquals(bounds("Cancel").top, bounds("Save changes").top, .5f)
            assertEquals(bounds("Save changes").top, bounds("Close").top, .5f)
            if (layoutDirection == LayoutDirection.Ltr) {
                assertTrue(bounds("Cancel").right < bounds("Save changes").left)
            } else {
                assertTrue(bounds("Save changes").right < bounds("Cancel").left)
            }
            rule.runOnIdle { viewport = IntSize(390, 844) }
        }
        rule.onNodeWithText("Close").performClick()
        assertEquals(1, closeRequests)
    }
}
