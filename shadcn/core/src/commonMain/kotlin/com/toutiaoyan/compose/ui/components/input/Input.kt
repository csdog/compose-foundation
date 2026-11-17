package com.toutiaoyan.compose.ui.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.then
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.layout.PaddingValues
import com.toutiaoyan.compose.ui.components.inputgroup.LocalInputGroupDisabled
import com.toutiaoyan.compose.ui.components.inputgroup.LocalInputGroupLayout
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.inputgroup.LocalInsideInputGroup
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.colors
import com.toutiaoyan.compose.ui.theme.currentTheme

internal val LocalInputCompositionObserver = staticCompositionLocalOf<((Boolean) -> Unit)?> { null }

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
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current && !LocalInputGroupDisabled.current
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
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val errorColor = theme.colors.destructive
    val resolvedError = isError || invalid
    val errorStyle =
        if (resolvedError) {
            Style {
                if (nova) {
                    animate(NovaInputColorTransition) {
                        borderColor(if (theme.isDark) errorColor.copy(alpha = errorColor.alpha * 0.5f) else errorColor)
                    }
                    dropShadow(
                        Shadow(
                            radius = 0.dp,
                            spread = 3.dp,
                            color = errorColor.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
                        ),
                    )
                } else {
                    borderColor(errorColor)
                }
            }
        } else {
            Style
        }
    val compactViewport =
        with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width < 768.dp.roundToPx() }
    val foreground =
        if (nova && variant == InputVariant.Default) {
            animateColorAsState(
                theme.colors.foreground,
                tween(NovaInputColorDurationMillis),
                label = "Input foreground",
            ).value
        } else {
            theme.colors.foreground
        }
    val inputTypography =
        theme.typography.bodyMedium.copy(
            fontFamily = if (nova) theme.fontFamily else theme.typography.bodyMedium.fontFamily,
            color = foreground,
            fontSize = if (nova && compactViewport) 16.sp else theme.typography.bodyMedium.fontSize,
            lineHeight = if (nova && compactViewport) 24.sp else theme.typography.bodyMedium.lineHeight,
        )

    // Inside a InputGroup the container owns the border and focus ring (real
    // shadcn's InputGroupInput is `border-0 focus-visible:ring-0`); drawing our own
    // here would double the border and put the ring on the field instead of the group.
    // dropShadow() with no arguments explicitly clears the ring set by variant's own
    // `focused { }` block earlier in the `then` chain -- Style properties are override-
    // not-additive per *declared* property, but an omitted property falls through to
    // whatever an earlier style in the chain already set, so simply not calling
    // dropShadow() here would leave the variant's ring showing underneath.
    val insideGroup = LocalInsideInputGroup.current
    val groupLayout = LocalInputGroupLayout.current
    val groupKey = remember { Any() }
    DisposableEffect(groupLayout, groupKey) {
        if (groupLayout != null) groupLayout.requesters[groupKey] = focusRequester
        onDispose {
            groupLayout?.requesters?.remove(groupKey)
            groupLayout?.controls?.remove(groupKey)
        }
    }
    SideEffect { groupLayout?.controls?.set(groupKey, resolvedError to resolvedEnabled) }
    val addons = groupLayout?.addons?.values.orEmpty()
    val insideGroupStyle =
        if (insideGroup) {
            Style {
                background(Color.Transparent)
                if (nova) {
                    contentPadding(
                        PaddingValues(
                            start = if (InputGroupAddonAlign.InlineStart in addons) 6.dp else 10.dp,
                            end = if (InputGroupAddonAlign.InlineEnd in addons) 6.dp else 10.dp,
                            top =
                                if (singleLine && InputGroupAddonAlign.BlockEnd in addons) {
                                    12.dp
                                } else if (singleLine) {
                                    4.dp
                                } else {
                                    8.dp
                                },
                            bottom =
                                if (singleLine && InputGroupAddonAlign.BlockStart in addons) {
                                    12.dp
                                } else if (singleLine) {
                                    4.dp
                                } else {
                                    8.dp
                                },
                        ),
                    )
                }
                borderColor(Color.Transparent)
                if (nova) borderWidth(androidx.compose.ui.unit.Dp.Unspecified)
                dropShadow()
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
        if (singleLine &&
            !(
                nova &&
                    insideGroup &&
                    (
                        InputGroupAddonAlign.BlockStart in addons ||
                            InputGroupAddonAlign.BlockEnd in addons
                    )
            )
        ) {
            Style { height(inputHeight) }
        } else {
            Style
        }

    var fieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    val displayedValue = fieldValue.copy(text = value)
    val compositionObserver = LocalInputCompositionObserver.current
    SideEffect {
        if (fieldValue != displayedValue) fieldValue = displayedValue
        compositionObserver?.invoke(displayedValue.composition != null)
    }
    DisposableEffect(compositionObserver) { onDispose { compositionObserver?.invoke(false) } }
    BasicTextField(
        value = displayedValue,
        onValueChange = { next ->
            val text = if (maxLength != null) next.text.take(maxLength) else next.text
            fieldValue = next.copy(text = text)
            compositionObserver?.invoke(fieldValue.composition != null)
            if (text != value) onValueChange(text)
        },
        enabled = resolvedEnabled,
        readOnly = readOnly,
        // Explicit color: BasicTextField paints its value text (and cursor) from
        // textStyle/cursorBrush, not from the ambient Style contentColor -- without
        // this the value text stays near-black in dark mode (same lesson as
        // ChipVariant.contentColor: text color must be passed explicitly).
        textStyle = inputTypography,
        cursorBrush = SolidColor(foreground),
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (resolvedError) Modifier.semantics { error("Invalid input") } else Modifier)
                .then(if (autoFocus || insideGroup) Modifier.focusRequester(focusRequester) else Modifier)
                .then(
                    if (required) {
                        Modifier.semantics {
                            contentDescription = placeholder?.let { "$it, required" } ?: "required"
                        }
                    } else {
                        Modifier
                    },
                )
                .cssStyleable(
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
            Box(contentAlignment = if (singleLine || !nova) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty() && placeholder != null) {
                    // TextStyles.Muted overrides Input's foreground contentColor with mutedForeground.
                    Text(
                        textStyle = inputTypography.copy(color = theme.colors.mutedForeground),
                        style = Style { contentColor(colors.mutedForeground) },
                    ) { placeholder }
                }
                innerTextField()
            }
        },
    )
}
