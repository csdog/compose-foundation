@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.inputotp

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.separator.SeparatorOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.currentTheme

enum class InputOTPType { Text, Password }

@Immutable
internal class InputOTPController(
    val value: String,
    val maxLength: Int,
    val isFocused: Boolean,
    val masked: Boolean,
)

private val LocalInputOTP = staticCompositionLocalOf<InputOTPController?> { null }

@Composable
private fun inputOTPController(): InputOTPController =
    checkNotNull(LocalInputOTP.current) {
        "InputOTPGroup / InputOTPSlot must be used inside InputOTP { … }"
    }

/** Value of the nearest [InputOTP]. Matches the input-otp context. */
class InputOTPState(
    val value: String,
    val maxLength: Int,
    val isFocused: Boolean,
    val masked: Boolean,
)

/** Reads the nearest [InputOTP] state. */
@Composable
fun inputOTPState(): InputOTPState {
    val ctrl = inputOTPController()
    return InputOTPState(
        value = ctrl.value,
        maxLength = ctrl.maxLength,
        isFocused = ctrl.isFocused,
        masked = ctrl.masked,
    )
}

class InputOTPScope internal constructor()

class InputOTPGroupScope internal constructor()

/**
 * A one-time-passcode input matching real shadcn/ui's `input-otp.tsx`:
 *
 * ```
 * InputOTP
 * └── InputOTPGroup
 *     ├── InputOTPSlot
 *     ├── InputOTPSeparator
 *     └── InputOTPSlot
 * ```
 *
 * One invisible [BasicTextField] captures typing/paste/backspace; the slots are
 * visual only. Pass [length] so paste is clipped.
 *
 * Usage:
 * ```
 * var code by remember { mutableStateOf("") }
 * InputOTP(value = code, onValueChange = { code = it }, length = 6) {
 *     InputOTPGroup {
 *         InputOTPSlot(0); InputOTPSlot(1); InputOTPSlot(2)
 *         InputOTPSeparator()
 *         InputOTPSlot(3); InputOTPSlot(4); InputOTPSlot(5)
 *     }
 * }
 * ```
 *
 * The [content] slot is optional: omit it for a flat row of [length] slots
 * (no separator). Pass [InputOTPGroup] / [InputOTPSlot] / [InputOTPSeparator]
 * for the full shadcn composition.
 *
 * [accept] matches shadcn's `pattern` — default digits only. Pass
 * `{ it.isLetterOrDigit() }` or [pattern] `Regex("[A-Za-z0-9]")` for alphanumeric
 * codes. [masked] shows a bullet per filled slot (password-style), matching
 * input-otp `type="password"`. Official [type] [InputOTPType.Password] is the
 * same as [masked] `true`. [maxLength] is the official name for [length].
 * [onComplete] fires once the value reaches [length] (paste included).
 */
@Composable
fun InputOTP(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    modifier: Modifier = Modifier,
    maxLength: Int? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    accept: (Char) -> Boolean = { it.isDigit() },
    pattern: Regex? = null,
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    masked: Boolean = false,
    readOnly: Boolean = false,
    type: InputOTPType? = null,
    onComplete: ((String) -> Unit)? = null,
    style: Style = Style,
    content: (@Composable InputOTPScope.() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val resolvedLength = maxLength ?: length
    val resolvedAccept: (Char) -> Boolean =
        if (pattern != null) {
            { char -> pattern.matches(char.toString()) }
        } else {
            accept
        }
    val resolvedMasked =
        when (type) {
            InputOTPType.Password -> true
            InputOTPType.Text -> false
            null -> masked
        }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val onCompleteUpdated = rememberUpdatedState(onComplete)
    LaunchedEffect(value, resolvedLength) {
        if (resolvedLength > 0 && value.length >= resolvedLength) {
            onCompleteUpdated.value?.invoke(value)
        }
    }
    val styleState = remember { MutableStyleState(interactionSource) }
    val controller =
        InputOTPController(
            value = value,
            maxLength = resolvedLength,
            isFocused = isFocused,
            masked = resolvedMasked,
        )

    CompositionLocalProvider(LocalInputOTP provides controller) {
        Box(modifier = modifier.styleable(styleState, style)) {
            if (content != null) {
                InputOTPScope().content()
            } else {
                InputOTPScope().InputOTPGroup {
                    repeat(resolvedLength) { index ->
                        InputOTPSlot(index)
                    }
                }
            }
            BasicTextField(
                value = value,
                onValueChange = { new -> onValueChange(new.filter(resolvedAccept).take(resolvedLength)) },
                enabled = resolvedEnabled,
                readOnly = readOnly,
                modifier = Modifier.matchParentSize(),
                interactionSource = interactionSource,
                textStyle = currentTheme.typography.bodyMedium.copy(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            )
        }
    }
}

/** Row of slots. Matches shadcn's `InputOTPGroup`. */
@Composable
fun InputOTPScope.InputOTPGroup(
    modifier: Modifier = Modifier,
    content: @Composable InputOTPGroupScope.() -> Unit,
) {
    inputOTPController()
    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InputOTPGroupScope().content()
    }
}

/** One digit box. [index] is 0-based. Matches shadcn's `InputOTPSlot`. */
@Composable
fun InputOTPGroupScope.InputOTPSlot(
    index: Int,
    modifier: Modifier = Modifier,
) {
    val ctrl = inputOTPController()
    val char = ctrl.value.getOrNull(index)
    val isActive = ctrl.isFocused && index == ctrl.value.length
    OtpSlot(char = char, isActive = isActive, masked = ctrl.masked, modifier = modifier)
}

/** Vertical divider between slot groups. Matches shadcn's `InputOTPSeparator`. */
@Composable
fun InputOTPGroupScope.InputOTPSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(
        modifier = modifier.fillMaxHeight(),
        orientation = SeparatorOrientation.Vertical,
        style = style,
    )
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
private fun OtpSlot(
    char: Char?,
    isActive: Boolean,
    masked: Boolean,
    modifier: Modifier = Modifier,
) {
    val theme = currentTheme
    val slotStyle =
        Style {
            background(theme.colors.background)
            borderWidth(1.dp)
            borderColor(if (isActive) theme.colors.ring else theme.colors.border)
            shape(RoundedCornerShape(twRadius.md))
            if (isActive) dropShadow(theme.focusRingShadow())
        }

    Box(
        modifier =
            modifier
                .size(36.dp)
                .styleable(remember { MutableStyleState(MutableInteractionSource()) }, slotStyle),
        contentAlignment = Alignment.Center,
    ) {
        Text(variant = TextVariant.TitleMedium) {
            when {
                char == null -> ""
                masked -> "•"
                else -> char.toString()
            }
        }
    }
}
