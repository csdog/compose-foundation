@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.TwShadow
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.utils.rememberTheme

enum class SidebarMenuButtonVariant { Default, Outline }

enum class SidebarMenuButtonSize { Default, Sm, Lg }

/** Official `variant` on `Sidebar`: default rail, floating card, or inset shell. */
enum class SidebarVariant { Sidebar, Floating, Inset }

@Composable
fun rememberSidebarRailStyle(): Style =
    rememberTheme {
        Style {
            height(1f)
            background(colors.sidebar)
        }
    }

@Composable
fun rememberSidebarContentStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            height(1f)
            contentPadding(spacing.sm)
        }
    }

@Composable
fun rememberSidebarInnerStyle(variant: SidebarVariant): Style =
    rememberTheme(variant) {
        when (variant) {
            SidebarVariant.Floating ->
                Style {
                    shape(RoundedCornerShape(radius.lg))
                    border(colors.sidebarBorder)
                    dropShadow(Shadow(radius = TwShadow.xs, color = Color.Black.copy(alpha = 0.25f)))
                    background(colors.sidebar)
                }
            SidebarVariant.Inset ->
                Style {
                    background(colors.sidebar)
                }
            SidebarVariant.Sidebar -> Style {}
        }
    }

@Composable
fun rememberSidebarInsetStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            height(1f)
            background(colors.background)
        }
    }

@Composable
fun rememberSidebarInsetCardStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            height(1f)
            background(colors.background)
            shape(RoundedCornerShape(radius.xl))
            dropShadow(Shadow(radius = TwShadow.xs, color = Color.Black.copy(alpha = 0.25f)))
        }
    }

@Composable
fun rememberSidebarTriggerStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            focusRing(RoundedCornerShape(radius.md))
            contentPadding(spacing.xs)
        }
    }

/**
 * Official `.cn-sidebar-menu-button`: `h-8` / `h-7` / `h-12`, `p-2`, `rounded-md`,
 * hover/active `bg-sidebar-accent`, outline 1px shadow, icon-collapsed `size-8`.
 */
@Composable
fun rememberSidebarMenuButtonStyle(
    active: Boolean,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    iconCollapsed: Boolean = false,
): Style =
    rememberTheme(active, variant, size, iconCollapsed) {
        val shape = RoundedCornerShape(radius.md)
        val height =
            when {
                iconCollapsed -> 32.dp
                size == SidebarMenuButtonSize.Sm -> 28.dp
                size == SidebarMenuButtonSize.Lg -> 48.dp
                else -> 32.dp
            }
        val pad =
            when {
                iconCollapsed && size == SidebarMenuButtonSize.Lg -> 0.dp
                else -> spacing.sm
            }
        Style {
            if (iconCollapsed) {
                width(32.dp)
            } else {
                width(1f)
            }
            height(height)
            shape(shape)
            focusRing(shape)
            contentPadding(pad)
            disabledDim()
            if (active) background(colors.sidebarAccent)
            if (variant == SidebarMenuButtonVariant.Outline) {
                if (!active) background(colors.background)
                dropShadow(Shadow(radius = 0.dp, spread = 1.dp, color = colors.sidebarBorder))
                hovered {
                    background(colors.sidebarAccent)
                    dropShadow(Shadow(radius = 0.dp, spread = 1.dp, color = colors.sidebarAccent))
                }
            } else {
                hovered { background(colors.sidebarAccent) }
            }
            pressed { background(colors.sidebarAccent) }
        }
    }

/** @deprecated Use [rememberSidebarMenuButtonStyle]. */
@Composable
fun rememberSidebarMenuItemStyle(
    active: Boolean,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
): Style = rememberSidebarMenuButtonStyle(active, variant, size)

@Composable
fun rememberSidebarMenuActionStyle(): Style =
    rememberTheme {
        val shape = RoundedCornerShape(radius.md)
        Style {
            width(20.dp)
            height(20.dp)
            shape(shape)
            focusRing(shape)
            hovered { background(colors.sidebarAccent) }
        }
    }

@Composable
fun rememberSidebarMenuBadgeStyle(): Style =
    rememberTheme {
        Style {
            height(20.dp)
            shape(RoundedCornerShape(radius.md))
            contentPadding(horizontal = spacing.scale1, vertical = 0.dp)
        }
    }

@Composable
fun rememberSidebarMenuSubButtonStyle(
    active: Boolean,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
): Style =
    rememberTheme(active, size) {
        val shape = RoundedCornerShape(radius.md)
        Style {
            width(1f)
            height(28.dp)
            shape(shape)
            focusRing(shape)
            contentPadding(horizontal = spacing.sm, vertical = 0.dp)
            disabledDim()
            if (active) background(colors.sidebarAccent)
            hovered { background(colors.sidebarAccent) }
            pressed { background(colors.sidebarAccent) }
        }
    }
