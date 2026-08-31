@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.breadcrumb

import com.toutiaoyan.compose.ui.ScreenshotTest
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
}
