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
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
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
import com.toutiaoyan.compose.ui.utils.rememberTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusVisible
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

/** Logical icon edge, matching shadcn data-icon inline-start/inline-end. */
enum class BadgeIconPlacement { Start, End, Both }

/**
 * Label/tag component. Matches real shadcn badge.tsx: every variant reserves a 1dp
 * border (transparent except Outline), and [onClick] enables the link/`asChild`
 * focus-ring path.
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
    content: @Composable () -> Unit,
) {
    val uri = LocalUriHandler.current
    val action = onClick ?: href?.let { { uri.openUri(it) } }
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)

    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val hovered by interactionSource.collectIsHoveredAsState()
    val foreground =
        when {
            nova && variant == BadgeVariant.Destructive -> theme.colors.destructive
            nova &&
                variant == BadgeVariant.Ghost ->
                if (hovered) {
                    theme.colors.mutedForeground
                } else {
                    theme.colors.foreground
                }
            nova && variant == BadgeVariant.Outline && action != null && hovered -> theme.colors.mutedForeground
            else -> variant.contentColor(theme.colors)
        }
    val clickableStyle =
        rememberTheme(variant, action != null) {
            if (!nova || action == null) {
                Style
            } else {
                Style {
                    hovered {
                        when (variant) {
                            BadgeVariant.Default -> background(colors.primary.copy(alpha = 0.8f))
                            BadgeVariant.Secondary -> background(colors.secondary.copy(alpha = 0.8f))
                            BadgeVariant.Destructive -> background(colors.destructive.copy(alpha = 0.2f))
                            BadgeVariant.Outline -> {
                                background(colors.muted)
                                contentColor(colors.mutedForeground)
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    val direction = LocalLayoutDirection.current
    val iconStyle =
        rememberTheme(iconPlacement, direction) {
            if (!nova || iconPlacement == null) {
                Style
            } else {
                Style {
                    val start = if (iconPlacement != BadgeIconPlacement.End) 6.dp else 8.dp
                    val end = if (iconPlacement != BadgeIconPlacement.Start) 6.dp else 8.dp
                    // Style's scalar start/end are placed physically; map the logical edges here.
                    contentPadding(
                        start = if (direction == LayoutDirection.Ltr) start else end,
                        top = 0.dp,
                        end = if (direction == LayoutDirection.Ltr) end else start,
                        bottom = 0.dp,
                    )
                }
            }
        }
    val invalidStyle =
        rememberTheme(invalid) {
            if (!invalid) {
                Style
            } else {
                Style {
                    border(colors.destructive)
                    if (theme.ring.enabled) {
                        focusVisible {
                            dropShadow(
                                Shadow(
                                    radius = 0.dp,
                                    spread = theme.ring.width + theme.ring.offset,
                                    color = colors.destructive.copy(alpha = if (isDark) 0.4f else 0.2f),
                                ),
                            )
                        }
                    }
                }
            }
        }
    val badgeModifier =
        modifier
            .then(
                if (action != null) {
                    Modifier.keyboardInputMode().clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = if (href == null) Role.Button else null,
                        onClick = action,
                    )
                } else if (nova) {
                    Modifier.hoverable(interactionSource)
                } else {
                    Modifier
                },
            )
            .then(if (invalid) Modifier.semantics(mergeDescendants = true) { error("Invalid badge") } else Modifier)
            .styleable(styleState, variant.rememberStyle(), clickableStyle, iconStyle, invalidStyle, style)
    val badgeContent: @Composable () -> Unit = {
        CompositionLocalProvider(
            LocalContentColor provides foreground,
            LocalInlineIconSize provides if (nova) 12.dp else null,
            LocalTextDecoration provides
                if (nova &&
                    variant == BadgeVariant.Link &&
                    hovered
                ) {
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
) {
    Badge(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        style = style,
        href = href,
        invalid = invalid,
        iconPlacement = iconPlacement,
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
) = Badge(modifier, variant, onClick, style, href = null, invalid = false, iconPlacement = null, content = content)

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
