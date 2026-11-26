@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors

/**
 * Fixed-fraction rendering coverage for legacy examples. Real mouse/touch dragging,
 * keyboard resizing and hit targets are covered separately by ResizableInteractionTest.
 */
class ResizableScreenshotTest : ScreenshotTest() {
    private fun split(
        darkTheme: Boolean,
        fraction: Float,
        suffix: String,
    ) {
        snapshot("resizable_$suffix", darkTheme = darkTheme) {
            ResizablePanelGroup(modifier = Modifier.height(120.dp), initialFraction = fraction) {
                    first, second, onHandleDrag,
                ->
                Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                ResizableHandle(onDrag = onHandleDrag)
                Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
            }
        }
    }

    private fun sidebarAndContent(darkTheme: Boolean) {
        snapshot("resizable_sidebar_and_content", darkTheme = darkTheme) {
            ResizablePanelGroup(
                modifier = Modifier.width(320.dp).height(160.dp),
                initialFraction = 0.35f,
            ) { first, second, onHandleDrag ->
                Box(
                    first.background(currentTheme.colors.muted),
                    contentAlignment = Alignment.Center,
                ) { Text(textStyle = TextColors.Muted) { "Sidebar" } }
                ResizableHandle(onDrag = onHandleDrag)
                Box(second, contentAlignment = Alignment.Center) { Text { "Content" } }
            }
        }
    }

    @Test
    fun states_light() = split(darkTheme = false, fraction = 0.5f, suffix = "states")

    @Test
    fun states_dark() = split(darkTheme = true, fraction = 0.5f, suffix = "states")

    @Test
    fun narrow_first_pane_light() = split(darkTheme = false, fraction = 0.25f, suffix = "narrow_first")

    @Test
    fun wide_first_pane_light() = split(darkTheme = false, fraction = 0.75f, suffix = "wide_first")

    @Test
    fun sidebar_and_content_light() = sidebarAndContent(darkTheme = false)

    @Test
    fun sidebar_and_content_dark() = sidebarAndContent(darkTheme = true)

    private fun withoutHandle(darkTheme: Boolean) {
        snapshot("resizable_without_handle", darkTheme = darkTheme) {
            ResizablePanelGroup(modifier = Modifier.height(120.dp)) { first, second, onHandleDrag ->
                Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                ResizableHandle(onDrag = onHandleDrag, withHandle = ResizableDefaults.WithHandle)
                Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
            }
        }
    }

    @Test
    fun without_handle_light() = withoutHandle(darkTheme = false)

    @Test
    fun without_handle_dark() = withoutHandle(darkTheme = true)
}
