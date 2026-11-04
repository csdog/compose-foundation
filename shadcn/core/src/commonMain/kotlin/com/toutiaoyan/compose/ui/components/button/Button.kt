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
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.then
import androidx.compose.foundation.style.contentPadding
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.utils.rememberTheme
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
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.ui.styles.border
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.tokens.StylePreset
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
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }

    val theme = currentTheme
    val isNova = theme.activePreset == StylePreset.Nova
    val labelStyle =
        if (isNova) {
            theme.typography.labelLarge.copy(
                fontFamily = theme.fontFamily,
                fontSize =
                    when (size) {
                        ButtonSize.Xs -> 12.sp
                        ButtonSize.Sm -> 12.8.sp
                        else -> 14.sp
                    },
                lineHeight = if (size == ButtonSize.Xs) 16.sp else 20.sp,
                lineHeightStyle =
                    LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None,
                        mode = LineHeightStyle.Mode.Tight,
                    ),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
            )
        } else {
            TextStyle.Default
        }
    val minTouchTarget = theme.minTouchTarget
    // Link underline: Style `hovered { textDecoration }` is not reliable inside
    // SelectionContainer; drive Text via [LocalTextDecoration] instead (real shadcn:
    // `underline-offset-4 hover:underline`).
    val isHovered by interactionSource.collectIsHoveredAsState()
    val linkDecoration =
        if (variant == ButtonVariant.Link && isHovered) TextDecoration.Underline else null
    val resolvedIndication = indication ?: LocalIndication.current
    val clipIndication = (resolvedIndication as? RippleIndication)?.bounded != false
    val indicationShape = remember { mutableStateOf<Shape>(RectangleShape) }
    val direction = LocalLayoutDirection.current
    val iconStyle =
        rememberTheme(size, iconPlacement, direction) {
            val normal =
                when (size) {
                    ButtonSize.Xs -> spacing.scale2
                    ButtonSize.Sm, ButtonSize.Md, ButtonSize.Lg -> spacing.scale2p5
                    else -> null
                }
            if (!isNova || iconPlacement == null || normal == null) {
                Style
            } else {
                val icon = if (size == ButtonSize.Xs || size == ButtonSize.Sm) spacing.scale1p5 else spacing.scale2
                val start = if (iconPlacement != ButtonIconPlacement.End) icon else normal
                val end = if (iconPlacement != ButtonIconPlacement.Start) icon else normal
                Style {
                    // Foundation scalar padding places physical edges; map logical edges once.
                    contentPadding(
                        start = if (direction == LayoutDirection.Ltr) start else end,
                        top = 0.dp,
                        end = if (direction == LayoutDirection.Ltr) end else start,
                        bottom = 0.dp,
                    )
                }
            }
        }
    val invalidStyle =
        rememberTheme(invalid) {
            if (!invalid) {
                Style
            } else {
                Style {
                    border(colors.destructive.copy(alpha = if (isDark) 0.5f else 1f))
                    if (ring.enabled) {
                        dropShadow(
                            Shadow(
                                radius = 0.dp,
                                spread = ring.width + ring.offset,
                                color = colors.destructive.copy(alpha = if (isDark) 0.4f else 0.2f),
                            ),
                        )
                    }
                }
            }
        }
    val chromeStyle =
        (variant.rememberStyle() then size.rememberStyle() then iconStyle then invalidStyle then style).captureShape(
            indicationShape,
        )

    // Outer: hit testing + theme minTouchTarget (default 48.dp), indication = null.
    // Inner: painted chrome + press indication (same interactionSource).
    Layout(
        modifier =
            modifier
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
                )
                .then(
                    if (resolvedEnabled) {
                        Modifier.pointerHoverIcon(if (isNova) PointerIcon.Default else PointerIcon.Hand)
                    } else {
                        Modifier
                    },
                ),
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
                    } else {
                        null
                    },
                LocalTextDecoration provides linkDecoration,
            ) {
                Box(
                    modifier =
                        Modifier
                            .buttonCssStyleable(
                                styleState,
                                chromeStyle,
                                nova = isNova,
                            )
                            .then(
                                if (variant == ButtonVariant.Link) {
                                    Modifier
                                } else {
                                    // Keep focus rings and shadows on the outer chrome; clip only feedback/content.
                                    Modifier.graphicsLayer {
                                        shape = indicationShape.value
                                        clip = clipIndication
                                    }.indication(interactionSource, resolvedIndication)
                                },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                if (isNova && (size == ButtonSize.Xs || size == ButtonSize.Sm)) 4.dp else 6.dp,
                                Alignment.CenterHorizontally,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        content()
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
        Text(maxLines = 1) { text }
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
