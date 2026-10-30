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
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.ui.semantics.Role
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

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
    content: @Composable () -> Unit,
) {
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
            nova && variant == BadgeVariant.Outline && onClick != null && hovered -> theme.colors.mutedForeground
            else -> variant.contentColor(theme.colors)
        }
    val clickableStyle =
        rememberTheme(variant, onClick != null) {
            if (!nova || onClick == null) {
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
    val badgeModifier =
        modifier
            .then(
                if (onClick != null) {
                    Modifier.keyboardInputMode().clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else if (nova) {
                    Modifier.hoverable(interactionSource)
                } else {
                    Modifier
                },
            )
            .styleable(styleState, variant.rememberStyle(), clickableStyle, style)
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
) {
    Badge(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        style = style,
        content = { Text { text } },
    )
}
