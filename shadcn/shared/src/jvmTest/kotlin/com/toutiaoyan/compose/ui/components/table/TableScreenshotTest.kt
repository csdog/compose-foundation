@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class TableScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("table_states", darkTheme = darkTheme) {
            Table {
                TableHeaderRow {
                    TableHeadCell("Invoice", Modifier.weight(1f))
                    TableHeadCell("Status")
                }
                TableRow {
                    TableCell("INV001", Modifier.weight(1f))
                    TableCell("Paid", muted = true)
                }
                TableRow(isLast = true) {
                    TableCell("INV002", Modifier.weight(1f))
                    TableCell("Pending", muted = true)
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
