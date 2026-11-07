@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.breadcrumb

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class BreadcrumbScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("breadcrumb_states", darkTheme = darkTheme) {
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem { BreadcrumbLink("Home", onClick = {}) }
                    BreadcrumbSeparator()
                    BreadcrumbItem { BreadcrumbLink("Components", onClick = {}) }
                    BreadcrumbSeparator()
                    BreadcrumbItem { BreadcrumbPage("Breadcrumb") }
                }
            }
        }
    }

    private fun withEllipsis(darkTheme: Boolean) {
        snapshot("breadcrumb_ellipsis", darkTheme = darkTheme) {
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem { BreadcrumbLink("Home", onClick = {}) }
                    BreadcrumbSeparator()
                    BreadcrumbItem { BreadcrumbEllipsis() }
                    BreadcrumbSeparator()
                    BreadcrumbItem { BreadcrumbPage("Settings") }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun with_ellipsis_light() = withEllipsis(darkTheme = false)

    private fun nova(dark: Boolean) =
        snapshot(
            "breadcrumb_nova",
            darkTheme = dark,
            stylePreset = com.toutiaoyan.compose.ui.tokens.StylePreset.Nova,
        ) {
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem("Home", onClick = {})
                    BreadcrumbSeparator()
                    BreadcrumbItem { BreadcrumbEllipsis() }
                    BreadcrumbSeparator()
                    BreadcrumbItem("Components", onClick = {})
                    BreadcrumbSeparator()
                    BreadcrumbItem("Breadcrumb")
                }
            }
        }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)

    private fun focusedLink(darkTheme: Boolean) {
        snapshotFocused(
            "breadcrumb_link_focused",
            focusTag = "breadcrumb-link",
            darkTheme = darkTheme,
            stylePreset = StylePreset.Nova,
        ) {
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem {
                        BreadcrumbLink("Home", onClick = {}, modifier = Modifier.testTag("breadcrumb-link"))
                    }
                    BreadcrumbSeparator()
                    BreadcrumbItem("Current")
                }
            }
        }
    }

    @Test fun link_focused_light() = focusedLink(darkTheme = false)

    @Test fun link_focused_dark() = focusedLink(darkTheme = true)
}
