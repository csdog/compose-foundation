@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.input

import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.focused
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRingAlways
import com.toutiaoyan.compose.ui.utils.rememberTheme

/**
 * HTML/shadcn `<input type>` analogue. Drives default [KeyboardOptions] /
 * [VisualTransformation]; pass those params on [Input] to override.
 *
 * Only maps types that Compose can express (keyboard + masking). Browser-only
 * types (`file`, `date`, `checkbox`, …) stay as separate components.
 */
sealed interface InputType {
    data object Text : InputType

    data object Password : InputType

    data object Email : InputType

    data object Tel : InputType

    data object Url : InputType

    data object Number : InputType

    data object Decimal : InputType

    data object Search : InputType
}

internal val InputType.keyboardOptions: KeyboardOptions
    get() =
        when (this) {
            InputType.Text -> KeyboardOptions.Default
            InputType.Password -> KeyboardOptions(keyboardType = KeyboardType.Password)
            InputType.Email -> KeyboardOptions(keyboardType = KeyboardType.Email)
            InputType.Tel -> KeyboardOptions(keyboardType = KeyboardType.Phone)
            InputType.Url -> KeyboardOptions(keyboardType = KeyboardType.Uri)
            InputType.Number -> KeyboardOptions(keyboardType = KeyboardType.Number)
            InputType.Decimal -> KeyboardOptions(keyboardType = KeyboardType.Decimal)
            InputType.Search -> KeyboardOptions(keyboardType = KeyboardType.Text)
        }

internal val InputType.visualTransformation: VisualTransformation
    get() =
        when (this) {
            InputType.Password -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        }

internal const val NovaInputColorDurationMillis = 150
internal val NovaInputColorTransition = tween<Float>(NovaInputColorDurationMillis)

/** base-nova Input tokens (`.cn-input`). */
object InputDefaults {
    /** Official `h-8`. */
    val Height: Dp
        @Composable get() = twSpacing.scale8
}

// Matches base-nova input: border, h-8 rounded-lg px-2.5 py-1,
// focus-visible:border-ring focus-visible:ring-3 -- `focusRingAlways(...)` draws
// an outer shadow ring without growing the border.
sealed interface InputVariant {
    data object Default : InputVariant

    data object Filled : InputVariant

    data object Ghost : InputVariant
}

@Composable
fun InputVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            InputVariant.Default ->
                Style {
                    val nova = theme.activePreset == StylePreset.Nova
                    if (nova) {
                        border(colors.input)
                        animate(NovaInputColorTransition) {
                            val fill = colors.input.copy(alpha = colors.input.alpha * 0.3f)
                            background(if (theme.isDark) fill else Color.Transparent)
                            contentColor(colors.foreground)
                            borderColor(colors.input)
                        }
                    } else {
                        background(colors.background)
                        contentColor(colors.foreground)
                        border(colors.border)
                    }
                    // Official `px-2.5 py-1`.
                    contentPadding(horizontal = spacing.scale2p5, vertical = spacing.scale1)
                    textStyle(theme.typography.bodyMedium)
                    focusRingAlways(RoundedCornerShape(radius.lg))
                    disabledDim()
                    if (nova) {
                        focused {
                            animate(NovaInputColorTransition) { borderColor(colors.ring) }
                        }
                        disabled {
                            animate(NovaInputColorTransition) {
                                val opacity = if (theme.isDark) 0.8f else 0.5f
                                background(colors.input.copy(alpha = colors.input.alpha * opacity))
                            }
                        }
                    }
                }

            InputVariant.Filled ->
                Style {
                    background(colors.muted)
                    contentColor(colors.foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.scale2p5, vertical = spacing.scale1)
                    textStyle(theme.typography.bodyMedium)
                    shape(RoundedCornerShape(radius.lg))
                    focusRingAlways(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            InputVariant.Ghost ->
                Style {
                    contentColor(colors.foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.xs, vertical = spacing.xs)
                    textStyle(theme.typography.bodyMedium)
                    shape(RoundedCornerShape(radius.lg))
                    focusRingAlways(RoundedCornerShape(radius.lg))
                    disabledDim()
                }
        }
    }
}
