@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toggle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.checked
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

// Matches shadcn/ui's real toggle.tsx: shared hover:bg-muted, data-[state=on]:bg-accent
// (our `secondary` token stands in for `accent`, since they're the same value in
// the reference theme). `checked` is declared after `hovered` so the pressed-state
// color wins if both interaction states are active at once, same as CSS cascade order.
sealed interface ToggleVariant {
    data object Default : ToggleVariant

    data object Outline : ToggleVariant
}

/** Same color each variant passes to Style `contentColor(...)`, for [LocalContentColor]. */
fun ToggleVariant.contentColor(colors: Colors): Color =
    when (this) {
        ToggleVariant.Default, ToggleVariant.Outline -> colors.foreground
    }

/**
 * Heights / min-widths matching base-nova toggle:
 * `default` → [Md] (`h-8`/`min-w-8`), `sm` → [Sm] (`h-7`/`min-w-7`), `lg` → [Lg] (`h-9`/`min-w-9`).
 */
sealed interface ToggleSize {
    data object Sm : ToggleSize

    data object Md : ToggleSize

    data object Lg : ToggleSize
}

@Composable
fun ToggleVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        val foreground = receiver.contentColor(colors)
        when (receiver) {
            ToggleVariant.Default ->
                Style {
                    background(if (theme.activePreset == StylePreset.Nova) colors.background else Color.Transparent)
                    contentColor(foreground)
                    hovered { background(colors.muted) }
                    checked {
                        background(
                            if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
                        )
                    }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ToggleVariant.Outline ->
                Style {
                    background(if (theme.activePreset == StylePreset.Nova) colors.background else Color.Transparent)
                    contentColor(foreground)
                    border(if (theme.activePreset == StylePreset.Nova) colors.input else colors.border)
                    hovered {
                        background(
                            if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
                        )
                    }
                    checked {
                        background(
                            if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
                        )
                    }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }
        }
    }
}

@Composable
fun ToggleSize.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            // base-nova sm = h-7 min-w-7 px-2.5
            ToggleSize.Sm ->
                Style {
                    contentPadding(horizontal = spacing.scale2p5, vertical = 0.dp)
                    height(28.dp)
                    minWidth(28.dp)
                    fontSize(12.8.sp)
                    if (theme.activePreset == StylePreset.Nova) shape(RoundedCornerShape(minOf(radius.md, 12.dp)))
                }

            // base-nova default = h-8 min-w-8 px-2.5
            ToggleSize.Md ->
                Style {
                    contentPadding(horizontal = spacing.scale2p5, vertical = 0.dp)
                    height(32.dp)
                    minWidth(32.dp)
                    fontSize(14.sp)
                }

            // base-nova lg = h-9 min-w-9 px-2.5
            ToggleSize.Lg ->
                Style {
                    contentPadding(horizontal = spacing.scale2p5, vertical = 0.dp)
                    height(36.dp)
                    minWidth(36.dp)
                    fontSize(14.sp)
                }
        }
    }
}
