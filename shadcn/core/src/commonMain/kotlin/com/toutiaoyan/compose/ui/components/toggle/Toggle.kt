package com.toutiaoyan.compose.ui.components.toggle
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.buttongroup.raiseFocusedSiblingZIndex
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

class ToggleState(val pressed: Boolean)

private val LocalToggle = staticCompositionLocalOf<ToggleState?> { null }

/** Reads the nearest [Toggle]. */
@Composable
fun toggleState(): ToggleState =
    checkNotNull(LocalToggle.current) {
        "toggleState() must be used inside Toggle { … }"
    }

/**
 * A two-state button, e.g. for toolbar toggles (bold/italic). Distinct from
 * [com.toutiaoyan.compose.ui.components.switch.Switch] (on/off preference) and
 * [com.toutiaoyan.compose.ui.components.chip.Chip] (pill-shaped filter tag).
 *
 * Its focus ring automatically follows whatever shape [com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup]
 * (or another grouped container) overrides via its own `style` parameter -- `dropShadow` always
 * follows the final resolved `shape()`, not just whatever this composable's own
 * variant style declares.
 *
 * Usage:
 * ```
 * var bold by remember { mutableStateOf(false) }
 * Toggle("B", pressed = bold, onPressedChange = { bold = it })
 * Toggle("B", defaultPressed = true)
 * Toggle("B", pressed = bold, onPressedChange = { bold = it }, size = ToggleSize.Sm)
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Toggle(
    pressed: Boolean? = null,
    onPressedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ToggleVariant = ToggleVariant.Default,
    size: ToggleSize = ToggleSize.Md,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    defaultPressed: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    var internalPressed by remember { mutableStateOf(defaultPressed) }
    val isPressed = pressed ?: internalPressed
    val setPressed: ((Boolean) -> Unit)? =
        if (pressed == null || onPressedChange != null) {
            { next ->
                if (pressed == null) internalPressed = next
                onPressedChange?.invoke(next)
            }
        } else {
            null
        }
    val interactionSource = remember { MutableInteractionSource() }
    val pressStyle = rememberPressMotionStyle(animation)
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isChecked = isPressed
            it.isEnabled = resolvedEnabled
        }

    Box(
        modifier =
            modifier
                .raiseFocusedSiblingZIndex()
                .semantics(mergeDescendants = true) {
                    if (label.isNotEmpty()) {
                        contentDescription = label
                    }
                    if (!resolvedEnabled) {
                        disabled()
                    }
                }
                .keyboardInputMode()
                .toggleable(
                    value = isPressed,
                    onValueChange = { setPressed?.invoke(it) },
                    enabled = resolvedEnabled && setPressed != null,
                    role = if (currentTheme.activePreset == StylePreset.Nova) Role.Button else Role.Checkbox,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        val chrome = Modifier.styleable(styleState, variant.rememberStyle() then size.rememberStyle(), style)
        val themedContent: @Composable () -> Unit = {
            CompositionLocalProvider(
                LocalContentColor provides variant.contentColor(currentTheme.colors),
                LocalToggle provides ToggleState(isPressed),
            ) {
                if (currentTheme.activePreset == StylePreset.Nova) {
                    CompositionLocalProvider(
                        LocalDefaultTextStyle provides
                            currentTheme.typography.bodyMedium.copy(
                                fontWeight =
                                    FontWeight.Medium,
                                fontSize =
                                    if (size == ToggleSize.Sm) {
                                        12.8.sp
                                    } else {
                                        14.sp
                                    },
                            ),
                        LocalInlineIconSize provides if (size == ToggleSize.Sm) 14.dp else 16.dp,
                    ) { content() }
                } else {
                    content()
                }
            }
        }
        if (currentTheme.activePreset == StylePreset.Nova) {
            Row(
                chrome,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                themedContent()
            }
        } else {
            Box(chrome, contentAlignment = Alignment.Center) { themedContent() }
        }
    }
}

/** String-label overload of [Toggle]. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Toggle(
    text: String,
    pressed: Boolean? = null,
    onPressedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ToggleVariant = ToggleVariant.Default,
    size: ToggleSize = ToggleSize.Md,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = text,
    defaultPressed: Boolean = false,
    style: Style = Style,
) {
    Toggle(
        pressed = pressed,
        onPressedChange = onPressedChange,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        animation = animation,
        label = label,
        defaultPressed = defaultPressed,
        style = style,
    ) {
        Text { text }
    }
}

/** Additive invalid-state entry point; the original public signatures remain intact. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Toggle(
    pressed: Boolean? = null,
    onPressedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ToggleVariant = ToggleVariant.Default,
    size: ToggleSize = ToggleSize.Md,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    label: String = "",
    defaultPressed: Boolean = false,
    style: Style = Style,
    invalid: Boolean,
    content: @Composable () -> Unit,
) {
    val theme = currentTheme
    val invalidStyle =
        if (invalid) {
            Style {
                borderColor(theme.colors.destructive)
                dropShadow(
                    androidx.compose.ui.graphics.shadow.Shadow(
                        radius = 0.dp,
                        spread = 3.dp,
                        color = theme.colors.destructive.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
                    ),
                )
            }
        } else {
            Style
        }
    Toggle(
        pressed, onPressedChange,
        modifier.semantics {
            if (invalid) error("Invalid selection")
        },
        enabled, disabled, variant, size, animation, label, defaultPressed, invalidStyle then style, content,
    )
}
