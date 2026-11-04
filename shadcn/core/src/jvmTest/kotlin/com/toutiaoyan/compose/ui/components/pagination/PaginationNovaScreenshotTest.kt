@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.pagination

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class PaginationNovaScreenshotTest : ScreenshotTest() {
    private fun nova(
        dark: Boolean,
        rtl: Boolean,
    ) = snapshot(
        if (rtl) "pagination_nova_rtl" else "pagination_nova_default",
        darkTheme = dark,
        stylePreset = StylePreset.Nova,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Box(Modifier.width(440.dp)) {
                Pagination {
                    PaginationContent {
                        PaginationItem { PaginationPrevious(onClick = {}, text = if (rtl) "السابق" else "Previous") }
                        (1..3).forEach { page ->
                            PaginationItem {
                                PaginationLink(
                                    page,
                                    isActive =
                                        page == 2,
                                    onClick =
                                        {},
                                ) {
                                    Text {
                                        if (rtl) {
                                            "١٢٣"[page - 1].toString()
                                        } else {
                                            page.toString()
                                        }
                                    }
                                }
                            }
                        }
                        PaginationItem { PaginationEllipsis() }
                        PaginationItem { PaginationNext(onClick = {}, text = if (rtl) "التالي" else "Next") }
                    }
                }
            }
        }
    }

    @Test fun default_light() = nova(false, false)

    @Test fun default_dark() = nova(true, false)

    @Test fun rtl_light() = nova(false, true)

    @Test fun rtl_dark() = nova(true, true)
}
