@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.carousel.Carousel
import com.toutiaoyan.compose.ui.components.carousel.CarouselContent
import com.toutiaoyan.compose.ui.components.carousel.CarouselDots
import com.toutiaoyan.compose.ui.components.carousel.CarouselItem
import com.toutiaoyan.compose.ui.components.carousel.CarouselNext
import com.toutiaoyan.compose.ui.components.carousel.CarouselPrevious
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

val carouselDoc =
    ComponentDoc(
        id = "carousel",
        title = "Carousel",
        description = "A swipeable, snap-scrolling set of slides, built directly on Compose's own Pager.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.carousel.CarouselContent
            import com.toutiaoyan.compose.ui.components.carousel.CarouselItem
            import androidx.compose.foundation.pager.rememberPagerState

            val state = rememberPagerState { 5 }
            CarouselContent(state = state) { page ->
                CarouselItem("Slide ${'$'}page")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        // Matches real shadcn's own default demo: a Card per slide, roughly
                        // square (real shadcn: `aspect-square`), a big bold slide number, and
                        // circular Previous/Next buttons overlaid on the carousel's edges
                        // (real shadcn: `absolute -left-12`/`-right-12`) rather than a row
                        // below it.
                        val state = rememberPagerState { 5 }
                        // The buttons overlay outside the carousel's own bounds by design (40dp
                        // offset + half the 32dp button width = 56dp clearance needed on each
                        // side) -- this margin gives them room so they don't get clipped by
                        // whatever container this demo is placed in.
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
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
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
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
                    },
                ),
                ComponentExample(
                    title = "With dot indicators",
                    code =
                        """
                        // CarouselDots has no real shadcn/ui equivalent -- real
                        // carousel.tsx ships only Previous/Next, no pagination dots. This is
                        // a deliberate addition, not a parity gap: a common carousel pattern
                        // worth having, shown separately so the "Default" example above stays
                        // a faithful match to the real demo.
                        val state = rememberPagerState { 5 }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Carousel(state = state, modifier = Modifier.width(280.dp).height(120.dp)) { page ->
                                Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(typography = TextTypography.TitleLarge) { "Slide ${'$'}{page + 1}" }
                                }
                            }
                            CarouselDots(state = state, modifier = Modifier.padding(top = 8.dp))
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Carousel(
                                state = state,
                                modifier = Modifier.width(280.dp).height(120.dp),
                            ) { page ->
                                Box(
                                    Modifier
                                        .padding(8.dp)
                                        .background(
                                            currentTheme.colors.muted,
                                            RoundedCornerShape(twRadius.md),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(variant = TextVariant.TitleLarge) { "Slide ${page + 1}" }
                                }
                            }
                            CarouselDots(
                                state = state,
                                modifier = Modifier.padding(top = twSpacing.sm),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom icons",
                    code =
                        """
                        // CarouselPrevious/Next's arrows are plain glyphs by default
                        // (this library takes no icon-library dependency) -- override each
                        // icon slot with a real vector from any icon set, e.g. Lucide.
                        val state = rememberPagerState { 5 }
                        // See the "Default" example above for why this outer margin exists.
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                                    icon = { DocIcon(Lucide.ChevronLeft) },
                                )
                                CarouselNext(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 40.dp),
                                    icon = { DocIcon(Lucide.ChevronRight) },
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                                    icon = { DocIcon(Lucide.ChevronLeft) },
                                )
                                CarouselNext(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 40.dp),
                                    icon = { DocIcon(Lucide.ChevronRight) },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(vertical = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    orientation = Orientation.Vertical,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-40).dp),
                                )
                                CarouselNext(
                                    state = state,
                                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 40.dp),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(vertical = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    orientation = Orientation.Vertical,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-40).dp),
                                )
                                CarouselNext(
                                    state = state,
                                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 40.dp),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    orientation = Orientation.Horizontal,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
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
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    orientation = Orientation.Horizontal,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
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
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    orientation = Orientation.Horizontal,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
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
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    orientation = Orientation.Horizontal,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
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
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    modifier = Modifier.fillMaxSize(),
                                    ariaLabel = "Gallery",
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    modifier = Modifier.fillMaxSize(),
                                    ariaLabel = "Gallery",
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    modifier = Modifier.fillMaxSize(),
                                    ariaLabel = null,
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(
                                    state = state,
                                    modifier = Modifier.fillMaxSize(),
                                    ariaLabel = null,
                                ) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Previous",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    disabled = false,
                                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                                CarouselPrevious(
                                    state = state,
                                    disabled = false,
                                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-40).dp),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Next",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(typography = TextTypography.DisplayMedium) { "${'$'}{page + 1}" }
                                        }
                                    }
                                }
                                CarouselNext(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 40.dp),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                            Box(
                                modifier = Modifier.width(200.dp).height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Carousel(state = state, modifier = Modifier.fillMaxSize()) { page ->
                                    Card(modifier = Modifier.fillMaxSize()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(variant = TextVariant.DisplayMedium) { "${page + 1}" }
                                        }
                                    }
                                }
                                CarouselNext(
                                    state = state,
                                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 40.dp),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dots",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Carousel(state = state, modifier = Modifier.width(280.dp).height(120.dp)) { page ->
                                Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(typography = TextTypography.TitleLarge) { "Slide ${'$'}{page + 1}" }
                                }
                            }
                            CarouselDots(state = state, modifier = Modifier.padding(top = 8.dp))
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Carousel(
                                state = state,
                                modifier = Modifier.width(280.dp).height(120.dp),
                            ) { page ->
                                Box(
                                    Modifier
                                        .padding(8.dp)
                                        .background(
                                            currentTheme.colors.muted,
                                            RoundedCornerShape(twRadius.md),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(variant = TextVariant.TitleLarge) { "Slide ${page + 1}" }
                                }
                            }
                            CarouselDots(state = state, modifier = Modifier.padding(top = 8.dp))
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        val state = rememberPagerState { 5 }
                        CarouselContent(state = state, modifier = Modifier.width(280.dp).height(120.dp)) { page ->
                            CarouselItem {
                                Text(typography = TextTypography.TitleLarge) { "Slide ${'$'}{page + 1}" }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 5 }
                        CarouselContent(
                            state = state,
                            modifier = Modifier.width(280.dp).height(120.dp),
                        ) { page ->
                            CarouselItem {
                                Box(
                                    Modifier
                                        .padding(8.dp)
                                        .background(
                                            currentTheme.colors.muted,
                                            RoundedCornerShape(twRadius.md),
                                        )
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(variant = TextVariant.TitleLarge) { "Slide ${page + 1}" }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        val state = rememberPagerState { 3 }
                        CarouselContent(state = state, modifier = Modifier.width(280.dp).height(120.dp)) { page ->
                            CarouselItem {
                                Text(typography = TextTypography.TitleLarge) { "Slide ${'$'}{page + 1}" }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberPagerState { 3 }
                        CarouselContent(
                            state = state,
                            modifier = Modifier.width(280.dp).height(120.dp),
                        ) { page ->
                            CarouselItem {
                                Box(
                                    Modifier
                                        .padding(8.dp)
                                        .background(
                                            currentTheme.colors.muted,
                                            RoundedCornerShape(twRadius.md),
                                        )
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(variant = TextVariant.TitleLarge) { "Slide ${page + 1}" }
                                }
                            }
                        }
                    },
                ),
            ),
    )
