package com.toutiaoyan.compose.ui.components.toggle
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.buttongroup.raiseFocusedSiblingZIndex
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.overlay.captureTouchCanvasFocus
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

class ToggleState(val pressed: Boolean)

private val LocalToggle = staticCompositionLocalOf<ToggleState?> { null }

private class ToggleTouchFocus {
    var restoreCanvasFocus: (() -> Unit)? = null
}

private fun Modifier.trackToggleTouch(state: ToggleTouchFocus): Modifier =
    pointerInput(state) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val restore = if (down.type == PointerType.Touch) captureTouchCanvasFocus(currentEvent) else null
            if (restore != null && waitForUpOrCancellation(pass = PointerEventPass.Initial) != null) {
                // Expose the callback to toggleable only during the release's Main pass.
                state.restoreCanvasFocus = restore
                try {
                    awaitPointerEvent(PointerEventPass.Final)
                } finally {
                    state.restoreCanvasFocus = null
                }
            }
        }
    }

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
 * Nova has no implicit press transform; other presets retain the legacy scale.
 * Callers can explicitly opt into any [ButtonAnimation] for every preset.
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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    label: String = "",
    defaultPressed: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
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
        content = content,
        iconPlacement = null,
    )
}

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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    label: String = "",
    defaultPressed: Boolean = false,
    style: Style = Style,
    iconPlacement: ToggleIconPlacement?,
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
    val canToggle = resolvedEnabled && setPressed != null
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val touchFocus = remember { ToggleTouchFocus() }
    val touchFocusModifier = if (canToggle) Modifier.trackToggleTouch(touchFocus) else Modifier
    val pressStyle = rememberPressMotionStyle(animation)
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState =
        remember(interactionSource) {
            MutableStyleState(interactionSource).apply { isChecked = isPressed }
        }
    styleState.isEnabled = resolvedEnabled
    // Foundation 1.12.2 loses checked transitions when its snapshot flags are written
    // in the same composition that updates the styled node. Publish later, like
    // interaction-source events, while semantics and callbacks keep the current value.
    LaunchedEffect(styleState, isPressed) { styleState.isChecked = isPressed }

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
                .then(touchFocusModifier)
                .focusRequester(focusRequester)
                .toggleable(
                    value = isPressed,
                    onValueChange = {
                        touchFocus.restoreCanvasFocus?.let { restore ->
                            restore()
                            focusRequester.requestFocus()
                        }
                        setPressed?.invoke(it)
                    },
                    enabled = canToggle,
                    role = if (currentTheme.activePreset == StylePreset.Nova) Role.Button else Role.Checkbox,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        val chrome =
            Modifier.cssStyleable(
                styleState,
                variant.rememberStyle() then size.rememberStyle(iconPlacement),
                style,
            )
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
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    label: String = text,
    defaultPressed: Boolean = false,
    style: Style = Style,
) {
    Toggle(
        text = text,
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
        iconPlacement = null,
    )
}

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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    label: String = text,
    defaultPressed: Boolean = false,
    style: Style = Style,
    iconPlacement: ToggleIconPlacement?,
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
        iconPlacement = iconPlacement,
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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    label: String = "",
    defaultPressed: Boolean = false,
    style: Style = Style,
    invalid: Boolean,
    content: @Composable () -> Unit,
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
        invalid = invalid,
        content = content,
        iconPlacement = null,
    )
}

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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    label: String = "",
    defaultPressed: Boolean = false,
    style: Style = Style,
    invalid: Boolean,
    iconPlacement: ToggleIconPlacement?,
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
        pressed = pressed,
        onPressedChange = onPressedChange,
        modifier =
            modifier.semantics {
                if (invalid) error("Invalid selection")
            },
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        animation = animation,
        label = label,
        defaultPressed = defaultPressed,
        style = invalidStyle then style,
        iconPlacement = iconPlacement,
        content = content,
    )
}
