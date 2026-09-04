@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.radiobutton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

private val LocalRadioSelected = staticCompositionLocalOf<Boolean?> { null }

/**
 * A single radio indicator. Combine several inside [RadioGroup] -- this
 * mirrors shadcn/ui's RadioGroup + RadioGroupItem split rather than a monolithic
 * "options list" API, so callers keep full control over row layout and labels.
 *
 * Usage:
 * ```
 * RadioGroup {
 *     Row(verticalAlignment = Alignment.CenterVertically) {
 *         RadioButton(selected = choice == "a", onClick = { choice = "a" })
 *         Label("Option A")
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    invalid: Boolean = false,
    required: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    style: Style = Style,
    content: @Composable () -> Unit = { RadioGroupIndicator() },
) {
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val errorColor = currentTheme.colors.destructive
    val invalidStyle = if (invalid) Style { borderColor(errorColor) } else Style
    val interactionSource = remember { MutableInteractionSource() }
    val pressStyle = rememberPressMotionStyle(animation)
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isSelected = selected
            it.isEnabled = resolvedEnabled
        }

    Box(
        modifier =
            modifier
                .semantics {
                    if (label.isNotEmpty()) {
                        contentDescription = if (required) "$label, required" else label
                    }
                    if (!resolvedEnabled) {
                        disabled()
                    }
                }
                .selectable(
                    selected = selected,
                    onClick = { onClick?.invoke() },
                    enabled = resolvedEnabled && onClick != null,
                    role = Role.RadioButton,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalRadioSelected provides selected) {
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .styleable(styleState, rememberRadioButtonStyle() then invalidStyle, style),
                contentAlignment = Alignment.Center,
            ) {
                content()
            }
        }
    }
}

/** Inner filled dot of a radio control. Base UI does not export RadioGroupIndicator. */
@Composable
internal fun RadioGroupIndicator(
    selected: Boolean =
        checkNotNull(LocalRadioSelected.current) {
            "RadioGroupIndicator must be used inside RadioButton, or pass selected="
        },
    modifier: Modifier = Modifier,
) {
    if (!selected) return
    val dotColor = currentTheme.colors.primaryForeground
    Canvas(modifier = modifier.size(8.dp)) {
        drawRoundRect(
            color = dotColor,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(size.minDimension / 2f),
        )
    }
}

/** Alias for [RadioButton] matching shadcn's `RadioGroupItem`. */
@Composable
fun RadioGroupItem(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    invalid: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    style: Style = Style,
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        invalid = invalid,
        animation = animation,
        label = label,
        style = style,
    )
}

enum class RadioGroupOrientation { Vertical, Horizontal }

/** Groups [RadioButton]s for accessibility semantics (single-selection).

 * Pass [value] + [onValueChange] for the Radix/shadcn controlled group; omit both
 * to keep each [RadioButton] fully caller-controlled.
 *
 * Usage:
 * ```
 * RadioGroup(value = choice, onValueChange = { choice = it }) {
 *     RadioGroupItem("a", "Option A")
 *     RadioGroupItem("b", "Option B")
 * }
 * RadioGroup(..., orientation = RadioGroupOrientation.Horizontal) { … }
 * ```
 */
@Immutable
internal class RadioGroupController(
    val value: String,
    val onValueChange: (String) -> Unit,
    val disabled: Boolean,
    val required: Boolean,
)

private val LocalRadioGroup = staticCompositionLocalOf<RadioGroupController?> { null }

class RadioGroupScope internal constructor()

/** Selected value of the nearest controlled [RadioGroup]. */
class RadioGroupState(
    val value: String,
    val onValueChange: (String) -> Unit,
)

/** Reads the nearest controlled [RadioGroup]. Matches Radix `useRadioGroupContext`. */
@Composable
fun radioGroupState(): RadioGroupState {
    val radio =
        checkNotNull(LocalRadioGroup.current) {
            "radioGroupState() must be used inside RadioGroup(value, onValueChange) { … }"
        }
    return RadioGroupState(value = radio.value, onValueChange = radio.onValueChange)
}

@Composable
fun RadioGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    defaultValue: String = "",
    orientation: RadioGroupOrientation = RadioGroupOrientation.Vertical,
    disabled: Boolean = false,
    required: Boolean = false,
    contentDescription: String? = null,
    content: @Composable RadioGroupScope.() -> Unit,
) {
    var internalValue by remember { mutableStateOf(defaultValue) }
    val current = value ?: internalValue
    val setValue: (String) -> Unit = { next ->
        if (value == null) internalValue = next
        onValueChange?.invoke(next)
    }
    val controller = RadioGroupController(current, setValue, disabled || LocalFieldSetDisabled.current, required)
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val grouped =
        modifier
            .selectableGroup()
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            )
            .styleable(styleState, style)
    CompositionLocalProvider(LocalRadioGroup provides controller) {
        if (orientation == RadioGroupOrientation.Horizontal) {
            Row(
                modifier = grouped,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioGroupScope().content()
            }
        } else {
            Column(modifier = grouped) {
                RadioGroupScope().content()
            }
        }
    }
}

/** Circle bound to the parent [RadioGroup] value. Matches shadcn's `RadioGroupItem`. */
@Composable
fun RadioGroupScope.RadioGroupItem(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    invalid: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    style: Style = Style,
) {
    val radio =
        checkNotNull(LocalRadioGroup.current) {
            "RadioGroupItem(value) must be used inside RadioGroup(value, onValueChange) { … }"
        }
    RadioButton(
        selected = radio.value == value,
        onClick = { radio.onValueChange(value) },
        modifier = modifier,
        enabled = enabled && !disabled && !radio.disabled,
        invalid = invalid,
        required = radio.required,
        animation = animation,
        label = label,
        style = style,
    )
}

/** Convenience: circle + visible [text] label beside it. */
@Composable
fun RadioGroupScope.RadioGroupItem(
    value: String,
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    invalid: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    style: Style = Style,
) {
    val radio =
        checkNotNull(LocalRadioGroup.current) {
            "RadioGroupItem(value, text) must be used inside RadioGroup { … }"
        }
    val resolvedEnabled = enabled && !disabled && !radio.disabled
    Row(
        modifier = modifier,
        verticalAlignment =
            if (description != null) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioGroupItem(
            value = value,
            enabled = resolvedEnabled,
            invalid = invalid,
            animation = animation,
            label = text,
            style = style,
        )
        if (description == null) {
            Label(text, disabled = !resolvedEnabled)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs2)) {
                Label(text, disabled = !resolvedEnabled)
                Text(
                    variant = TextVariant.BodySmall,
                    textStyle = TextColors.Muted,
                ) { description }
            }
        }
    }
}
