package com.toutiaoyan.compose.ui.components.badge
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.theme.LocalTextDecoration
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

/** Logical icon edge, matching shadcn data-icon inline-start/inline-end. */
enum class BadgeIconPlacement { Start, End, Both }

/** Badge with a locally remembered interaction source. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
    href: String? = null,
    invalid: Boolean = false,
    iconPlacement: BadgeIconPlacement? = null,
    content: @Composable () -> Unit,
) = Badge(modifier, variant, onClick, style, href, invalid, iconPlacement, interactionSource = null, content = content)

/** Text badge with a locally remembered interaction source. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
    href: String? = null,
    invalid: Boolean = false,
    iconPlacement: BadgeIconPlacement? = null,
) = Badge(text, modifier, variant, onClick, style, href, invalid, iconPlacement, interactionSource = null)

/**
 * Label/tag component. Matches real shadcn badge.tsx: every variant reserves a 1dp
 * border (transparent except Outline), and [onClick] enables the link/`asChild`
 * focus-ring path. Pass [interactionSource] to control or inspect hover/press state;
 * omitting it keeps a remembered source local to this badge.
 *
 * Usage:
 * ```
 * Badge("New")
 * Badge("Error", variant = BadgeVariant.Destructive)
 * Badge("Docs", onClick = { /* navigate */ })
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
    href: String? = null,
    invalid: Boolean = false,
    iconPlacement: BadgeIconPlacement? = null,
    interactionSource: MutableInteractionSource?,
    content: @Composable () -> Unit,
) {
    val uri = LocalUriHandler.current
    val action = onClick ?: href?.let { { uri.openUri(it) } }
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(resolvedInteractionSource)

    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val hovered by resolvedInteractionSource.collectIsHoveredAsState()
    val foreground = badgeForeground(theme.colors, variant, nova, hovered, action != null)
    val clickableStyle = rememberBadgeClickStyle(variant, nova, action != null)
    val iconStyle = rememberBadgeIconStyle(iconPlacement, nova)
    val invalidStyle = rememberInvalidBadgeStyle(invalid)
    val badgeModifier =
        modifier
            .then(
                if (action != null) {
                    Modifier.keyboardInputMode().clickable(
                        interactionSource = resolvedInteractionSource,
                        indication = null,
                        role = if (href == null) Role.Button else null,
                        onClick = action,
                    )
                } else if (nova) {
                    Modifier.hoverable(resolvedInteractionSource)
                } else {
                    Modifier
                },
            )
            .then(if (invalid) Modifier.semantics(mergeDescendants = true) { error("Invalid badge") } else Modifier)
            .styleable(styleState, variant.rememberStyle(), clickableStyle, iconStyle, invalidStyle, style)
    val badgeContent: @Composable () -> Unit = {
        BadgeContent(foreground, nova, underline = variant == BadgeVariant.Link && hovered, content = content)
    }
    if (nova) {
        Row(
            modifier = badgeModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) { badgeContent() }
    } else {
        Box(modifier = badgeModifier, contentAlignment = Alignment.Center) { badgeContent() }
    }
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
    href: String? = null,
    invalid: Boolean = false,
    iconPlacement: BadgeIconPlacement? = null,
    interactionSource: MutableInteractionSource?,
) {
    Badge(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        style = style,
        href = href,
        invalid = invalid,
        iconPlacement = iconPlacement,
        interactionSource = interactionSource,
        content = { Text(maxLines = 1) { text } },
    )
}

// Retain the pre-0.3.30 JVM entry points while source calls use the extended overloads.
@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
    content: @Composable () -> Unit,
) = Badge(
    modifier,
    variant,
    onClick,
    style,
    href = null,
    invalid = false,
    iconPlacement = null,
    interactionSource = null,
    content = content,
)

@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
) = Badge(text, modifier, variant, onClick, style, href = null, invalid = false, iconPlacement = null)

@Composable
private fun BadgeContent(
    foreground: Color,
    nova: Boolean,
    underline: Boolean,
    content: @Composable () -> Unit,
) {
    val theme = currentTheme
    CompositionLocalProvider(
        LocalContentColor provides foreground,
        LocalInlineIconSize provides if (nova) 12.dp else null,
        LocalTextDecoration provides
            if (nova && underline) {
                TextDecoration.Underline
            } else {
                null
            },
        LocalDefaultTextStyle provides
            if (nova) {
                theme.typography.bodySmall.copy(
                    fontSize =
                        12.sp,
                    lineHeight =
                        16.sp,
                    fontWeight =
                        FontWeight.Medium,
                    fontFamily =
                        theme.fontFamily,
                    lineHeightStyle =
                        LineHeightStyle(
                            LineHeightStyle.Alignment.Center,
                            LineHeightStyle.Trim.None,
                            LineHeightStyle.Mode.Tight,
                        ),
                )
            } else {
                TextStyle.Default
            },
    ) {
        content()
    }
}
