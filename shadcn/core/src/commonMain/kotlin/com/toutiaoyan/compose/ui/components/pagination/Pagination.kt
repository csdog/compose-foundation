@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.pagination

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.button.rememberStyle
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.icons.MoreHorizontal
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

class PaginationScope internal constructor()

class PaginationContentScope internal constructor()

@Immutable
class PaginationState(
    val page: Int,
    val pageCount: Int?,
    val onPageChange: (Int) -> Unit,
    val siblingCount: Int = 1,
    val showEdges: Boolean = false,
) {
    val canGoPrevious: Boolean get() = page > 1
    val canGoNext: Boolean get() = pageCount == null || page < pageCount
    val canGoFirst: Boolean get() = page > 1
    val canGoLast: Boolean get() = pageCount != null && page < pageCount

    fun goPrevious() {
        if (canGoPrevious) onPageChange(page - 1)
    }

    fun goNext() {
        if (canGoNext) onPageChange(page + 1)
    }

    fun goFirst() {
        if (canGoFirst) onPageChange(1)
    }

    fun goLast() {
        val last = pageCount ?: return
        if (canGoLast) onPageChange(last)
    }
}

private val LocalPagination = staticCompositionLocalOf<PaginationState?> { null }

/** Reads the nearest controlled [Pagination]. Matches a Compose-side page context. */
@Composable
fun paginationState(): PaginationState =
    checkNotNull(LocalPagination.current) {
        "paginationState() must be used inside Pagination(page, onPageChange) { … }"
    }

/**
 * Page-number navigation matching real shadcn/ui's `pagination.tsx`:
 *
 * ```
 * Pagination
 * └── PaginationContent
 *     ├── PaginationPrevious
 *     ├── PaginationItem
 *     │   └── PaginationLink
 *     ├── PaginationEllipsis
 *     └── PaginationNext
 * ```
 *
 * Active page uses [ButtonVariant.Outline]; the rest and Previous/Next use Ghost.
 *
 * Usage:
 * ```
 * var page by remember { mutableStateOf(1) }
 * Pagination(page = page, onPageChange = { page = it }, pageCount = 10)
 * Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
 *     PaginationContent {
 *         PaginationPrevious()
 *         PaginationLink(page = 1)
 *         PaginationEllipsis()
 *         PaginationLink(page = 10)
 *         PaginationNext()
 *     }
 * }
 * ```
 *
 * Omit [page] / [onPageChange] to keep each link fully caller-controlled
 * (`PaginationLink(page, isActive, onClick)`), matching real shadcn's presentational API.
 * Omit [content] to render Previous + a windowed page list + Next from [pageCount].
 */
@Composable
fun Pagination(
    modifier: Modifier = Modifier,
    style: Style = Style,
    page: Int? = null,
    onPageChange: ((Int) -> Unit)? = null,
    pageCount: Int? = null,
    siblingCount: Int = 1,
    showEdges: Boolean = false,
    ariaLabel: String = "pagination",
    content: @Composable PaginationScope.() -> Unit = { PaginationDefaultContent() },
) {
    val state =
        if (page != null && onPageChange != null) {
            PaginationState(
                page = page,
                pageCount = pageCount,
                onPageChange = onPageChange,
                siblingCount = siblingCount.coerceAtLeast(0),
                showEdges = showEdges,
            )
        } else {
            null
        }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(LocalPagination provides state) {
        Box(
            modifier =
                modifier
                    .semantics { contentDescription = ariaLabel }
                    .styleable(styleState, style),
        ) {
            PaginationScope().content()
        }
    }
}

private sealed interface PaginationToken {
    data class Page(val page: Int) : PaginationToken

    data object Ellipsis : PaginationToken
}

private fun paginationTokens(
    page: Int,
    pageCount: Int,
    siblingCount: Int = 1,
): List<PaginationToken> {
    if (pageCount <= 0) return emptyList()
    if (pageCount <= 7) return (1..pageCount).map { PaginationToken.Page(it) }
    val current = page.coerceIn(1, pageCount)
    val siblings = siblingCount.coerceAtLeast(0)
    val pages = mutableListOf(1, pageCount)
    for (i in (current - siblings)..(current + siblings)) {
        if (i in 1..pageCount) pages.add(i)
    }
    if (current <= siblings + 1) pages.add((siblings + 2).coerceAtMost(pageCount))
    if (current >= pageCount - siblings) pages.add((pageCount - siblings - 1).coerceAtLeast(1))
    val unique = pages.filter { it in 1..pageCount }.toSet().sorted()
    val tokens = mutableListOf<PaginationToken>()
    var previous: Int? = null
    for (n in unique) {
        if (previous != null && n - previous > 1) {
            tokens.add(PaginationToken.Ellipsis)
        }
        tokens.add(PaginationToken.Page(n))
        previous = n
    }
    return tokens
}

/**
 * Default [PaginationContent]: Previous, windowed page links from [paginationState], Next.
 * Matches the usual shadcn pagination demo without spelling out every [PaginationLink].
 */
@Composable
fun PaginationScope.PaginationDefaultContent() {
    val state = LocalPagination.current
    PaginationContent {
        if (state?.showEdges == true) PaginationFirst()
        PaginationPrevious()
        val page = state?.page
        val pageCount = state?.pageCount
        if (page != null && pageCount != null) {
            for (token in paginationTokens(page, pageCount, state.siblingCount)) {
                when (token) {
                    is PaginationToken.Page -> PaginationLink(token.page)
                    PaginationToken.Ellipsis -> PaginationEllipsis()
                }
            }
        }
        PaginationNext()
        if (state?.showEdges == true) PaginationLast()
    }
}

/** Row of page controls. Matches shadcn's `PaginationContent`. */
@Composable
fun PaginationScope.PaginationContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable PaginationContentScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PaginationContentScope().content()
    }
}

/** List-item wrapper (`<li>` in real shadcn). Optional around [PaginationLink]. */
@Composable
fun PaginationContentScope.PaginationItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier, content = { content() })
}

/**
 * A page-number button. Active uses Outline; inactive uses Ghost — matching
 * shadcn's `PaginationLink` + `isActive`.
 */
@Composable
fun PaginationLink(
    page: Int,
    isActive: Boolean? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
) {
    val state = LocalPagination.current
    val resolvedActive = isActive ?: (state != null && state.page == page)
    val resolvedEnabled = enabled && !disabled
    val resolvedClick =
        onClick ?: {
            checkNotNull(state) {
                "PaginationLink needs onClick= or Pagination(page, onPageChange)"
            }.onPageChange(page)
        }
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val variantStyle =
        if (resolvedActive) {
            ButtonVariant.Outline.rememberStyle()
        } else {
            ButtonVariant.Ghost.rememberStyle()
        }
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = "Go to page $page"
                    selected = resolvedActive
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = resolvedClick,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier.styleable(
                    styleState,
                    rememberPaginationButtonStyle() then variantStyle,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(variant = TextVariant.LabelLarge) { page.toString() }
        }
    }
}

/** Previous-page control. Matches shadcn's `PaginationPrevious` (`text` defaults off, like `sm:hidden`). */
@Composable
fun PaginationPrevious(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean? = null,
    disabled: Boolean = false,
    text: String? = null,
) {
    val state = LocalPagination.current
    PaginationControl(
        pointingLeft = true,
        enabled = (enabled ?: (state?.canGoPrevious ?: true)) && !disabled,
        onClick =
            onClick ?: {
                checkNotNull(state) {
                    "PaginationPrevious needs onClick= or Pagination(page, onPageChange)"
                }.goPrevious()
            },
        modifier = modifier,
        text = text,
    )
}

/** First-page control. Same chrome as [PaginationPrevious], double chevron. */
@Composable
fun PaginationFirst(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean? = null,
    disabled: Boolean = false,
    text: String? = null,
) {
    val state = LocalPagination.current
    PaginationControl(
        pointingLeft = true,
        enabled = (enabled ?: (state?.canGoFirst ?: true)) && !disabled,
        onClick =
            onClick ?: {
                checkNotNull(state) {
                    "PaginationFirst needs onClick= or Pagination(page, onPageChange)"
                }.goFirst()
            },
        modifier = modifier,
        text = text,
        doubleChevron = true,
        defaultDescription = "Go to first page",
    )
}

/** Last-page control. Same chrome as [PaginationNext], double chevron. */
@Composable
fun PaginationLast(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean? = null,
    disabled: Boolean = false,
    text: String? = null,
) {
    val state = LocalPagination.current
    PaginationControl(
        pointingLeft = false,
        enabled = (enabled ?: (state?.canGoLast ?: true)) && !disabled,
        onClick =
            onClick ?: {
                checkNotNull(state) {
                    "PaginationLast needs onClick= or Pagination(page, onPageChange)"
                }.goLast()
            },
        modifier = modifier,
        text = text,
        doubleChevron = true,
        defaultDescription = "Go to last page",
    )
}

/** Next-page control. Matches shadcn's `PaginationNext` (`text` defaults off, like `sm:hidden`). */
@Composable
fun PaginationNext(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean? = null,
    disabled: Boolean = false,
    text: String? = null,
) {
    val state = LocalPagination.current
    PaginationControl(
        pointingLeft = false,
        enabled = (enabled ?: (state?.canGoNext ?: true)) && !disabled,
        onClick =
            onClick ?: {
                checkNotNull(state) {
                    "PaginationNext needs onClick= or Pagination(page, onPageChange)"
                }.goNext()
            },
        modifier = modifier,
        text = text,
    )
}

/** Ellipsis gap between page ranges. Matches shadcn's `PaginationEllipsis`. */
@Composable
fun PaginationEllipsis(modifier: Modifier = Modifier) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .semantics { contentDescription = "More pages" }
                .styleable(styleState, rememberPaginationButtonStyle()),
        contentAlignment = Alignment.Center,
    ) {
        Icon(MoreHorizontal, tint = currentTheme.colors.mutedForeground)
    }
}

@Composable
private fun PaginationControl(
    pointingLeft: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    doubleChevron: Boolean = false,
    defaultDescription: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = enabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val iconTint = if (enabled) currentTheme.colors.foreground else currentTheme.colors.mutedForeground
    val chevron =
        @Composable {
            val icon =
                @Composable {
                    Icon(
                        ChevronRight,
                        tint = iconTint,
                        modifier = if (pointingLeft) Modifier.rotate(180f) else Modifier,
                    )
                }
            if (doubleChevron) {
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    icon()
                    icon()
                }
            } else {
                icon()
            }
        }
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription =
                        text
                            ?: defaultDescription
                            ?: if (pointingLeft) "Go to previous page" else "Go to next page"
                    if (!enabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isNullOrEmpty()) {
            Box(
                modifier =
                    Modifier.styleable(
                        styleState,
                        rememberPaginationButtonStyle() then ButtonVariant.Ghost.rememberStyle(),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                chevron()
            }
        } else {
            Row(
                modifier =
                    Modifier.styleable(
                        styleState,
                        rememberPaginationLabeledButtonStyle() then ButtonVariant.Ghost.rememberStyle(),
                    ),
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (pointingLeft) {
                    chevron()
                    Text(variant = TextVariant.LabelLarge) { text }
                } else {
                    Text(variant = TextVariant.LabelLarge) { text }
                    chevron()
                }
            }
        }
    }
}
