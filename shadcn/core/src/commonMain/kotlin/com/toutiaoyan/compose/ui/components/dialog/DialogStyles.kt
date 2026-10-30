@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

/**
 * Panel width for [DialogContent], matching base-nova `max-w-*`
 * (`sm` / `max-w-xs` ≈ 320dp, default `sm:max-w-sm` ≈ 384dp, `lg` ≈ 512dp / `max-w-lg`).
 */
enum class DialogSize {
    Sm,
    Default,
    Lg,
}

/** Panel chrome for [Dialog] / [AlertDialog] — base-nova `.cn-dialog-content`. */
@Composable
fun rememberDialogPanelStyle(size: DialogSize = DialogSize.Default): Style =
    rememberTheme(size) {
        val width =
            when (size) {
                DialogSize.Sm -> 320.dp
                DialogSize.Default -> 384.dp
                DialogSize.Lg -> 512.dp
            }
        Style {
            width(width)
            // Official `rounded-xl` `p-4` `bg-popover`
            shape(RoundedCornerShape(radius.xl))
            background(colors.popover)
            border(if (theme.activePreset == StylePreset.Nova) colors.foreground.copy(alpha = 0.1f) else colors.border)
            contentPadding(spacing.lg)
        }
    }
