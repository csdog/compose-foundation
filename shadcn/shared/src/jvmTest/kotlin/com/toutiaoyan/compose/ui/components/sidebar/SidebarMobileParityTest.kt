@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class SidebarMobileParityTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private lateinit var state: SidebarState

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }

    private fun show(
        overrideWidth: Boolean,
        forceMobile: Boolean? = true,
    ) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(width, 768)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    SidebarProvider(mobile = forceMobile, defaultOpenMobile = true) {
                        val current = sidebarState()
                        SideEffect { state = current }
                        Sidebar(
                            Modifier.testTag("panel"),
                            style = if (overrideWidth) Style { width(320.dp) } else Style,
                        ) { Box(Modifier.size(20.dp).testTag("first-child")) }
                        SidebarRail(Modifier.testTag("edge-toggle"))
                    }
                }
            }
        }
    }

    private fun panel() = rule.onNodeWithTag("panel", useUnmergedTree = true)

    private fun capture(name: String) {
        val file = File("build/reports/sidebar-mobile-r1/$name-$width-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(panel().captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultMobileWidthZeroRootPaddingAndOutsideClosesOnlyMobile() {
        show(false)
        panel().assertWidthIsEqualTo(288.dp).assertHeightIsEqualTo(768.dp)
        panel().assert(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Sidebar"))
        val outer = panel().getUnclippedBoundsInRoot()
        val child = rule.onNodeWithTag("first-child").getUnclippedBoundsInRoot()
        assertEquals(outer.left, child.left)
        assertEquals(outer.top, child.top)
        capture("default")
        val roots = rule.onAllNodes(isRoot())
        roots[roots.fetchSemanticsNodes().lastIndex].performMouseInput { click(Offset(1000f, 600f)) }
        panel().assertDoesNotExist()
        rule.runOnIdle {
            assertFalse(state.openMobile)
            assertTrue(state.open)
        }
    }

    @Test fun automaticMobileInheritsResponsiveSheetWidthAndHidesTheEdgeToggle() {
        show(false, forceMobile = null)
        if (width == 390) {
            panel().assertWidthIsEqualTo(293.dp)
            rule.onNodeWithTag("edge-toggle").assertDoesNotExist()
        } else {
            panel().assertWidthIsEqualTo(256.dp)
            rule.onNodeWithTag("edge-toggle").assertExists()
        }
    }

    @Test fun callerStyleCanOverrideDefaultMobileWidth() {
        show(true)
        panel().assertWidthIsEqualTo(320.dp)
        capture("override")
    }
}
