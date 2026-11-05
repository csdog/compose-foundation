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
import androidx.compose.foundation.pager.PageSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.icons.NovaChevronRight
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
import kotlinx.coroutines.CoroutineScope
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

private val LocalCarouselOrientation = staticCompositionLocalOf { Orientation.Horizontal }
private val LocalCarouselRoot = staticCompositionLocalOf { false }

/** Compound root: supplies state/orientation and captures keys from slides and controls. */
@Composable
fun CarouselRoot(
    state: PagerState,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Horizontal,
    ariaLabel: String? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    CompositionLocalProvider(
        LocalCarouselState provides
            state,
        LocalCarouselOrientation provides
            orientation,
        LocalCarouselRoot provides
            true,
    ) {
        Box(
            modifier.carouselKeys(state, scope).semantics {
                role = Role.Carousel
                if (ariaLabel != null) contentDescription = ariaLabel
            },
            content = content,
        )
    }
}

private fun Modifier.carouselKeys(
    state: PagerState,
    scope: CoroutineScope,
): Modifier =
    onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val delta =
            when (event.key) {
                Key.DirectionLeft -> -1
                Key.DirectionRight -> 1
                else -> return@onPreviewKeyEvent false
            }
        scope.scrollCarousel(state, delta)
        true
    }

private fun CoroutineScope.scrollCarousel(
    state: PagerState,
    delta: Int,
) {
    if (state.pageCount == 0 ||
        (
            delta < 0 &&
                !state.canScrollBackward
        ) ||
        (
            delta > 0 &&
                !state.canScrollForward
        )
    ) {
        return
    }
    val target = (state.targetPage + delta).coerceIn(0, state.pageCount - 1)
    launch { state.animateScrollToPage(target) }
}

@Composable
private fun CarouselChevron(previous: Boolean) {
    val vertical = LocalCarouselOrientation.current == Orientation.Vertical
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val rotation =
        if (vertical) {
            if (previous) -90f else 90f
        } else if (previous != rtl) {
            180f
        } else {
            0f
        }
    Icon(
        if (currentTheme.activePreset == StylePreset.Nova) NovaChevronRight else ChevronRight,
        tint = currentTheme.colors.foreground,
        modifier = Modifier.rotate(rotation),
    )
}

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
    orientation: Orientation = LocalCarouselOrientation.current,
    ariaLabel: String? = null,
    pageSize: PageSize = PageSize.Fill,
    pageSpacing: Dp = 0.dp,
    userScrollEnabled: Boolean = true,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val scope = rememberCoroutineScope()
    val inRoot = LocalCarouselRoot.current
    val styledModifier =
        modifier
            .fillMaxSize()
            .then(if (inRoot) Modifier else Modifier.carouselKeys(state, scope))
            .semantics {
                if (!inRoot) role = Role.Carousel
                if (ariaLabel != null) {
                    contentDescription = ariaLabel
                }
            }
            .styleable(styleState, style)
    CompositionLocalProvider(LocalCarouselState provides state) {
        if (orientation == Orientation.Horizontal) {
            HorizontalPager(
                state =
                state,
                modifier =
                styledModifier,
                pageSize =
                pageSize,
                pageSpacing =
                pageSpacing,
                userScrollEnabled =
                userScrollEnabled,
            ) { page ->
                pageContent(page)
            }
        } else {
            VerticalPager(
                state =
                state,
                modifier =
                styledModifier,
                pageSize =
                pageSize,
                pageSpacing =
                pageSpacing,
                userScrollEnabled =
                userScrollEnabled,
            ) { page ->
                pageContent(page)
            }
        }
    }
}

/**
 * Alias for [Carousel] matching shadcn's `CarouselContent` (the snap-scrolling viewport).
 * Prefer this name when pairing with [CarouselItem] / [CarouselPrevious] / [CarouselNext].
 */
@Composable
fun CarouselContent(
    state: PagerState? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: Orientation = LocalCarouselOrientation.current,
    ariaLabel: String? = null,
    pageSize: PageSize = PageSize.Fill,
    pageSpacing: Dp = 0.dp,
    userScrollEnabled: Boolean = true,
    pageContent: @Composable (page: Int) -> Unit,
) {
    Carousel(
        state = carouselState(state),
        modifier = modifier,
        style = style,
        orientation = orientation,
        ariaLabel = ariaLabel,
        pageSize = pageSize, pageSpacing = pageSpacing, userScrollEnabled = userScrollEnabled,
        pageContent = pageContent,
    )
}

/**
 * One slide inside [CarouselContent]. Matches shadcn's `CarouselItem` slot.
 *
 * Compose has no `aria-roledescription`; pass a slide-specific
 * `contentDescription` through [modifier] when the surrounding content needs a
 * stable screen-reader position such as "Slide 1 of 5".
 */
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
    icon: @Composable () -> Unit = { CarouselChevron(previous = true) },
    disabled: Boolean = false,
) {
    val resolved = carouselState(state)
    val scope = rememberCoroutineScope()
    val buttonSize = if (currentTheme.activePreset == StylePreset.Nova) 28.dp else 32.dp
    Button(
        onClick = { scope.scrollCarousel(resolved, -1) },
        modifier = modifier,
        enabled = resolved.canScrollBackward && !disabled,
        variant = ButtonVariant.Outline,
        size = ButtonSize.Icon,
        label = "Previous slide",
        // Nova uses icon-sm (28dp); other presets retain the existing 32dp chrome.
        style =
            Style {
                shape(CircleShape)
                width(buttonSize)
                height(buttonSize)
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
    icon: @Composable () -> Unit = { CarouselChevron(previous = false) },
    disabled: Boolean = false,
) {
    val resolved = carouselState(state)
    val scope = rememberCoroutineScope()
    val canScrollNext = resolved.canScrollForward
    val buttonSize = if (currentTheme.activePreset == StylePreset.Nova) 28.dp else 32.dp
    Button(
        onClick = { scope.scrollCarousel(resolved, 1) },
        modifier = modifier,
        enabled = canScrollNext && !disabled,
        variant = ButtonVariant.Outline,
        size = ButtonSize.Icon,
        label = "Next slide",
        style =
            Style {
                shape(CircleShape)
                width(buttonSize)
                height(buttonSize)
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
