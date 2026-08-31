@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.pagination

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class PaginationScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("pagination_states", darkTheme = darkTheme) {
            Pagination {
                PaginationContent {
                    PaginationPrevious(enabled = true, onClick = {})
                    PaginationLink(page = 1, isActive = false, onClick = {})
                    PaginationLink(page = 2, isActive = true, onClick = {})
                    PaginationLink(page = 3, isActive = false, onClick = {})
                    PaginationEllipsis()
                    PaginationLink(page = 10, isActive = false, onClick = {})
                    PaginationNext(enabled = true, onClick = {})
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
