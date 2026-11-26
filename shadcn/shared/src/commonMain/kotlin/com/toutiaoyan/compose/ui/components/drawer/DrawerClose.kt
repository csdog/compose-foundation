@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.icons.X
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Host slot for a dismiss control. Matches Base UI / shadcn `DrawerClose`.
 *
 * Web uses `render={<Button />}`; Compose uses **children** — put the host
 * inside and wire [DrawerScope.close] (no extra clickable wrapper):
 * ```
 * DrawerClose {
 *     Button("Cancel", onClick = close, variant = ButtonVariant.Outline)
 * }
 * ```
 */
@Composable
fun DrawerClose(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
    }
}

/** Convenience: outline [Button] labeled [text], wired to dismiss. */
@Composable
fun DrawerClose(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
) {
    val state = LocalDrawerState.current
    Button(
        text,
        onClick = state::close,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
    )
}

private const val DRAWER_DISABLED_CLOSE_ALPHA = 0.5f

/**
 * Icon-only X control. Prefer labeled [DrawerClose] (children / text) for base-nova
 * footer Cancel patterns; this matches older Sheet-like chrome.
 */
@Composable
fun DrawerCloseIcon(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    ariaLabel: String = "Close",
) {
    val state = LocalDrawerState.current
    val resolvedEnabled = !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val closeButtonStyle =
        remember {
            Style {
                focusRing(RoundedCornerShape(twRadius.sm))
            }
        }
    Box(
        modifier =
            modifier
                .then(if (resolvedEnabled) Modifier else Modifier.alpha(DRAWER_DISABLED_CLOSE_ALPHA))
                .semantics {
                    role = Role.Button
                    contentDescription = ariaLabel
                    if (!resolvedEnabled) disabled()
                }
                .styleable(styleState, touchTargetStyle then closeButtonStyle)
                .keyboardInputMode()
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = state::close,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    }
}
