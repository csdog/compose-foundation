@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.inputgroup

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.input.NovaInputColorDurationMillis
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

// base-nova input-group fill and disabled opacity.
private const val NOVA_DISABLED_DARK_FILL_ALPHA = 0.8f
private const val NOVA_DISABLED_LIGHT_FILL_ALPHA = 0.5f
private const val NOVA_DARK_FILL_ALPHA = 0.3f
private const val NOVA_DISABLED_GROUP_ALPHA = 0.5f

/**
 * Set by [InputGroup] so an inner [com.toutiaoyan.compose.ui.components.input.Input]
 * (or Textarea) knows the group container owns the border and focus ring, and
 * suppresses its own — same contract as real shadcn's `InputGroupInput`
 * (`border-0 focus-visible:ring-0`). [InputGroupInput] / [InputGroupTextarea] are
 * name-parity wrappers; composing [Input] / [Textarea] directly still works.
 */
internal val LocalInsideInputGroup = compositionLocalOf { false }

internal val LocalInputGroupDisabled = compositionLocalOf { false }

internal class InputGroupLayoutState {
    val addons = mutableStateMapOf<Any, InputGroupAddonAlign>()
    val controls = mutableStateMapOf<Any, Pair<Boolean, Boolean>>() // invalid, enabled
    val focusedAddons = mutableStateMapOf<Any, Boolean>()
    val requesters = mutableMapOf<Any, FocusRequester>()

    fun focusInput() {
        requesters.entries.firstOrNull { controls[it.key]?.second == true }?.value?.requestFocus()
    }
}

internal val LocalInputGroupLayout = staticCompositionLocalOf<InputGroupLayoutState?> { null }

@Immutable
class InputGroupState(
    val invalid: Boolean,
    val focused: Boolean,
    val disabled: Boolean = false,
)

private val LocalInputGroup = staticCompositionLocalOf<InputGroupState?> { null }

/** Reads the nearest [InputGroup]. Matches shadcn input-group context. */
@Composable
fun inputGroupState(): InputGroupState =
    checkNotNull(LocalInputGroup.current) {
        "inputGroupState() must be used inside InputGroup { … }"
    }

/** Real shadcn `InputGroupAddon` `align` values. */
enum class InputGroupAddonAlign {
    /** `align="inline-start"` — leading side of the control. */
    InlineStart,

    /** `align="inline-end"` — trailing side of the control. */
    InlineEnd,

    /** `align="block-start"` — full-width row above the control. */
    BlockStart,

    /** `align="block-end"` — full-width row below the control (chat composer toolbar). */
    BlockEnd,
}

/**
 * Bordered container matching real shadcn/ui `input-group.tsx`.
 *
 * Composition (same as shadcn docs):
 * ```
 * InputGroup
 * ├── InputGroupInput / InputGroupTextarea
 * ├── InputGroupAddon(align=…)
 * ├── InputGroupButton
 * └── InputGroupText
 * ```
 *
 * The **container** owns border + focus ring. [InputGroupAddon] children are placed by
 * [InputGroupAddonAlign]; everything else is the control slot (`flex-1`).
 *
 * ```
 * InputGroup {
 *     InputGroupAddon(InputGroupAddonAlign.InlineStart) {
 *         InputGroupText("$")
 *     }
 *     Input(value = amount, onValueChange = { amount = it })
 * }
 *
 * InputGroup {
 *     Input(value = msg, onValueChange = { msg = it }, singleLine = false)
 *     InputGroupAddon(InputGroupAddonAlign.BlockEnd) {
 *         InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconSm) { … }
 *         Spacer(Modifier.weight(1f))
 *         InputGroupButton(onClick = ::send, size = InputGroupButtonSize.IconSm) { … }
 *     }
 * }
 * ```
 */
@Composable
fun InputGroup(
    modifier: Modifier = Modifier,
    invalid: Boolean = false,
    style: Style = Style,
    disabled: Boolean = false,
    contentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    var hasFocusWithin by remember { mutableStateOf(false) }

    // colors/shapes read here — not inside Style{} (stale CompositionLocal snapshot).
    val theme = currentTheme
    val colors = theme.colors
    val nova = theme.activePreset == StylePreset.Nova
    val layoutState = remember { InputGroupLayoutState() }
    val hasControlFocus = hasFocusWithin && layoutState.focusedAddons.values.none { it }
    val resolvedDisabled = disabled || LocalFieldSetDisabled.current
    val groupDisabled = resolvedDisabled || layoutState.controls.values.any { !it.second }
    val groupInvalid = invalid || layoutState.controls.values.any { it.first }
    val shapes = twRadius
    val borderColor =
        when {
            groupInvalid -> colors.destructive
            hasControlFocus -> colors.ring
            else -> if (nova) colors.input else colors.border
        }

    val tint =
        when {
            groupDisabled -> if (theme.isDark) NOVA_DISABLED_DARK_FILL_ALPHA else NOVA_DISABLED_LIGHT_FILL_ALPHA
            theme.isDark -> NOVA_DARK_FILL_ALPHA
            else -> 0f
        }
    val border by if (nova) {
        animateColorAsState(borderColor, tween(NovaInputColorDurationMillis))
    } else {
        rememberUpdatedState(borderColor)
    }
    val fill = if (nova) colors.input.copy(alpha = colors.input.alpha * tint) else colors.background
    val background by if (nova) {
        animateColorAsState(fill, tween(NovaInputColorDurationMillis))
    } else {
        rememberUpdatedState(fill)
    }

    val containerStyle =
        Style {
            width(1f)
            // base-nova `.cn-input-group`: `h-8 rounded-lg`
            // (`h-auto` when block addons present — minHeight lets Layout grow)
            minHeight(InputGroupMinHeight)
            background(background)
            borderColor(border)
            borderWidth(InputGroupBorderWidth)
            shape(RoundedCornerShape(shapes.lg))
            if (hasControlFocus) dropShadow(theme.focusRingShadow())
            if (nova && groupInvalid) {
                dropShadow(
                    Shadow(
                        radius = 0.dp,
                        spread = 3.dp,
                        color = colors.destructive.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
                    ),
                )
            }
        }

    Layout(
        modifier =
            modifier
                .semantics {
                    val label = contentDescription
                    if (label != null) this.contentDescription = label
                }
                .onFocusEvent { hasFocusWithin = it.hasFocus }
                .alpha(if (nova && groupDisabled) NOVA_DISABLED_GROUP_ALPHA else 1f)
                .cssStyleable(styleState, containerStyle then style),
        content = {
            CompositionLocalProvider(
                LocalInsideInputGroup provides true,
                LocalInputGroupDisabled provides resolvedDisabled,
                LocalInputGroupLayout provides layoutState,
                LocalInputGroup provides
                    InputGroupState(
                        invalid = groupInvalid,
                        focused = hasControlFocus,
                        disabled = groupDisabled,
                    ),
            ) {
                content()
            }
        },
    ) { measurables, constraints ->
        measureInputGroup(measurables, constraints, nova)
    }
}

private class InputGroupAddonParentDataModifier(
    private val align: InputGroupAddonAlign,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = InputGroupAddonParentData(align)
}

/**
 * Real shadcn `InputGroupAddon` — icons, text, or buttons beside / above / below the control.
 * Call order is free; [align] sets visual position (Compose has no CSS `order`).
 */
@Composable
fun InputGroupAddon(
    align: InputGroupAddonAlign = InputGroupAddonAlign.InlineStart,
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val theme = currentTheme
    val muted = theme.colors.mutedForeground
    val nova = theme.activePreset == StylePreset.Nova
    val layoutState = LocalInputGroupLayout.current
    val key = remember { Any() }
    DisposableEffect(layoutState, key) {
        onDispose {
            layoutState?.addons?.remove(key)
            layoutState?.focusedAddons?.remove(key)
        }
    }
    SideEffect { layoutState?.addons?.set(key, align) }
    val spacing = twSpacing
    // Fit a 24 dp Nova inline button inside the 30 dp inner row.
    val inlineVerticalPadding = if (nova) 3.dp else 6.dp
    val pad =
        remember(align, nova, spacing) {
            Style {
                when (align) {
                    InputGroupAddonAlign.InlineStart ->
                        contentPadding(
                            start = if (nova) 8.dp else spacing.md,
                            end = if (nova) 0.dp else spacing.xs,
                            top = inlineVerticalPadding,
                            bottom = inlineVerticalPadding,
                        )

                    InputGroupAddonAlign.InlineEnd ->
                        contentPadding(
                            start = if (nova) 0.dp else spacing.xs,
                            end = if (nova) 8.dp else spacing.md,
                            top = inlineVerticalPadding,
                            bottom = inlineVerticalPadding,
                        )

                    InputGroupAddonAlign.BlockStart ->
                        contentPadding(
                            start = if (nova) 10.dp else spacing.md,
                            end = if (nova) 10.dp else spacing.md,
                            top = if (nova) 8.dp else spacing.md,
                            bottom = if (nova) 6.dp else spacing.xs,
                        )

                    InputGroupAddonAlign.BlockEnd ->
                        contentPadding(
                            start = if (nova) 10.dp else spacing.md,
                            end = if (nova) 10.dp else spacing.md,
                            top = if (nova) 6.dp else spacing.xs,
                            bottom = if (nova) 8.dp else spacing.md,
                        )
                }
            }
        }
    val interaction = remember { MutableInteractionSource() }
    val enabled = !LocalInputGroupDisabled.current
    val styleState = rememberUpdatedStyleState(interaction) { it.isEnabled = enabled }
    val widthMod =
        if (align == InputGroupAddonAlign.BlockStart || align == InputGroupAddonAlign.BlockEnd) {
            Modifier.fillMaxWidth()
        } else {
            Modifier
        }

    val addonTextStyle =
        theme.typography.bodyMedium.copy(
            fontFamily = theme.fontFamily,
            color = muted,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )
    CompositionLocalProvider(
        LocalContentColor provides muted,
        LocalInlineIconSize provides if (nova) 16.dp else LocalInlineIconSize.current,
        LocalDefaultTextStyle provides if (nova) addonTextStyle else LocalDefaultTextStyle.current,
    ) {
        Row(
            modifier =
                modifier
                    .then(InputGroupAddonParentDataModifier(align))
                    .then(widthMod)
                    // The group ring follows editable control focus, not addon buttons.
                    .onFocusEvent { layoutState?.focusedAddons?.set(key, it.hasFocus) }
                    .focusProperties { canFocus = false }
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        enabled = enabled,
                    ) { layoutState?.focusInput() }
                    .styleable(styleState, pad, style),
            verticalAlignment = Alignment.CenterVertically,
            // gap-2
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            content = content,
        )
    }
}

/** String-label overload of [InputGroupAddon], wrapping [InputGroupText]. */
@Composable
fun InputGroupAddon(
    text: String,
    align: InputGroupAddonAlign = InputGroupAddonAlign.InlineStart,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    InputGroupAddon(align = align, modifier = modifier, style = style) {
        InputGroupText(text)
    }
}

/** Real shadcn `InputGroupButton` sizes (`xs` / `sm` / `icon-xs` / `icon-sm`). */
enum class InputGroupButtonSize {
    Xs,
    Sm,
    IconXs,
    IconSm,
}

/**
 * Real shadcn `InputGroupButton` — defaults to Ghost + Xs (compact chrome inside an addon).
 */
@Composable
fun InputGroupButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: InputGroupButtonSize = InputGroupButtonSize.Xs,
    label: String? = null,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val nova = currentTheme.activePreset == StylePreset.Nova
    val radius = twRadius
    val groupButtonStyle =
        if (nova) {
            Style {
                when (size) {
                    InputGroupButtonSize.Xs -> {
                        height(24.dp)
                        contentPadding(horizontal = 6.dp, vertical = 0.dp)
                        shape(RoundedCornerShape((radius.lg - 3.dp).coerceAtLeast(0.dp)))
                    }
                    InputGroupButtonSize.Sm -> height(28.dp)
                    InputGroupButtonSize.IconXs -> {
                        width(24.dp)
                        height(24.dp)
                        contentPadding(0.dp)
                        shape(RoundedCornerShape((radius.lg - 3.dp).coerceAtLeast(0.dp)))
                    }
                    InputGroupButtonSize.IconSm -> {
                        width(32.dp)
                        height(32.dp)
                        contentPadding(0.dp)
                    }
                }
            }
        } else {
            Style
        }
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled || LocalInputGroupDisabled.current,
        variant = variant,
        size =
            if (nova) {
                ButtonSize.Md
            } else {
                when (size) {
                    InputGroupButtonSize.Xs -> ButtonSize.Xs
                    InputGroupButtonSize.Sm -> ButtonSize.Sm
                    InputGroupButtonSize.IconXs -> ButtonSize.IconXs
                    InputGroupButtonSize.IconSm -> ButtonSize.IconSm
                }
            },
        label = label,
        style = groupButtonStyle then style,
        content = content,
    )
}

/** String-label overload of [InputGroupButton]. */
@Composable
fun InputGroupButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: InputGroupButtonSize = InputGroupButtonSize.Xs,
    style: Style = Style,
) {
    InputGroupButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        label = text,
        style = style,
    ) {
        Text { text }
    }
}

@Composable
fun InputGroupText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val theme = currentTheme
    if (theme.activePreset == StylePreset.Nova) {
        Text(
            modifier = modifier,
            textStyle =
                theme.typography.bodyMedium.copy(
                    fontFamily = theme.fontFamily,
                    color = theme.colors.mutedForeground,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
        ) {
            text
        }
    } else {
        Text(modifier = modifier, variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { text }
    }
}

/** Composable overload for icon + text (or other) addon chrome. */
@Composable
fun InputGroupText(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}

/**
 * Name-parity wrapper for [Input] inside [InputGroup]. Matches shadcn's
 * `InputGroupInput` (`border-0` comes from [LocalInsideInputGroup]).
 */
@Composable
fun InputGroupInput(
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
) {
    Input(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        readOnly = readOnly,
        type = type,
        placeholder = placeholder,
        isError = isError,
        invalid = invalid,
    )
}

/**
 * Name-parity wrapper for [Textarea] inside [InputGroup]. Matches shadcn's
 * `InputGroupTextarea`.
 */
@Composable
fun InputGroupTextarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    readOnly: Boolean = false,
    placeholder: String? = null,
    isError: Boolean = false,
    invalid: Boolean = false,
    rows: Int? = null,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    Textarea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        readOnly = readOnly,
        placeholder = placeholder,
        isError = isError,
        invalid = invalid,
        rows = rows,
        minLines = minLines,
        maxLines = maxLines,
    )
}
