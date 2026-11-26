@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

// Chip isn't a real shadcn/ui component (shadcn only ships a static Badge), so
// there's no upstream reference here -- but it follows the same conventions
// established from Button/Input/Checkbox/Radio/Switch/Toggle: `focusRing(...)`
// handles the focus ring for every variant.
sealed interface ChipVariant {
    data object Default : ChipVariant

    data object Selected : ChipVariant

    data object Outline : ChipVariant
}

/** Same color each variant passes to Style `contentColor(...)`, for [LocalContentColor]. */
fun ChipVariant.contentColor(colors: Colors): Color =
    when (this) {
        ChipVariant.Default -> colors.secondaryForeground
        ChipVariant.Selected -> colors.primaryForeground
        ChipVariant.Outline -> colors.foreground
    }

@Composable
fun ChipVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        Style {
            when (receiver) {
                ChipVariant.Default -> background(colors.secondary)
                ChipVariant.Selected -> background(colors.primary)
                ChipVariant.Outline -> Unit
            }
            contentColor(receiver.contentColor(colors))
            border(if (receiver == ChipVariant.Selected) colors.primary else colors.border)
            focusRing(RoundedCornerShape(radius.full))
            contentPadding(horizontal = spacing.md, vertical = spacing.xs)
            fontSize(13.sp)
            when (receiver) {
                ChipVariant.Default -> {
                    val highlight = colors.secondary.copy(alpha = 0.9f)
                    hovered { background(highlight) }
                    pressed { background(highlight) }
                }
                ChipVariant.Outline -> hovered { background(colors.secondary) }
                ChipVariant.Selected -> Unit
            }
            disabledDim()
        }
    }
}
