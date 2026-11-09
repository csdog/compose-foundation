@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.layout.PaddingValues
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.style.shadowMd
import com.toutiaoyan.compose.tailwind.style.shadowXs
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing

sealed interface SelectVariant {
    data object Default : SelectVariant
}

sealed interface SelectSize {
    data object Default : SelectSize

    data object Sm : SelectSize
}

/** base-nova SelectTrigger tokens (`.cn-select-trigger`). */
object SelectDefaults {
    /** Official `data-[size=default]:h-8`. */
    val TriggerHeight = 32.dp

    /** Official `data-[size=sm]:h-7`. */
    val TriggerHeightSm = 28.dp
}

/**
 * Matches base-nova `SelectTrigger`
 * (`data-[size=default]:h-8` / `data-[size=sm]:h-7` / `rounded-lg` / `pl-2.5 pr-2` + focus ring).
 */
@Composable
fun SelectVariant.rememberStyle(size: SelectSize = SelectSize.Default): Style =
    rememberTheme(this, size) {
        Style {
            background(
                if (theme.activePreset == StylePreset.Nova) {
                    if (theme.isDark) colors.input.copy(alpha = colors.input.alpha * 0.3f) else Color.Transparent
                } else {
                    Color.Transparent
                },
            )
            border(colors.input)
            shape(RoundedCornerShape(radius.lg))
            focusRing(RoundedCornerShape(radius.lg))
            // Official `pl-2.5 pr-2` — approximate with horizontal 2.5; py from height.
            if (theme.activePreset == StylePreset.Nova) {
                contentPadding(PaddingValues(start = 10.dp, end = 8.dp))
                if (size == SelectSize.Sm) shape(RoundedCornerShape(minOf(radius.md, 10.dp)))
                hovered { if (theme.isDark) background(colors.input.copy(alpha = colors.input.alpha * 0.5f)) }
                disabledDim()
            } else {
                contentPadding(horizontal = spacing.scale2p5, vertical = spacing.scale1)
            }
            height(
                if (size is SelectSize.Sm) {
                    SelectDefaults.TriggerHeightSm
                } else {
                    SelectDefaults.TriggerHeight
                },
            )
            if (theme.activePreset != StylePreset.Nova) shadowXs()
        }
    }

/**
 * Matches real shadcn's `SelectContent`
 * (`bg-popover text-popover-foreground shadow-md` + viewport `p-1`).
 */
@Composable
fun SelectVariant.rememberPanelStyle(): Style =
    rememberTheme(this) {
        Style {
            background(colors.popover)
            border(if (theme.activePreset == StylePreset.Nova) colors.foreground.copy(alpha = 0.1f) else colors.border)
            shape(RoundedCornerShape(if (theme.activePreset == StylePreset.Nova) radius.lg else radius.md))
            contentPadding(if (theme.activePreset == StylePreset.Nova) 0.dp else spacing.xs2)
            shadowMd()
        }
    }

/**
 * Matches real shadcn's `SelectItem`
 * (`py-1.5 pl-2 pr-8 text-sm` + `focus:bg-accent focus:text-accent-foreground`).
 * Selection is indicated by a trailing Check (see [Select]), not a filled background.
 */
@Composable
fun rememberSelectItemStyle(): Style =
    rememberTheme {
        Style {
            background(Color.Transparent)
            contentColor(colors.popoverForeground)
            shape(RoundedCornerShape(if (theme.activePreset == StylePreset.Nova) radius.md else radius.sm))
            if (theme.activePreset == StylePreset.Nova) {
                minHeight(28.dp)
                contentPadding(PaddingValues(start = 6.dp, end = 8.dp, top = 4.dp, bottom = 4.dp))
            } else {
                contentPadding(horizontal = spacing.sm, vertical = 1.5.spacing.dp)
            }
            if (theme.activePreset == StylePreset.Nova) {
                focused {
                    background(colors.accent)
                    contentColor(colors.accentForeground)
                }
            }
            hovered {
                background(colors.accent)
                contentColor(colors.accentForeground)
            }
            disabledDim()
        }
    }
