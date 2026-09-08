@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.combobox

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing

@Composable
fun rememberComboboxPanelStyle(): Style =
    rememberTheme {
        Style {
            width(200.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.popover)
            border(colors.border)
            contentPadding(spacing.sm)
        }
    }

@Composable
fun rememberComboboxTriggerStyle(): Style =
    rememberTheme {
        Style {
            width(200.dp)
        }
    }

@Composable
fun rememberComboboxChipsStyle(
    focused: Boolean,
    invalid: Boolean,
): Style =
    rememberTheme(focused, invalid) {
        val stroke =
            when {
                invalid -> colors.destructive
                focused -> colors.ring
                else -> colors.border
            }
        Style {
            width(1f)
            // base-nova `.cn-combobox-chips`: `min-h-8 rounded-lg px-2.5 py-1`
            minHeight(32.dp)
            shape(RoundedCornerShape(radius.lg))
            background(colors.background)
            border(stroke)
            contentPadding(horizontal = 2.5.spacing.dp, vertical = 1.spacing.dp)
            if (focused) dropShadow(focusRingShadow())
        }
    }

@Composable
fun rememberComboboxChipStyle(): Style =
    rememberTheme {
        Style {
            height(5.5.spacing.dp)
            shape(RoundedCornerShape(radius.sm))
            background(colors.muted)
            contentPadding(horizontal = 1.5.spacing.dp, vertical = 0.dp)
        }
    }

/** Official `autoHighlight` — same muted fill as a hovered menu item. */
@Composable
fun rememberComboboxHighlightedItemStyle(): Style =
    rememberTheme {
        Style {
            background(colors.muted)
        }
    }
