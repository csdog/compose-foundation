@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
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
            background(Color.Transparent)
            border(colors.input)
            shape(RoundedCornerShape(radius.lg))
            focusRing(RoundedCornerShape(radius.lg))
            // Official `pl-2.5 pr-2` — approximate with horizontal 2.5; py from height.
            contentPadding(horizontal = spacing.scale2p5, vertical = spacing.scale1)
            height(
                if (size is SelectSize.Sm) {
                    SelectDefaults.TriggerHeightSm
                } else {
                    SelectDefaults.TriggerHeight
                },
            )
            shadowXs()
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
            border(colors.border)
            shape(RoundedCornerShape(radius.md))
            contentPadding(spacing.xs2)
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
            shape(RoundedCornerShape(radius.sm))
            contentPadding(horizontal = spacing.sm, vertical = 1.5.spacing.dp)
            hovered {
                background(colors.accent)
                contentColor(colors.accentForeground)
            }
            disabledDim()
        }
    }
