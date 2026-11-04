@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.theme.currentTheme

internal val LocalFieldLabelChoice = staticCompositionLocalOf { false }

/** A label wrapping an entire choice field. The caller owns selection and focuses the labeled control. */
@Composable
fun FieldLabel(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    var focused by remember { mutableStateOf(false) }
    val theme = currentTheme
    val focusVisible = focused && LocalInputModeManager.current.inputMode == InputMode.Keyboard
    val enabled = !disabled && !LocalFieldSetDisabled.current
    val state = remember { MutableStyleState(interactions) }
    val colors = theme.colors
    val base =
        Style {
            shape(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            borderWidth(1.dp)
            borderColor(
                if (focusVisible) {
                    colors.ring
                } else if (selected) {
                    colors.primary.copy(alpha = if (theme.isDark) .2f else .3f)
                } else {
                    colors.border
                },
            )
            background(
                if (hovered && enabled) {
                    colors.muted.copy(alpha = .5f)
                } else if (selected) {
                    colors.primary.copy(alpha = if (theme.isDark) .1f else .05f)
                } else {
                    androidx.compose.ui.graphics.Color.Transparent
                },
            )
            if (focusVisible) dropShadow(Shadow(radius = 0.dp, spread = 3.dp, color = colors.ring.copy(alpha = .5f)))
        }
    CompositionLocalProvider(LocalFieldSetDisabled provides !enabled, LocalFieldLabelChoice provides true) {
        Box(
            modifier =
                modifier
                    .onFocusChanged { focused = it.hasFocus }
                    .focusProperties { canFocus = false }
                    .clickable(
                        interactionSource = interactions,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick,
                    )
                    .cssStyleable(state, base, style),
        ) { Box(Modifier.padding(10.dp)) { content() } }
    }
}
