@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.breadcrumb

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.toutiaoyan.compose.ui.utils.rememberTheme
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.ui.icons.NovaChevronRight
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.icons.NovaEllipsis
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * A row of navigation links showing the current page's location in a hierarchy.
 * Matches real shadcn/ui's `breadcrumb.tsx` -- a bare `nav`/`ol`/`li` structure with no
 * styling of its own beyond the muted-link/current-page/separator treatment; there is no
 * routing here, [BreadcrumbLink]'s `onClick` is entirely caller-driven.
 *
 * Usage:
 * ```
 * Breadcrumb {
 *     BreadcrumbList {
 *         BreadcrumbItem("Home", onClick = {})
 *         BreadcrumbSeparator()
 *         BreadcrumbItem("Breadcrumb")
 *     }
 * }
 * ```
 */
@Composable
fun Breadcrumb(
    modifier: Modifier = Modifier,
    style: Style = Style,
    ariaLabel: String = "breadcrumb",
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    FlowRow(
        modifier =
            modifier
                .semantics { contentDescription = ariaLabel }
                .styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** Ordered, wrapping run of crumbs. Each BreadcrumbItem stays atomic. */
@Composable
fun RowScope.BreadcrumbList(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val state = remember { MutableStyleState(MutableInteractionSource()) }
    FlowRow(
        modifier = modifier.styleable(state, style),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** One inline-flex list item; icon and text content wrap as a single crumb. */
@Composable
fun RowScope.BreadcrumbItem(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val state = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier.styleable(state, style),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/**
 * Convenience: [text] as [BreadcrumbLink] when [onClick] is set, otherwise [BreadcrumbPage].
 */
@Composable
fun RowScope.BreadcrumbItem(
    text: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
) {
    BreadcrumbItem {
        if (onClick != null) {
            BreadcrumbLink(
                text = text,
                onClick = onClick,
                modifier = modifier,
                style = style,
                disabled = disabled,
            )
        } else {
            BreadcrumbPage(text = text, modifier = modifier, style = style)
        }
    }
}

/** A clickable, muted crumb -- every entry except the current page. */
@Composable
fun BreadcrumbLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
) {
    val resolvedEnabled = !disabled
    val theme = currentTheme
    val focusRing = theme.focusRingShadow()
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val linkStyle =
        rememberTheme(resolvedEnabled, focusRing) {
            Style {
                shape(RoundedCornerShape(2.dp))
                contentColor(colors.mutedForeground)
                focused { dropShadow(focusRing) }
                animate(
                    toSpec = tween(150, easing = FastOutSlowInEasing),
                    fromSpec = tween(150, easing = FastOutSlowInEasing),
                ) {
                    if (resolvedEnabled) hovered { contentColor(colors.foreground) }
                }
            }
        }
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .then(if (resolvedEnabled) Modifier else Modifier.alpha(0.5f))
                .semantics {
                    role = Role.Button
                    contentDescription = text
                    if (!resolvedEnabled) disabled()
                }
                .hoverable(interactionSource, enabled = resolvedEnabled)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .cssStyleable(styleState, touchTargetStyle then linkStyle then style),
        contentAlignment = Alignment.Center,
    ) {
        Text(variant = TextVariant.BodyMedium) { text }
    }
}

@Composable
@JvmName("BreadcrumbLinkLambda")
@OverloadResolutionByLambdaReturnType
fun BreadcrumbLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
    text: () -> String,
) {
    BreadcrumbLink(
        text = text(),
        onClick = onClick,
        modifier = modifier,
        style = style,
        disabled = disabled,
    )
}

/** The current, non-clickable page -- always the last entry in a [Breadcrumb]. */
@Composable
fun BreadcrumbPage(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .semantics {
                    contentDescription = "$text, current page"
                },
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = currentTheme.typography.bodyMedium.copy(color = currentTheme.colors.foreground),
            modifier =
                Modifier
                    .clearAndSetSemantics { }
                    .styleable(styleState, style),
        )
    }
}

@Composable
@JvmName("BreadcrumbPageLambda")
@OverloadResolutionByLambdaReturnType
fun BreadcrumbPage(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    BreadcrumbPage(text = text(), modifier = modifier, style = style)
}

/** A "›" divider between crumbs; insert manually where wanted, matching real shadcn's chevron default. */
@Composable
fun BreadcrumbSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit = {
        Icon(
            NovaChevronRight,
            modifier =
                Modifier.size(
                    14.dp,
                ).rotate(if (LocalLayoutDirection.current == LayoutDirection.Rtl) 180f else 0f),
            tint = currentTheme.colors.mutedForeground,
        )
    },
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier = modifier.clearAndSetSemantics { }.styleable(styleState, style),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** A collapsed run of hidden crumbs, e.g. `Home › ... › Settings`. Purely presentational -- expanding it is caller-driven. */
@Composable
fun BreadcrumbEllipsis(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .clearAndSetSemantics { }
                .styleable(
                    styleState,
                    Style {
                        width(20.dp)
                        height(20.dp)
                    } then style,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(NovaEllipsis, modifier = Modifier.size(16.dp), tint = currentTheme.colors.mutedForeground)
    }
}
