package com.toutiaoyan.compose.ui.components.button

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.modifiers.DefaultRippleIndication
import com.toutiaoyan.compose.ui.modifiers.RippleIndication
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.LocalTextDecoration
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.buttongroup.raiseFocusedSiblingZIndex
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.text.Text

/** Logical icon edges corresponding to shadcn data-icon inline-start/inline-end. */
enum class ButtonIconPlacement { Start, End, Both }

/**
 * shadcn-inspired button.
 *
 * Its focus ring automatically follows whatever shape [ButtonGroup] (or another
 * grouped container) overrides via its own `style` parameter -- `dropShadow` always
 * follows the final resolved `shape()`, not just whatever this composable's own
 * variant style declares.
 *
 * Accessibility / press motion lean on RikkaUi patterns: merged semantics (`Role.Button`,
 * optional [label], disabled), theme [com.toutiaoyan.compose.ui.theme.Theme]
 * `minTouchTarget` (outer hit area; visual chrome stays on the inner box), and press
 * [indication] (parameter, else [LocalIndication]).
 *
 * Usage:
 * ```
 * Button("Click me")
 * Button("Click me", onClick = {})
 * Button("Outline", onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm)
 * Button("01", onClick = {}, size = ButtonSize.Xs, style = Style { height(28.dp) })
 * Button("Full width", onClick = {}, modifier = Modifier.fillMaxWidth(), style = Style { fillWidth() })
 * ```
 *
 * Style merge on the painted chrome (later wins): `variant → size → iconPlacement → style`.
 * Bounded press feedback follows that final shape without clipping the outer focus ring.
 * Press motion is an inner [indication], not a Style, so the outer min-touch
 * hit box does not scale or shift.
 * [modifier] is on the outer min-touch wrapper so weight / align / fillMaxWidth keep
 * working; stretch the chrome with `style = Style { fillWidth() }` (or height overrides
 * via [style]) — same path for every [ButtonSize], no Icon special-case.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = null,
    style: Style = Style,
    iconPlacement: ButtonIconPlacement? = null,
    invalid: Boolean = false,
    content: @Composable () -> Unit,
) {
    ButtonSurface(
        onClick, modifier, enabled, disabled, variant, size, indication, label, style, iconPlacement, invalid,
        interactive = true,
        content = content,
    )
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
internal fun ButtonSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = null,
    style: Style = Style,
    iconPlacement: ButtonIconPlacement? = null,
    invalid: Boolean = false,
    interactive: Boolean,
    reserveMinTouchTarget: Boolean = true,
    content: @Composable () -> Unit,
) {
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }

    val theme = currentTheme
    val isNova = theme.activePreset == StylePreset.Nova
    val isRhea = theme.activePreset == StylePreset.Rhea
    val isRheaOutline = isRhea && variant == ButtonVariant.Outline
    val isRheaSmall = isRhea && size == ButtonSize.Sm
    val isNovaSmall = isNova && (size == ButtonSize.Xs || size == ButtonSize.Sm)
    val labelStyle = buttonLabelStyle(theme, variant, size)
    val minTouchTarget = theme.minTouchTarget.takeIf { reserveMinTouchTarget }
    // Link underline: Style `hovered { textDecoration }` is not reliable inside
    // SelectionContainer; drive Text via [LocalTextDecoration] instead (real shadcn:
    // `underline-offset-4 hover:underline`).
    val isHovered by interactionSource.collectIsHoveredAsState()
    val linkDecoration =
        if (variant == ButtonVariant.Link && isHovered) TextDecoration.Underline else null
    val resolvedIndication =
        indication ?: LocalIndication.current.takeUnless { isRheaOutline && it === DefaultRippleIndication }
    val clipIndication = (resolvedIndication as? RippleIndication)?.bounded != false
    val indicationShape = remember { mutableStateOf<Shape>(RectangleShape) }
    val iconStyle = rememberButtonIconPadding(size, iconPlacement)
    val invalidStyle = rememberButtonInvalidStyle(invalid)
    val chromeStyle =
        (variant.rememberStyle() then size.rememberStyle() then iconStyle then invalidStyle then style).captureShape(
            indicationShape,
        )

    val inputModifier =
        if (interactive) {
            Modifier
                .raiseFocusedSiblingZIndex()
                .keyboardInputMode()
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    if (!label.isNullOrEmpty()) {
                        contentDescription = label
                    }
                    if (!resolvedEnabled) {
                        disabled()
                    }
                    if (invalid) error("Invalid button")
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = onClick,
                ).then(
                    if (resolvedEnabled) {
                        Modifier.pointerHoverIcon(if (isNova) PointerIcon.Default else PointerIcon.Hand)
                    } else {
                        Modifier
                    },
                )
        } else {
            Modifier
        }

    // The outer layout reserves the touch target; the inner chrome paints the indication.
    Layout(
        modifier = modifier.then(inputModifier),
        content = {
            CompositionLocalProvider(
                LocalContentColor provides
                    if (isNova &&
                        variant == ButtonVariant.Destructive
                    ) {
                        theme.colors.destructive
                    } else {
                        variant.iconTint(theme.colors)
                    },
                LocalDefaultTextStyle provides labelStyle,
                LocalInlineIconSize provides
                    if (isNova) {
                        when (size) {
                            ButtonSize.Xs, ButtonSize.IconXs -> 12.dp
                            ButtonSize.Sm -> 14.dp
                            else -> 16.dp
                        }
                    } else if (isRheaSmall) {
                        16.dp
                    } else {
                        null
                    },
                LocalTextDecoration provides linkDecoration,
            ) {
                val feedback =
                    if (variant == ButtonVariant.Link || resolvedIndication == null) {
                        Modifier
                    } else {
                        Modifier.graphicsLayer {
                            shape = indicationShape.value
                            clip = clipIndication
                        }.indication(interactionSource, resolvedIndication)
                    }
                Box(
                    modifier =
                        Modifier
                            .buttonCssStyleable(
                                styleState,
                                chromeStyle,
                                clipBackgroundToPadding = isNova || isRheaOutline,
                            )
                            .then(if (isNova) Modifier else feedback),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                if (isRheaSmall || isNovaSmall) 4.dp else 6.dp,
                                Alignment.CenterHorizontally,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        content()
                    }
                    if (isNova && variant != ButtonVariant.Link) {
                        // CSS buttons allow content overflow. Clip only the decorative feedback;
                        // it neither changes measurement nor adds another interactive target.
                        Box(Modifier.matchParentSize().then(feedback))
                    }
                }
            }
        },
    ) { measurables, constraints ->
        // Only caller constraints reach the chrome. A larger accessibility hit area
        // is added afterwards, so it cannot inflate an icon's painted size.
        val chrome = measurables.single().measure(constraints)
        val minimum = (minTouchTarget?.roundToPx() ?: 0).coerceAtLeast(0)
        val width = constraints.constrainWidth(maxOf(chrome.width, minimum))
        val height = constraints.constrainHeight(maxOf(chrome.height, minimum))
        layout(width, height) {
            chrome.placeRelative((width - chrome.width) / 2, (height - chrome.height) / 2)
        }
    }
}

/** String-label overload of [Button]. Matches shadcn `<Button>Save</Button>`. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = text,
    style: Style = Style,
    iconPlacement: ButtonIconPlacement? = null,
    invalid: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        indication = indication,
        label = label,
        style = style,
        iconPlacement = iconPlacement,
        invalid = invalid,
    ) {
        Text(
            modifier =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Modifier.wrapContentWidth(unbounded = true)
                } else {
                    Modifier
                },
            maxLines = 1,
        ) { text }
    }
}

/** Resolve the same shape cascade as chrome without guessing an ancestor graphics-layer outline. */
@OptIn(ExperimentalFoundationStyleApi::class)
private fun Style.captureShape(result: MutableState<Shape>): Style {
    val original = this
    return Style {
        val target = this
        var resolved: Shape = RectangleShape
        with(original) {
            object : StyleScope by target {
                override fun shape(value: Shape) {
                    resolved = value
                    target.shape(value)
                }
            }.applyStyle()
        }
        // Read only by the inner graphics layer, never by composition or measurement.
        result.value = resolved
    }
}

// Preserve the pre-0.3.31 binary entry points while source calls use the extended APIs.
@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = null,
    style: Style = Style,
    content: @Composable () -> Unit,
) = Button(
    onClick, modifier, enabled, disabled, variant, size, indication, label, style,
    iconPlacement = null, content = content,
)

@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = text,
    style: Style = Style,
) = Button(text, onClick, modifier, enabled, disabled, variant, size, indication, label, style, iconPlacement = null)

// Preserve the 0.3.31 entry points with the icon edge hint.
@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = null,
    style: Style = Style,
    iconPlacement: ButtonIconPlacement? = null,
    content: @Composable () -> Unit,
) = Button(
    onClick, modifier, enabled, disabled, variant, size, indication, label, style, iconPlacement,
    invalid = false, content = content,
)

@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    indication: Indication? = null,
    label: String? = text,
    style: Style = Style,
    iconPlacement: ButtonIconPlacement? = null,
) = Button(
    text, onClick, modifier, enabled, disabled, variant, size, indication, label, style, iconPlacement,
    invalid = false,
)
