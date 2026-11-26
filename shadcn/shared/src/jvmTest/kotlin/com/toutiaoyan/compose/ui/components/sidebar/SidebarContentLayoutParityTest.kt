@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class SidebarContentLayoutParityTest(private val mobile: Boolean, private val dark: Boolean) : ScreenshotTest() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "mobile={0},dark={1}")
        fun parameters() =
            listOf(
                arrayOf(false, false),
                arrayOf(false, true),
                arrayOf(true, false),
                arrayOf(true, true),
            )
    }

    private fun show(long: Boolean) {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            SidebarProvider(mobile = mobile, defaultOpenMobile = mobile, modifier = Modifier.height(400.dp)) {
                Sidebar(Modifier.testTag("panel")) {
                    SidebarHeader(Modifier.testTag("header")) {
                        Box(Modifier.size(20.dp).testTag("header-first").background(currentTheme.colors.primary))
                        Box(Modifier.size(20.dp).testTag("header-second").background(currentTheme.colors.primary))
                    }
                    SidebarContent(Modifier.testTag("content")) {
                        repeat(if (long) 30 else 2) { index ->
                            Text(modifier = Modifier.height(32.dp).testTag("item-$index")) { "Navigation $index" }
                        }
                    }
                    SidebarFooter(Modifier.testTag("footer")) {
                        Box(Modifier.size(20.dp).testTag("footer-item").background(currentTheme.colors.primary))
                    }
                }
            }
        }
    }

    private fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

    private fun capture(name: String) {
        val file = File("build/reports/sidebar-layout-r1/$name-mobile-$mobile-dark-$dark.png")
        file.parentFile.mkdirs()
        ImageIO.write(composeRule.onNodeWithTag("panel").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun paddingBelongsToHeaderAndFooterWhileContentHasNoGapOrPadding() {
        show(false)
        val panel = bounds("panel")
        val header = bounds("header")
        val content = bounds("content")
        val footer = bounds("footer")
        assertEquals(panel.top, header.top, .1f)
        assertEquals(64f, header.height, .1f)
        assertEquals(panel.left + 8f, bounds("header-first").left, .1f)
        assertEquals(header.top + 8f, bounds("header-first").top, .1f)
        assertEquals(bounds("header-first").bottom + 8f, bounds("header-second").top, .1f)
        assertEquals(header.bottom, content.top, .1f)
        assertEquals(panel.left, bounds("item-0").left, .1f)
        assertEquals(content.top, bounds("item-0").top, .1f)
        assertEquals(bounds("item-0").bottom, bounds("item-1").top, .1f)
        assertEquals(content.bottom, footer.top, .1f)
        assertEquals(panel.bottom, footer.bottom, .1f)
        assertEquals(36f, footer.height, .1f)
        assertEquals(panel.bottom - 8f, bounds("footer-item").bottom, .1f)
        capture("short")
    }

    @Test fun longNavigationScrollsWhileHeaderAndFooterStayInPlace() {
        show(true)
        val header = bounds("header")
        val footer = bounds("footer")
        val range =
            composeRule.onNodeWithTag(
                "content",
            ).fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange]
        assertTrue(range.maxValue() > 0f)
        composeRule.onNodeWithTag("item-29").assertIsNotDisplayed()
        composeRule.onNodeWithTag("content").performMouseInput {
            moveTo(center)
            scroll(4f)
        }
        composeRule.waitForIdle()
        assertTrue(range.value() > 0f, "A real mouse wheel event must move the body")
        assertEquals(header, bounds("header"))
        assertEquals(footer, bounds("footer"))
        composeRule.onNodeWithTag("item-29").performScrollTo().assertIsDisplayed()
        assertEquals(header, bounds("header"))
        assertEquals(footer, bounds("footer"))
        assertTrue(range.value() > 0f)
        capture("scrolled")
    }
}
