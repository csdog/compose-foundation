@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.style.ringShadow
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

@Composable
fun rememberNavMenuItemStyle(open: Boolean): Style =
    rememberTheme(open) {
        if (theme.activePreset == StylePreset.Nova) {
            Style {
                shape(RoundedCornerShape(radius.lg))
                height(36.dp)
                contentPadding(horizontal = 10.dp, vertical = 6.dp)
                fontSize(14.sp)
                lineHeight(20.sp)
                fontWeight(FontWeight.Medium)
                background(if (open) colors.muted.copy(alpha = colors.muted.alpha * 0.5f) else Color.Transparent)
                hovered { background(colors.muted) }
                focused { background(colors.muted) }
                focusRing(RoundedCornerShape(radius.lg))
                disabledDim()
            }
        } else {
            Style {
                shape(RoundedCornerShape(radius.sm))
                background(if (open) colors.secondary else colors.background)
                focusRing(RoundedCornerShape(radius.sm))
                contentPadding(horizontal = spacing.md, vertical = spacing.xs)
            }
        }
    }

@Composable
internal fun rememberNavMenuLinkStyle(
    active: Boolean,
    inPanel: Boolean,
): Style =
    rememberTheme(active, inPanel) {
        if (theme.activePreset == StylePreset.Nova) {
            val linkShape = RoundedCornerShape(if (inPanel) radius.md else radius.lg)
            Style {
                shape(linkShape)
                contentPadding(8.dp)
                fontSize(14.sp)
                lineHeight(20.sp)
                fontWeight(FontWeight.Normal)
                background(if (active) colors.muted.copy(alpha = colors.muted.alpha * 0.5f) else Color.Transparent)
                hovered { background(colors.muted) }
                focused { background(colors.muted) }
                if (!inPanel) focusRing(linkShape)
                disabledDim()
            }
        } else {
            Style {
                shape(RoundedCornerShape(radius.sm))
                background(if (active) colors.secondary else colors.background)
                focusRing(RoundedCornerShape(radius.sm))
                contentPadding(horizontal = spacing.md, vertical = spacing.xs)
            }
        }
    }

@Composable
fun rememberNavMenuPanelStyle(): Style =
    rememberTheme {
        val nova = theme.activePreset == StylePreset.Nova
        Style {
            if (!nova) width(320.dp)
            shape(RoundedCornerShape(if (nova) radius.lg else radius.md))
            background(colors.popover)
            contentColor(colors.popoverForeground)
            if (nova) {
                dropShadow(
                    Shadow(
                        radius =
                            2.dp,
                        spread =
                            (-1).dp,
                        offset =
                            DpOffset(
                                0.dp,
                                1.dp,
                            ),
                        color =
                            Color.Black.copy(
                                alpha =
                                0.1f,
                            ),
                    ),
                    Shadow(radius = 3.dp, offset = DpOffset(0.dp, 1.dp), color = Color.Black.copy(alpha = 0.1f)),
                    ringShadow(colors.foreground.copy(alpha = 0.1f)),
                )
            } else {
                border(colors.border)
            }
            contentPadding(if (nova) 4.dp else spacing.lg)
        }
    }

@Composable
fun rememberNavMenuIndicatorStyle(): Style =
    rememberTheme {
        Style {
            background(colors.border)
            shape(RoundedCornerShape(1.dp))
        }
    }
