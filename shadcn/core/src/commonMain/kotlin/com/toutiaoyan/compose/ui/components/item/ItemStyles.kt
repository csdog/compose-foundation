@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.item

import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.dropdownmenu.LocalDropdownMenuContent
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.focusVisible
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.rememberTheme

enum class ItemVariant { Default, Outline, Muted }

enum class ItemSize { Default, Sm, Xs }

internal const val NOVA_ITEM_COLOR_DURATION_MILLIS = 100
internal const val NOVA_MUTED_ITEM_BACKGROUND_ALPHA = 0.5f
private val NovaItemBorderTransition = tween<Float>(NOVA_ITEM_COLOR_DURATION_MILLIS)

@Composable
fun ItemVariant.rememberStyle(size: ItemSize = ItemSize.Default): Style {
    val variant = this
    val inDropdown = LocalDropdownMenuContent.current
    return rememberTheme(variant, size, inDropdown) {
        val nova = theme.activePreset == StylePreset.Nova
        val compactDropdown = nova && size == ItemSize.Xs && inDropdown
        val padH =
            if (compactDropdown) {
                0.dp
            } else if (nova) {
                if (size == ItemSize.Xs) 10.dp else 12.dp
            } else {
                when (size) {
                    ItemSize.Default -> spacing.lg
                    ItemSize.Sm, ItemSize.Xs -> spacing.md
                }
            }
        val padV =
            if (compactDropdown) {
                0.dp
            } else if (nova) {
                if (size == ItemSize.Xs) 8.dp else 10.dp
            } else {
                when (size) {
                    ItemSize.Default -> spacing.lg
                    ItemSize.Sm -> spacing.md
                    ItemSize.Xs -> spacing.sm
                }
            }
        Style {
            width(1f)
            val corners = RoundedCornerShape(if (nova) radius.lg else radius.md)
            shape(corners)
            if (nova) {
                val outline = if (variant == ItemVariant.Outline) colors.border else Color.Transparent
                border(outline)
                animate(NovaItemBorderTransition) { borderColor(outline) }
                focusRing(corners)
                if (ring.enabled) {
                    focusVisible {
                        animate(NovaItemBorderTransition) { borderColor(colors.ring) }
                    }
                }
            } else if (variant == ItemVariant.Outline) {
                border(colors.border)
            }
            if (variant == ItemVariant.Muted) {
                background(if (nova) colors.muted.copy(alpha = NOVA_MUTED_ITEM_BACKGROUND_ALPHA) else colors.muted)
            }
            contentPadding(horizontal = padH, vertical = padV)
        }
    }
}

@Composable
fun ItemSize.itemGap(): Dp =
    if (currentTheme.activePreset == StylePreset.Nova) {
        if (this == ItemSize.Xs) 8.dp else 10.dp
    } else {
        when (this) {
            ItemSize.Default, ItemSize.Sm -> twSpacing.md
            ItemSize.Xs -> twSpacing.sm
        }
    }
