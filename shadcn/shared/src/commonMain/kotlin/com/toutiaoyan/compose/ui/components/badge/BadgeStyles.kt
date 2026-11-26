@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.badge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import com.toutiaoyan.compose.ui.styles.focusVisible
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.RememberThemeScope
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRing

sealed interface BadgeVariant {
    data object Default : BadgeVariant

    data object Secondary : BadgeVariant

    data object Destructive : BadgeVariant

    data object Outline : BadgeVariant

    data object Ghost : BadgeVariant

    /** Official `variant="link"`: primary text, underline on hover. */
    data object Link : BadgeVariant
}

/** Base tint for legacy presets. Nova resolves tinted/destructive and hover colors inside Badge. */
fun BadgeVariant.contentColor(colors: Colors): Color =
    when (this) {
        BadgeVariant.Default -> colors.primaryForeground
        BadgeVariant.Secondary -> colors.secondaryForeground
        BadgeVariant.Destructive -> colors.destructiveForeground
        BadgeVariant.Outline -> colors.foreground
        BadgeVariant.Ghost -> colors.mutedForeground
        BadgeVariant.Link -> colors.primary
    }

@Composable
fun BadgeVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        if (theme.activePreset == StylePreset.Nova) {
            val foreground =
                when (receiver) {
                    BadgeVariant.Destructive -> colors.destructive
                    BadgeVariant.Ghost -> colors.foreground
                    else -> receiver.contentColor(colors)
                }
            return@rememberTheme Style {
                height(20.dp)
                border(if (receiver == BadgeVariant.Outline) colors.border else Color.Transparent)
                // Center a 16px line box inside the fixed 20px inline-flex badge.
                contentPadding(horizontal = spacing.scale2, vertical = 0.dp)
                fontSize(12.sp)
                fontWeight(FontWeight.Medium)
                contentColor(foreground)
                focusRing(RoundedCornerShape(radius.full))
                when (receiver) {
                    BadgeVariant.Default -> background(colors.primary)
                    BadgeVariant.Secondary -> background(colors.secondary)
                    BadgeVariant.Destructive -> {
                        background(colors.destructive.copy(alpha = if (isDark) 0.2f else 0.1f))
                        if (ring.enabled) {
                            focusVisible {
                                dropShadow(
                                    Shadow(
                                        radius = 0.dp,
                                        spread = ring.width + ring.offset,
                                        color = colors.destructive.copy(alpha = if (isDark) 0.4f else 0.2f),
                                    ),
                                )
                            }
                        }
                    }
                    BadgeVariant.Ghost -> {
                        background(Color.Transparent)
                        hovered {
                            background(colors.muted.copy(alpha = if (isDark) 0.5f else 1f))
                            contentColor(colors.mutedForeground)
                        }
                    }
                    BadgeVariant.Link -> hovered { textDecoration(TextDecoration.Underline) }
                    BadgeVariant.Outline -> Unit
                }
            }
        }
        legacyBadgeStyle(receiver)
    }
}

private fun RememberThemeScope.legacyBadgeStyle(variant: BadgeVariant): Style =
    Style {
        when (variant) {
            BadgeVariant.Default -> background(colors.primary)
            BadgeVariant.Secondary -> background(colors.secondary)
            BadgeVariant.Destructive -> background(colors.destructive)
            BadgeVariant.Ghost -> background(colors.muted)
            BadgeVariant.Outline -> Unit
            BadgeVariant.Link -> hovered { textDecoration(TextDecoration.Underline) }
        }
        contentColor(variant.contentColor(colors))
        // Reserve the outline variant's border width in all variants.
        border(if (variant == BadgeVariant.Outline) colors.border else Color.Transparent)
        contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
        fontSize(12.sp)
        if (variant != BadgeVariant.Ghost) fontWeight(FontWeight.SemiBold)
        focusRing(RoundedCornerShape(radius.full))
    }

internal fun badgeForeground(
    colors: Colors,
    variant: BadgeVariant,
    nova: Boolean,
    hovered: Boolean,
    clickable: Boolean,
): Color =
    when {
        nova && variant == BadgeVariant.Destructive -> colors.destructive
        nova &&
            variant == BadgeVariant.Ghost ->
            if (hovered) {
                colors.mutedForeground
            } else {
                colors.foreground
            }
        nova && variant == BadgeVariant.Outline && clickable && hovered -> colors.mutedForeground
        else -> variant.contentColor(colors)
    }

@Composable
internal fun rememberBadgeClickStyle(
    variant: BadgeVariant,
    nova: Boolean,
    clickable: Boolean,
): Style =
    rememberTheme(variant, nova, clickable) {
        if (!nova || !clickable) {
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

@Composable
internal fun rememberBadgeIconStyle(
    iconPlacement: BadgeIconPlacement?,
    nova: Boolean,
): Style {
    val direction = LocalLayoutDirection.current
    return rememberTheme(iconPlacement, nova, direction) {
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
}

@Composable
internal fun rememberInvalidBadgeStyle(invalid: Boolean): Style =
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
