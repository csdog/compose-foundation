package com.toutiaoyan.compose.ui.components.checkbox
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

private val LocalCheckboxState = staticCompositionLocalOf<ToggleableState?> { null }

/** Toggle state of the nearest [Checkbox]. Matches Radix checkbox context. */
class CheckboxState(val state: ToggleableState) {
    val checked: Boolean get() = state == ToggleableState.On
    val indeterminate: Boolean get() = state == ToggleableState.Indeterminate
}

/** Reads the nearest [Checkbox]. */
@Composable
fun checkboxState(): CheckboxState {
    val state =
        checkNotNull(LocalCheckboxState.current) {
            "checkboxState() / CheckboxIndicator() must be used inside Checkbox { … }"
        }
    return CheckboxState(state)
}

/**
 * Usage:
 * ```
 * var checked by remember { mutableStateOf(false) }
 * Checkbox(checked = checked, onCheckedChange = { checked = it }, text = "Accept terms")
 *
 * // Tri-state (e.g. "select all" driven by children):
 * Checkbox(checked = allChecked, indeterminate = someChecked, onCheckedChange = { ... })
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Checkbox(
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    invalid: Boolean = false,
    required: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    defaultChecked: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit = { CheckboxIndicator() },
) {
    var internalChecked by remember { mutableStateOf(defaultChecked) }
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val isChecked = checked ?: internalChecked
    val setChecked: ((Boolean) -> Unit)? =
        if (checked == null || onCheckedChange != null) {
            { next ->
                if (checked == null) internalChecked = next
                onCheckedChange?.invoke(next)
            }
        } else {
            null
        }
    val toggleState =
        when {
            indeterminate -> ToggleableState.Indeterminate
            isChecked -> ToggleableState.On
            else -> ToggleableState.Off
        }
    val errorColor = currentTheme.colors.destructive
    val invalidStyle = if (invalid) Style { borderColor(errorColor) } else Style
    val interactionSource = remember { MutableInteractionSource() }
    val pressStyle = rememberPressMotionStyle(animation)
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.triStateToggle = toggleState
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
                .triStateToggleable(
                    state = toggleState,
                    onClick = { setChecked?.invoke(toggleState != ToggleableState.On) },
                    enabled = resolvedEnabled && setChecked != null,
                    role = Role.Checkbox,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalCheckboxState provides toggleState) {
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .styleable(styleState, rememberCheckboxStyle() then invalidStyle, style),
                contentAlignment = Alignment.Center,
            ) {
                content()
            }
        }
    }
}

/** Convenience: checkbox + visible [text] label beside it. Matches shadcn's Checkbox + Label demo. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Checkbox(
    text: String,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    description: String? = null,
    indeterminate: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    invalid: Boolean = false,
    required: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = text,
    defaultChecked: Boolean = false,
    style: Style = Style,
) {
    Row(
        modifier = modifier,
        verticalAlignment =
            if (description != null) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            indeterminate = indeterminate,
            enabled = resolvedEnabled,
            invalid = invalid,
            required = required,
            animation = animation,
            label = label,
            defaultChecked = defaultChecked,
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

/** Check / dash mark inside a [Checkbox]. Base UI does not export CheckboxIndicator. */
@Composable
internal fun CheckboxIndicator(
    state: ToggleableState = checkNotNull(LocalCheckboxState.current) {
        "CheckboxIndicator must be used inside Checkbox, or pass state="
    },
    modifier: Modifier = Modifier,
) {
    val markColor = currentTheme.colors.primaryForeground
    if (state == ToggleableState.Off) return
    Canvas(modifier = modifier.size(14.dp)) {
        val stroke = Stroke(width = size.width * 0.16f)
        if (state == ToggleableState.On) {
            val path =
                Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.55f)
                    lineTo(size.width * 0.42f, size.height * 0.8f)
                    lineTo(size.width * 0.88f, size.height * 0.2f)
                }
            drawPath(path, color = markColor, style = stroke)
        } else {
            drawLine(
                color = markColor,
                start = Offset(size.width * 0.2f, size.height / 2f),
                end = Offset(size.width * 0.8f, size.height / 2f),
                strokeWidth = stroke.width,
                cap = stroke.cap,
            )
        }
    }
}
