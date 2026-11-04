@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

@Composable
fun rememberTabsListStyle(
    variant: TabsListVariant = TabsListVariant.Default,
    horizontal: Boolean = true,
): Style =
    rememberTheme(variant, horizontal) {
        if (variant == TabsListVariant.Line) {
            Style {
                shape(RectangleShape)
                background(Color.Transparent)
                // Official base still has `p-[3px]` on line lists.
                contentPadding(spacing.xs2)
                if (horizontal) height(32.dp)
            }
        } else {
            Style {
                shape(RoundedCornerShape(radius.lg))
                background(colors.muted)
                contentPadding(spacing.xs2)
                // base-nova: `group-data-horizontal/tabs:h-8`
                if (horizontal) height(32.dp)
            }
        }
    }

@Composable
fun rememberTabItemStyle(
    selected: Boolean,
    variant: TabsListVariant = TabsListVariant.Default,
): Style =
    rememberTheme(selected, variant) {
        Style {
            shape(RoundedCornerShape(radius.md))
            if (selected && variant == TabsListVariant.Default) {
                if (theme.isDark && theme.activePreset == com.toutiaoyan.compose.ui.tokens.StylePreset.Nova) {
                    background(colors.input.copy(alpha = colors.input.alpha * .3f))
                } else {
                    background(colors.background)
                }
            }
            focusRing(RoundedCornerShape(radius.md))
            if (theme.activePreset == com.toutiaoyan.compose.ui.tokens.StylePreset.Nova) {
                contentPadding(horizontal = 6.dp, vertical = 2.dp)
                borderWidth(1.dp)
                borderColor(
                    if (selected &&
                        variant == TabsListVariant.Default &&
                        theme.isDark
                    ) {
                        colors.input
                    } else {
                        Color.Transparent
                    },
                )
            } else {
                contentPadding(horizontal = spacing.md, vertical = spacing.xs)
            }
            disabledDim()
        }
    }

@Composable
fun rememberTabsIndicatorStyle(variant: TabsListVariant = TabsListVariant.Default): Style =
    rememberTheme(variant) {
        if (variant == TabsListVariant.Line) {
            Style {
                shape(RectangleShape)
                background(colors.foreground)
            }
        } else {
            Style {
                shape(RoundedCornerShape(radius.md))
                if (theme.activePreset == com.toutiaoyan.compose.ui.tokens.StylePreset.Nova && theme.isDark) {
                    background(colors.input.copy(alpha = colors.input.alpha * 0.3f))
                    borderWidth(1.dp)
                    borderColor(colors.input)
                } else {
                    background(colors.background)
                }
            }
        }
    }
