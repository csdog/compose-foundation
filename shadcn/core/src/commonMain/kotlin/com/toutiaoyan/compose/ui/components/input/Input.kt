package com.toutiaoyan.compose.ui.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.inputgroup.LocalInsideInputGroup
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.colors
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Single-line (or multi-line) text input matching base-nova `input.tsx`
 * (`.cn-input`: `h-8 rounded-lg`).
 *
 * Pure control — no label / description / leading·trailing addons. Compose those with:
 * - [com.toutiaoyan.compose.ui.components.field.Field] / FieldLabel / FieldDescription / FieldError
 * - [com.toutiaoyan.compose.ui.components.inputgroup.InputGroup] for icon/prefix/suffix addons
 *
 * [type] maps HTML `<input type>` to Compose keyboard + masking defaults; pass
 * [keyboardOptions] / [visualTransformation] to override.
 *
 * Usage:
 * ```
 * Input(value = email, onValueChange = { email = it }, type = InputType.Email, placeholder = "you@example.com")
 * Input(..., readOnly = true)
 * Input(..., invalid = true)
 * InputGroup {
 *     InputGroupAddon(InputGroupAddonAlign.InlineStart) { Icon(Lucide.Search) }
 *     Input(value = q, onValueChange = { q = it }, placeholder = "Search…")
 * }
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Input(
    value: String,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    readOnly: Boolean = false,
    type: InputType = InputType.Text,
    placeholder: String? = null,
    isError: Boolean = false,
    invalid: Boolean = false,
    required: Boolean = false,
    maxLength: Int? = null,
    autoFocus: Boolean = false,
    variant: InputVariant = InputVariant.Default,
    style: Style = Style,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    LaunchedEffect(autoFocus) {
        if (autoFocus) focusRequester.requestFocus()
    }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }

    // currentTheme.colors.destructive captured as a plain val before the Style{} block, not
    // read directly inside it -- see InputGroup.kt's comment on this same
    // anti-pattern (a stale CompositionLocal snapshot that survives dark-mode toggles).
    val errorColor = currentTheme.colors.destructive
    val resolvedError = isError || invalid
    val errorStyle = if (resolvedError) Style { borderColor(errorColor) } else Style

    // Inside a InputGroup the container owns the border and focus ring (real
    // shadcn's InputGroupInput is `border-0 focus-visible:ring-0`); drawing our own
    // here would double the border and put the ring on the field instead of the group.
    // dropShadow() with no arguments explicitly clears the ring set by variant's own
    // `focused { }` block earlier in the `then` chain -- Style properties are override-
    // not-additive per *declared* property, but an omitted property falls through to
    // whatever an earlier style in the chain already set, so simply not calling
    // dropShadow() here would leave the variant's ring showing underneath.
    val insideGroup = LocalInsideInputGroup.current
    val insideGroupStyle =
        if (insideGroup) {
            Style {
                background(Color.Transparent)
                borderColor(Color.Transparent)
                focused {
                    borderColor(Color.Transparent)
                    dropShadow()
                }
            }
        } else {
            Style
        }

    // base-nova `.cn-input` is `h-8` → `8.spacing.dp` (32dp at default unit). Without a
    // fixed height, empty placeholder Text and the filled BasicTextField lay out
    // to different intrinsic heights. Textarea passes singleLine = false and keeps a
    // flexible min height instead.
    val inputHeight = InputDefaults.Height
    val singleLineStyle =
        if (singleLine) {
            Style { height(inputHeight) }
        } else {
            Style
        }

    BasicTextField(
        value = value,
        onValueChange = { next ->
            onValueChange(if (maxLength != null) next.take(maxLength) else next)
        },
        enabled = resolvedEnabled,
        readOnly = readOnly,
        // Explicit color: BasicTextField paints its value text (and cursor) from
        // textStyle/cursorBrush, not from the ambient Style contentColor -- without
        // this the value text stays near-black in dark mode (same lesson as
        // ChipVariant.contentColor: text color must be passed explicitly).
        textStyle = currentTheme.typography.bodyMedium.copy(color = currentTheme.colors.foreground),
        cursorBrush = SolidColor(currentTheme.colors.foreground),
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (autoFocus) Modifier.focusRequester(focusRequester) else Modifier)
                .then(
                    if (required) {
                        Modifier.semantics {
                            contentDescription = placeholder?.let { "$it, required" } ?: "required"
                        }
                    } else {
                        Modifier
                    },
                )
                .styleable(
                    styleState,
                    variant.rememberStyle() then errorStyle then insideGroupStyle then singleLineStyle,
                    style,
                ),
        keyboardOptions = keyboardOptions ?: type.keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation ?: type.visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines ?: if (singleLine) 1 else Int.MAX_VALUE,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty() && placeholder != null) {
                    // TextStyles.Muted overrides Input's foreground contentColor with mutedForeground.
                    Text(
                        textStyle = currentTheme.typography.bodyMedium.merge(TextColors.Muted),
                        style = Style { contentColor(colors.mutedForeground) },
                    ) { placeholder }
                }
                innerTextField()
            }
        },
    )
}
