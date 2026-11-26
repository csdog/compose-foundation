@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.test.Test

/**
 * Only the idle (no menu open) state -- the open-panel appearance reuses the exact
 * same [com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem]/Popup rendering already proven correct by
 * DropdownMenuScreenshotTest; the one genuinely new piece ([com.toutiaoyan.compose.ui.components.overlay.PointPositionProvider])
 * has its own direct math tests in `PopupPositionProviderTest`. Right-click gesture
 * simulation wasn't attempted here after `performMouseInput { moveTo(...) }` hung the
 * test worker in TooltipScreenshotTest -- not worth the same risk for a state that's
 * already covered another way.
 */
class ContextMenuScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("context_menu_idle", darkTheme = darkTheme) {
            ContextMenu {
                ContextMenuTrigger {
                    Box(
                        modifier =
                            Modifier
                                .size(width = 240.dp, height = 80.dp)
                                .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(textStyle = TextColors.Muted) { "Right-click here" }
                    }
                }
                ContextMenuContent {
                    ContextMenuItem("Back", onClick = {})
                }
            }
        }
    }

    @Test
    fun idle_light() = states(darkTheme = false)

    @Test
    fun idle_dark() = states(darkTheme = true)
}
