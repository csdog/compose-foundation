@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.carousel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.layout.aspectRatio
import com.toutiaoyan.compose.ui.components.text.Text

class CarouselScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("carousel_states", darkTheme = darkTheme) {
            val state = rememberPagerState { 3 }
            // Fixed width: HorizontalPager needs a bounded main-axis size for its own page
            // width -- without one it silently expands to whatever ambient width it's given.
            Column(modifier = Modifier.width(200.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Carousel(state = state, modifier = Modifier.fillMaxWidth().height(100.dp)) { page ->
                    Box(Modifier, contentAlignment = Alignment.Center) { Text { "Slide ${page + 1}" } }
                }
                // Previous/Next must render circular (real shadcn: rounded-full) -- covers
                // the shape fix, never previously exercised by this golden.
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CarouselPrevious(state = state)
                    CarouselNext(state = state)
                }
                CarouselDots(state = state)
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    /**
     * Regression guard for a real bug: [CarouselPrevious]/[CarouselNext] used
     * to size themselves via two competing mechanisms -- an outer `Modifier.size(32.dp)`
     * on the button plus `ButtonSize.Icon`'s own Style-level `width(36.dp)/height(36.dp)`
     * -- and the Style-level size won, rendering the button at 36dp despite the (ignored)
     * 32dp modifier. Positioned the way the real doc example does (`.offset(x = -40.dp)`
     * right against a 200dp carousel's edge, matching CarouselDoc.kt exactly), that 4dp
     * overflow bled the Previous button into the carousel card underneath it.
     */
    private fun overlapRegression(darkTheme: Boolean) {
        snapshot("carousel_overlap_regression", darkTheme = darkTheme) {
            val state = rememberPagerState { 3 }
            Box(modifier = Modifier.width(200.dp).height(200.dp), contentAlignment = Alignment.Center) {
                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                    Card(modifier = Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text { "${page + 1}" }
                        }
                    }
                }
                CarouselPrevious(
                    state = state,
                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                )
                CarouselNext(
                    state = state,
                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 40.dp),
                )
            }
        }
    }

    @Test
    fun overlap_regression_light() = overlapRegression(darkTheme = false)

    /**
     * Isolated size check, no neighbors (no dots, no Next button, no card) to avoid any
     * ambiguity about which circle in the golden is which -- proves
     * [CarouselPrevious] renders at its intended 32dp, not the pre-fix 36dp.
     */
    @Test
    fun previous_isolated_size_light() {
        snapshot("carousel_previous_isolated_size", darkTheme = false) {
            val state = rememberPagerState { 3 }
            CarouselPrevious(state = state)
        }
    }

    private fun nova(
        kind: String,
        dark: Boolean,
    ) {
        snapshot("carousel_nova_$kind", darkTheme = dark, stylePreset = StylePreset.Nova) {
            val vertical = kind == "vertical"
            val partial = kind == "sizes"
            val width = if (partial) 384.dp else 320.dp
            val page = if (partial) 117.333.dp else width
            val height = if (vertical) 266.dp else page + 32.dp
            val state = rememberPagerState { 5 }
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (kind == "rtl") {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                Box(Modifier.padding(horizontal = 56.dp, vertical = if (vertical) 56.dp else 0.dp)) {
                    CarouselRoot(
                        state,
                        Modifier.width(width).height(height),
                        orientation =
                            if (vertical) {
                                Orientation.Vertical
                            } else {
                                Orientation.Horizontal
                            },
                    ) {
                        CarouselContent(
                            pageSize =
                                PageSize.Fixed(
                                    if (vertical) {
                                        131.dp
                                    } else {
                                        page
                                    },
                                ),
                            pageSpacing =
                                if (vertical) {
                                    4.dp
                                } else {
                                    16.dp
                                },
                        ) { index ->
                            Box(Modifier.fillMaxSize()) {
                                Card(
                                    Modifier.fillMaxWidth().padding(4.dp),
                                    style = Style { contentPadding(horizontal = 0.dp, vertical = 16.dp) },
                                ) {
                                    CardContent(
                                        modifier =
                                            if (vertical) {
                                                Modifier
                                            } else {
                                                Modifier.aspectRatio(1f)
                                            },
                                        style =
                                            Style { contentPadding(24.dp) },
                                    ) {
                                        Box(
                                            if (vertical) {
                                                Modifier.fillMaxWidth()
                                            } else {
                                                Modifier.fillMaxSize()
                                            },
                                            contentAlignment =
                                                Alignment.Center,
                                        ) {
                                            Text(
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            if (partial ||
                                                                vertical
                                                            ) {
                                                                30.sp
                                                            } else {
                                                                36.sp
                                                            },
                                                        lineHeight =
                                                            if (partial ||
                                                                vertical
                                                            ) {
                                                                36.sp
                                                            } else {
                                                                40.sp
                                                            },
                                                        fontWeight =
                                                            FontWeight.SemiBold,
                                                    ),
                                            ) {
                                                if (kind == "rtl") "١" else (index + 1).toString()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        CarouselPrevious(
                            modifier =
                                if (vertical) {
                                    Modifier.align(Alignment.TopCenter).offset(
                                        y =
                                            (-48).dp,
                                    )
                                } else {
                                    Modifier.align(Alignment.CenterStart).offset(
                                        x =
                                            (-48).dp,
                                    )
                                },
                        )
                        CarouselNext(
                            modifier =
                                if (vertical) {
                                    Modifier.align(Alignment.BottomCenter).offset(
                                        y =
                                            48.dp,
                                    )
                                } else {
                                    Modifier.align(Alignment.CenterEnd).offset(
                                        x =
                                            48.dp,
                                    )
                                },
                        )
                    }
                }
            }
        }
    }

    @Test fun nova_default_light() = nova("default", false)

    @Test fun nova_default_dark() = nova("default", true)

    @Test fun nova_sizes_light() = nova("sizes", false)

    @Test fun nova_sizes_dark() = nova("sizes", true)

    @Test fun nova_vertical_light() = nova("vertical", false)

    @Test fun nova_vertical_dark() = nova("vertical", true)

    @Test fun nova_rtl_light() = nova("rtl", false)

    @Test fun nova_rtl_dark() = nova("rtl", true)
}
