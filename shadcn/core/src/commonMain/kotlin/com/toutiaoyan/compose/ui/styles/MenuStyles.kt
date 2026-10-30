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
import com.toutiaoyan.compose.tailwind.style.shadowMd
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberMenuPanelStyle(): Style {
    val contextNova = LocalContextMenuNova.current
    return rememberTheme(contextNova) {
        Style {
            width(if (contextNova) 192.dp else 224.dp)
            shape(RoundedCornerShape(if (contextNova) radius.lg else radius.md))
            background(colors.popover)
            border(if (contextNova) colors.foreground.copy(alpha = 0.1f) else colors.border)
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
