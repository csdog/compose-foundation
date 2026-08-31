package com.toutiaoyan.compose.ui.components.switch
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

@Immutable
class SwitchState(
    val checked: Boolean,
    val thumbOffset: Dp,
    val thumbSize: Dp,
    val thumbColor: Color,
)

private val LocalSwitchState = staticCompositionLocalOf<SwitchState?> { null }

/** Reads the nearest [Switch]. Matches Radix switch context. */
@Composable
fun switchState(): SwitchState =
    checkNotNull(LocalSwitchState.current) {
        "switchState() / SwitchThumb() must be used inside Switch { … }"
    }

/**
 * Usage:
 * ```
 * var enabled by remember { mutableStateOf(false) }
 * Switch(checked = enabled, onCheckedChange = { enabled = it }, text = "Airplane Mode")
 * Switch(checked = enabled, onCheckedChange = { enabled = it })
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Switch(
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    size: SwitchSize = SwitchSize.Default,
    invalid: Boolean = false,
    required: Boolean = false,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    defaultChecked: Boolean = false,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit = { SwitchThumb() },
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
    val errorColor = currentTheme.colors.destructive
    val invalidStyle = if (invalid) Style { borderColor(errorColor) } else Style
    val interactionSource = remember { MutableInteractionSource() }
    val pressStyle = rememberPressMotionStyle(animation)
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isChecked = isChecked
            it.isEnabled = resolvedEnabled
        }
    val thumbInset = size.thumbInset
    val thumbOffset by animateDpAsState(
        if (isChecked) size.trackWidth - size.thumbSize - thumbInset else thumbInset,
    )
    val thumbColor = currentTheme.colors.background
    val state =
        SwitchState(
            checked = isChecked,
            thumbOffset = thumbOffset,
            thumbSize = size.thumbSize,
            thumbColor = thumbColor,
        )

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
                .toggleable(
                    value = isChecked,
                    onValueChange = { setChecked?.invoke(it) },
                    enabled = resolvedEnabled && setChecked != null,
                    role = Role.Switch,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalSwitchState provides state) {
            Box(
                modifier =
                    Modifier
                        .size(width = size.trackWidth, height = size.trackHeight)
                        .styleable(styleState, rememberSwitchTrackStyle() then invalidStyle, style),
                content = content,
            )
        }
    }
}

/** Convenience: switch + visible [text] label beside it. Matches shadcn's Switch + Label demo. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Switch(
    text: String,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    size: SwitchSize = SwitchSize.Default,
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = resolvedEnabled,
            size = size,
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

/** Sliding knob of a [Switch]. Base UI does not export SwitchThumb. */
@Composable
internal fun BoxScope.SwitchThumb(
    modifier: Modifier = Modifier,
    color: Color = LocalSwitchState.current?.thumbColor ?: currentTheme.colors.background,
) {
    val switch = LocalSwitchState.current
    val positioned =
        if (switch != null) {
            Modifier
                .align(Alignment.CenterStart)
                .offset(x = switch.thumbOffset)
                .size(switch.thumbSize)
                .then(modifier)
        } else {
            modifier
        }
    Box(modifier = positioned.background(color, CircleShape))
}
