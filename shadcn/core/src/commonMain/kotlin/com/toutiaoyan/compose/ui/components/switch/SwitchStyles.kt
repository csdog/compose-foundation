@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.switch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.checked
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

/**
 * Track / thumb sizes matching real shadcn `switch.tsx` `data-[size=*]`.
 *
 * - [Default]: `w-8` × `h-[1.15rem]` (32×~18), thumb `size-4` (16)
 * - [Sm]: `w-6` × `h-3.5` (24×14), thumb `size-3` (12)
 */
sealed interface SwitchSize {
    val trackWidth: Dp
    val trackHeight: Dp
    val thumbSize: Dp

    data object Default : SwitchSize {
        override val trackWidth = 32.dp
        override val trackHeight = 18.dp
        override val thumbSize = 16.dp
    }

    data object Sm : SwitchSize {
        override val trackWidth = 24.dp
        override val trackHeight = 14.dp
        override val thumbSize = 12.dp
    }
}

/** Checked thumb inset matching real `translate-x-[calc(100%-2px)]`. */
val SwitchSize.thumbInset: Dp get() = 2.dp

private const val NOVA_UNCHECKED_TRACK_DARK_ALPHA_MULTIPLIER = 0.8f

internal fun novaSwitchUncheckedTrackColor(
    input: Color,
    dark: Boolean,
): Color = input.copy(alpha = input.alpha * if (dark) NOVA_UNCHECKED_TRACK_DARK_ALPHA_MULTIPLIER else 1f)

// Matches shadcn/ui's real switch.tsx: border border-transparent (1.dp, constant),
// data-[state=unchecked]:bg-input (not muted), data-[state=checked]:bg-primary,
// focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50.
// No hover state defined.
@Composable
fun rememberSwitchTrackStyle(): Style =
    rememberTheme {
        Style {
            background(
                if (theme.activePreset == StylePreset.Nova) {
                    novaSwitchUncheckedTrackColor(colors.input, theme.isDark)
                } else {
                    colors.border
                },
            )
            border(Color.Transparent)
            checked { background(colors.primary) }
            focusRing(RoundedCornerShape(radius.full))
            disabledDim()
        }
    }
