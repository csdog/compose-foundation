@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.carousel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.launch
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.tailwind.core.twSpacing

/** Pager state of the nearest [Carousel]. Matches shadcn's `useCarousel()`. */
val LocalCarouselState = staticCompositionLocalOf<PagerState?> { null }

@Composable
private fun carouselState(state: PagerState?): PagerState =
    state
        ?: LocalCarouselState.current
        ?: error("CarouselPrevious / CarouselNext / CarouselDots need state= or a parent Carousel")

/** Reads the nearest [Carousel] pager state. Matches shadcn's `useCarousel()`. */
@Composable
fun carouselApi(): PagerState = carouselState(null)

/**
 * A snap-scrolling, swipeable item carousel. Real shadcn/ui's `carousel.tsx` wraps
 * `embla-carousel-react`; Compose Multiplatform's own [HorizontalPager]/[VerticalPager]
 * already provide the same snap-per-item scrolling behavior natively, so this wraps
 * those directly rather than reimplementing snap physics.
 *
 * [modifier] must constrain the main-axis size (`width` for [Orientation.Horizontal],
 * `height` for [Orientation.Vertical]) -- this composable fills whatever it's given
 * ([HorizontalPager]/[VerticalPager] need a bounded main-axis size to know each page's
 * own size), so an unconstrained caller silently gets a carousel that expands to the
 * full ambient width/height instead of a sensible card-like size. Focused ArrowLeft /
 * ArrowRight go to the previous/next page, matching shadcn's root `onKeyDown`.
 *
 * Usage:
 * ```
 * val state = rememberPagerState { items.size }
 * Carousel(state = state, modifier = Modifier.width(280.dp).height(160.dp)) {
 *     page -> Text { items[page] }
 * }
 * ```
 */
@Composable
fun Carousel(
    state: PagerState,
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: Orientation = Orientation.Horizontal,
    ariaLabel: String? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val scope = rememberCoroutineScope()
    val styledModifier =
        modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionLeft -> {
                        scope.launch {
                            if (state.currentPage > 0) {
                                state.animateScrollToPage(state.currentPage - 1)
                            }
                        }
                        true
                    }
                    Key.DirectionRight -> {
                        scope.launch {
                            if (state.currentPage < state.pageCount - 1) {
                                state.animateScrollToPage(state.currentPage + 1)
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            .semantics {
                role = Role.Carousel
                if (ariaLabel != null) {
                    contentDescription = ariaLabel
                }
            }
            .styleable(styleState, style)
    CompositionLocalProvider(LocalCarouselState provides state) {
        if (orientation == Orientation.Horizontal) {
            HorizontalPager(state = state, modifier = styledModifier) { page -> pageContent(page) }
        } else {
            VerticalPager(state = state, modifier = styledModifier) { page -> pageContent(page) }
        }
    }
}

/**
 * Alias for [Carousel] matching shadcn's `CarouselContent` (the snap-scrolling viewport).
 * Prefer this name when pairing with [CarouselItem] / [CarouselPrevious] / [CarouselNext].
 */
@Composable
fun CarouselContent(
    state: PagerState,
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: Orientation = Orientation.Horizontal,
    ariaLabel: String? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    Carousel(
        state = state,
        modifier = modifier,
        style = style,
        orientation = orientation,
        ariaLabel = ariaLabel,
        pageContent = pageContent,
    )
}

/** One slide inside [CarouselContent]. Matches shadcn's `CarouselItem`. */
@Composable
fun CarouselItem(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** String-body overload of [CarouselItem]. */
@Composable
fun CarouselItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    CarouselItem(modifier) {
        Text { text }
    }
}

/** A circular Previous button that scrolls a [Carousel]'s [state] back one page. */
@Composable
fun CarouselPrevious(
    state: PagerState? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    // A self-generated ImageVector (ChevronRight rotated 180deg), not a third-party
    // icon-set dependency (this library still takes none -- see README) -- a plain text
    // glyph doesn't render on WasmJS (Skia has no browser emoji-font fallback). tint
    // matches ButtonVariant.Outline's own contentColor (colors.foreground) since a plain
    // Image never auto-inherits the Style API's ambient contentColor the way Text
    // did. Override with any icon set (e.g. this repo's own demo app passes a real
    // Lucide ChevronLeft here -- see CarouselDoc.kt).
    icon: @Composable () -> Unit = {
        Icon(ChevronRight, tint = currentTheme.colors.foreground, modifier = Modifier.rotate(180f))
    },
    disabled: Boolean = false,
) {
    val resolved = carouselState(state)
    val scope = rememberCoroutineScope()
    Button(
        onClick = { scope.launch { resolved.animateScrollToPage((resolved.currentPage - 1).coerceAtLeast(0)) } },
        modifier = modifier,
        enabled = resolved.currentPage > 0 && !disabled,
        variant = ButtonVariant.Outline,
        size = ButtonSize.Icon,
        label = "Previous slide",
        // Real shadcn's CarouselPrevious/CarouselNext are `rounded-full` (not the
        // standard button corner radius) AND smaller than the default icon-button size
        // (`size-7`/`size-8` vs. ButtonSize.Icon's 36dp). width/height here -- not a
        // separate `Modifier.size(32.dp)` on the button -- is the single source of
        // truth for the button's size: this `style` param is applied last (see
        // Button's `variant.rememberStyle() then size.rememberStyle(), style`
        // chain, where a later style overrides a property an earlier one already set),
        // so it cleanly overrides ButtonSize.Icon's 36dp rather than fighting an outer
        // Modifier.size() for the final rendered size -- a real bug this component
        // shipped with: the two competing sizing mechanisms let the button render at
        // 36dp despite the (ignored) 32dp Modifier.size(), overflowing past its
        // allocated slot and overlapping the carousel content it's offset right up
        // against (see CarouselDoc.kt's `.offset(x = (-40).dp)`/`.offset(x = 40.dp)`).
        style =
            Style {
                shape(CircleShape)
                width(32.dp)
                height(32.dp)
            } then style,
    ) {
        icon()
    }
}

/** A circular Next button that scrolls a [Carousel]'s [state] forward one page. */
@Composable
fun CarouselNext(
    state: PagerState? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    // A self-generated ImageVector, not a third-party icon-set dependency (this library
    // still takes none -- see README) -- a plain text glyph doesn't render on WasmJS
    // (Skia has no browser emoji-font fallback). tint matches ButtonVariant.Outline's own
    // contentColor (colors.foreground) since a plain Image never auto-inherits the Style
    // API's ambient contentColor the way Text did. Override with any icon set (e.g.
    // this repo's own demo app passes a real Lucide ChevronRight here -- see
    // CarouselDoc.kt).
    icon: @Composable () -> Unit = { Icon(ChevronRight, tint = currentTheme.colors.foreground) },
    disabled: Boolean = false,
) {
    val resolved = carouselState(state)
    val scope = rememberCoroutineScope()
    val canScrollNext = resolved.currentPage < resolved.pageCount - 1
    Button(
        onClick = {
            scope.launch {
                resolved.animateScrollToPage(
                    (resolved.currentPage + 1).coerceAtMost(resolved.pageCount - 1),
                )
            }
        },
        modifier = modifier,
        enabled = canScrollNext && !disabled,
        variant = ButtonVariant.Outline,
        size = ButtonSize.Icon,
        label = "Next slide",
        // See CarouselPrevious's matching comment -- width/height here (not a
        // separate Modifier.size(32.dp)) is the single source of truth for size.
        style =
            Style {
                shape(CircleShape)
                width(32.dp)
                height(32.dp)
            } then style,
    ) {
        icon()
    }
}

/** A row of small dots indicating the current page in a [Carousel]. */
@Composable
fun CarouselDots(
    state: PagerState? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val resolved = carouselState(state)
    val scope = rememberCoroutineScope()
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
    ) {
        repeat(resolved.pageCount) { index ->
            val interactionSource = remember { MutableInteractionSource() }
            val isActive = index == resolved.currentPage
            val styleState = remember { MutableStyleState(interactionSource) }
            Box(
                modifier =
                    Modifier
                        .semantics {
                            role = Role.Button
                            contentDescription = "Page ${index + 1}"
                            selected = isActive
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                        ) {
                            scope.launch { resolved.animateScrollToPage(index) }
                        }
                        .styleable(styleState, touchTargetStyle then pressStyle),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier.styleable(styleState, rememberCarouselDotStyle(isActive)),
                )
            }
        }
    }
}
