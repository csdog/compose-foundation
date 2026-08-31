package com.toutiaoyan.compose.ui.components.button

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
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.LocalTextDecoration
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.buttongroup.raiseFocusedSiblingZIndex
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.text.Text

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
 * Style merge on the painted chrome (later wins): `variant → size → style`.
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
    content: @Composable () -> Unit,
) {
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }

    val touchTargetStyle = rememberMinTouchTargetStyle()
    // Link underline: Style `hovered { textDecoration }` is not reliable inside
    // SelectionContainer; drive Text via [LocalTextDecoration] instead (real shadcn:
    // `underline-offset-4 hover:underline`).
    val isHovered by interactionSource.collectIsHoveredAsState()
    val linkDecoration =
        if (variant == ButtonVariant.Link && isHovered) TextDecoration.Underline else null
    val resolvedIndication = indication ?: LocalIndication.current

    // Outer: hit testing + theme minTouchTarget (default 48.dp), indication = null.
    // Inner: painted chrome + press indication (same interactionSource).
    Box(
        modifier =
            modifier
                .raiseFocusedSiblingZIndex()
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    if (!label.isNullOrEmpty()) {
                        contentDescription = label
                    }
                    if (!resolvedEnabled) {
                        disabled()
                    }
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
                        Modifier.pointerHoverIcon(PointerIcon.Hand)
                    } else {
                        Modifier
                    },
                )
                .styleable(styleState, touchTargetStyle),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides variant.iconTint(currentTheme.colors),
            LocalTextDecoration provides linkDecoration,
        ) {
            Box(
                modifier =
                    Modifier
                        .styleable(
                            styleState,
                            variant.rememberStyle() then size.rememberStyle() then style,
                        )
                        .then(
                            if (variant == ButtonVariant.Link) {
                                Modifier
                            } else {
                                Modifier.indication(interactionSource, resolvedIndication)
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    content()
                }
            }
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
    ) {
        Text { text }
    }
}
