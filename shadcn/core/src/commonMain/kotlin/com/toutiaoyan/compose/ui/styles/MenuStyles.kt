@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuNova
import androidx.compose.foundation.style.focused
import com.toutiaoyan.compose.tailwind.style.ringShadow
import com.toutiaoyan.compose.tailwind.style.shadowMd
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

/**
 * Shared panel chrome for anchored menu surfaces (dropdown / context menu / menubar).
 *
 * Nova's official `cn-dropdown-menu-content` is `min-w-32` with no fixed width: the
 * dropdown content width comes from the trigger (`w-(--anchor-width)`), and submenu
 * panels are content-sized (`w-auto` + `min-w-[96px]`). Callers that need the official
 * width pass [novaWidth] / [novaMinWidth]; existing callers keep the legacy fixed width.
 *
 * [novaRing] draws Nova's official `ring-1 ring-foreground/10` as an outer ring shadow instead
 * of an inner `border`. The CSS ring is `box-shadow`, so it adds no layout: a `border` here
 * consumes 1dp on each side and shrinks every menu row by 2dp. Only callers that opt in change
 * behavior; context menu / menubar keep the existing border rendering.
 */
@Composable
fun rememberMenuPanelStyle(
    novaWidth: Dp? = null,
    novaMinWidth: Dp? = null,
    novaRing: Boolean = false,
): Style {
    val contextNova = LocalContextMenuNova.current
    return rememberTheme(contextNova, novaWidth, novaMinWidth, novaRing) {
        Style {
            if (contextNova) {
                if (novaMinWidth != null) minWidth(novaMinWidth) else width(novaWidth ?: 192.dp)
            } else {
                width(224.dp)
            }
            shape(RoundedCornerShape(if (contextNova) radius.lg else radius.md))
            background(colors.popover)
            when {
                contextNova && novaRing ->
                    dropShadow(ringShadow(colors.foreground.copy(alpha = 0.1f)))
                contextNova -> border(colors.foreground.copy(alpha = 0.1f))
                else -> border(colors.border)
            }
            if (contextNova) shadowMd()
            contentPadding(if (contextNova) 4.dp else spacing.xs2)
        }
    }
}

@Composable
fun rememberMenuItemStyle(): Style {
    val contextNova = LocalContextMenuNova.current
    return rememberTheme(contextNova) {
        Style {
            width(1f)
            shape(RoundedCornerShape(radius.sm))
            background(colors.popover)
            contentPadding(horizontal = if (contextNova) 6.dp else spacing.sm, vertical = spacing.xs)
            hovered { background(if (contextNova) colors.accent else colors.muted) }
            if (contextNova) focused { background(colors.accent) }
            disabledDim()
        }
    }
}

@Composable
fun rememberMenuLabelStyle(): Style {
    val contextNova = LocalContextMenuNova.current
    return rememberTheme(contextNova) {
        Style {
            width(1f)
            contentPadding(horizontal = if (contextNova) 6.dp else spacing.sm, vertical = spacing.xs)
        }
    }
}
