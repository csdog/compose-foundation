package com.toutiaoyan.compose.ui.components.chip
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * Selectable chip / filter tag.
 *
 * The leading icon receives the current theme's standard icon size.
 * [selected] is the caller-owned checked state.
 *
 * Usage:
 * ```
 * Chip(label = "Kotlin", selected = true, onClick = { toggle() })
 * Chip(label = "Swift", variant = ChipVariant.Outline, onClick = {})
 * Chip(label = "Verified", leadingIcon = { modifier -> Icon(CheckIcon, modifier = modifier) })
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Chip(
    label: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ChipVariant = if (selected) ChipVariant.Selected else ChipVariant.Default,
    leadingIcon: (@Composable (Modifier) -> Unit)? = null,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    style: Style = Style,
) {
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val baseVariantStyle = variant.rememberStyle()
    val pressStyle = rememberPressMotionStyle(animation)
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
            it.isSelected = selected
        }

    val clickableModifier =
        if (onClick != null) {
            Modifier.keyboardInputMode().clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = resolvedEnabled,
                role = Role.Checkbox,
                onClick = onClick,
            )
        } else {
            Modifier
        }

    val theme = Theme.current

    Box(
        modifier =
            modifier
                .semantics(mergeDescendants = true) {
                    role = Role.Checkbox
                    contentDescription = label
                    this.selected = selected
                    toggleableState = ToggleableState(selected)
                    if (!resolvedEnabled) {
                        disabled()
                    }
                }
                .then(clickableModifier)
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.styleable(styleState, baseVariantStyle, style),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(twSpacing.xs2),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides variant.contentColor(currentTheme.colors),
            ) {
                if (leadingIcon != null) {
                    leadingIcon(Modifier.size(theme.icons.standardSize))
                }
                Text { label }
            }
        }
    }
}
