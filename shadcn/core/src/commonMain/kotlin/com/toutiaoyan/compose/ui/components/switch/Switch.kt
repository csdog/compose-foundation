package com.toutiaoyan.compose.ui.components.switch
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.key
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.style.checked
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.field.FieldLabel
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
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val uncheckedTrackColor =
        theme.colors.input.copy(
            alpha = theme.colors.input.alpha * if (theme.isDark) 0.8f else 1f,
        )
    val trackColor = if (isChecked) theme.colors.primary else uncheckedTrackColor
    // The wrapper transitions the root background; the thumb only transitions its transform.
    // A new palette starts at its own color instead of interpolating stale theme tokens.
    val animatedTrackColor =
        if (nova) {
            key(theme.colors) {
                animateColorAsState(trackColor, tween(150, easing = FastOutSlowInEasing)).value
            }
        } else {
            trackColor
        }
    val trackMotionStyle =
        if (nova) {
            Style {
                background(animatedTrackColor)
                checked { background(animatedTrackColor) }
            }
        } else {
            Style
        }
    val errorColor = theme.colors.destructive
    val invalidStyle =
        if (invalid) {
            Style {
                borderColor(if (nova && theme.isDark) errorColor.copy(alpha = errorColor.alpha * 0.5f) else errorColor)
                if (nova) {
                    dropShadow(
                        Shadow(
                            radius = 0.dp,
                            spread = 3.dp,
                            color = errorColor.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
                        ),
                    )
                }
            }
        } else {
            Style
        }
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
        if (isChecked) {
            size.trackWidth - size.thumbSize - thumbInset
        } else if (nova) {
            0.dp
        } else {
            thumbInset
        },
        animationSpec = if (nova) tween(150) else spring(),
    )
    val thumbColor =
        if (nova && theme.isDark) {
            if (isChecked) theme.colors.primaryForeground else theme.colors.foreground
        } else {
            theme.colors.background
        }
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
                    if (invalid) error("Invalid selection")
                    if (label.isNotEmpty()) {
                        contentDescription = if (required) "$label, required" else label
                    }
                    if (!resolvedEnabled) {
                        disabled()
                    }
                }
                .keyboardInputMode()
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
                        .size(
                            width = size.trackWidth,
                            height = if (nova && size == SwitchSize.Default) 18.4.dp else size.trackHeight,
                        )
                        .cssStyleable(
                            styleState,
                            rememberSwitchTrackStyle() then trackMotionStyle then invalidStyle,
                            style,
                        ),
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
    val labelTarget = remember { FocusRequester() }
    var localChecked by remember { mutableStateOf(defaultChecked) }
    val value = checked ?: localChecked
    val update: ((Boolean) -> Unit)? =
        if (checked == null || onCheckedChange != null) {
            { next ->
                if (checked == null) localChecked = next
                onCheckedChange?.invoke(next)
            }
        } else {
            null
        }
    Row(
        modifier = modifier,
        verticalAlignment =
            if (description != null) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
        Switch(
            checked = value,
            modifier = Modifier.focusRequester(labelTarget),
            onCheckedChange = update,
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
            Label(
                text,
                modifier =
                    Modifier.focusProperties { canFocus = false }.clickable(
                        enabled = resolvedEnabled && update != null,
                    ) {
                        update?.invoke(!value)
                        labelTarget.requestFocus()
                    },
                disabled = !resolvedEnabled,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs2)) {
                FieldLabel(
                    text,
                    modifier =
                        Modifier.focusProperties { canFocus = false }.clickable(
                            enabled = resolvedEnabled && update != null,
                        ) {
                            update?.invoke(!value)
                            labelTarget.requestFocus()
                        },
                    disabled = !resolvedEnabled,
                )
                Text(
                    variant = TextVariant.BodySmall,
                    textStyle =
                        if (currentTheme.activePreset == StylePreset.Nova) {
                            currentTheme.typography.bodyMedium.merge(
                                TextColors.Muted,
                            )
                        } else {
                            TextColors.Muted
                        },
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
