@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.breadcrumb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.icons.MoreHorizontal
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing
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
    Row(
        modifier =
            modifier
                .semantics { contentDescription = ariaLabel }
                .styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        content = content,
    )
}

/**
 * Matches shadcn's `BreadcrumbList`. Children stay in the parent [Breadcrumb]
 * row (no extra spacing) so existing flat call sites keep the same layout.
 */
@Composable
fun RowScope.BreadcrumbList(
    content: @Composable RowScope.() -> Unit,
) {
    content()
}

/**
 * Matches shadcn's `BreadcrumbItem`. Passthrough — crumb spacing lives on
 * [Breadcrumb], not on each item.
 */
@Composable
fun RowScope.BreadcrumbItem(
    content: @Composable RowScope.() -> Unit,
) {
    content()
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
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
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
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .styleable(styleState, touchTargetStyle then pressStyle then style),
        contentAlignment = Alignment.Center,
    ) {
        Text(variant = TextVariant.BodyMedium, textStyle = TextColors.Muted) { text }
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
    Text(
        variant = TextVariant.BodyMedium, modifier =
        modifier
            .semantics {
                contentDescription = text
                selected = true
            }
            .styleable(styleState, style)) { text }
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
        Icon(ChevronRight, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(modifier = modifier.styleable(styleState, style)) {
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
                .semantics { contentDescription = "More" }
                .styleable(styleState, style),
    ) {
        Icon(MoreHorizontal, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    }
}
