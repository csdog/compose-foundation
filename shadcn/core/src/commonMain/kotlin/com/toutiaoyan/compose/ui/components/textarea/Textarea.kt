@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.textarea

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputVariant

/**
 * Multi-line text input. Matches shadcn/ui's real textarea.tsx, which shares the
 * exact same border/focus/disabled spec as Input -- this is a thin wrapper over
 * [Input] with `singleLine = false` and a minimum height.
 *
 * Like [Input], this is a pure control (no label / description) — compose with
 * [com.toutiaoyan.compose.ui.components.field.Field].
 *
 * Usage:
 * ```
 * var bio by remember { mutableStateOf("") }
 * Textarea(value = bio, onValueChange = { bio = it }, placeholder = "Tell us about yourself")
 * Textarea(..., minLines = 6)
 * ```
 */
@Composable
fun Textarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    readOnly: Boolean = false,
    placeholder: String? = null,
    isError: Boolean = false,
    invalid: Boolean = false,
    required: Boolean = false,
    maxLength: Int? = null,
    autoFocus: Boolean = false,
    variant: InputVariant = InputVariant.Default,
    style: Style = Style,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    rows: Int? = null,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    Input(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.defaultMinSize(minHeight = 64.dp),
        enabled = enabled,
        disabled = disabled,
        readOnly = readOnly,
        placeholder = placeholder,
        isError = isError,
        invalid = invalid,
        required = required,
        maxLength = maxLength,
        autoFocus = autoFocus,
        variant = variant,
        style = style,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = false,
        minLines = rows ?: minLines,
        maxLines = maxLines,
    )
}
