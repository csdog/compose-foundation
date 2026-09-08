@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.item
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

/** Visual variants for [com.toutiaoyan.compose.ui.components.item.Item]. */
enum class ItemVariant { Default, Outline, Muted }

enum class ItemSize { Default, Sm, Xs }

/**
 * Visual styles for [com.toutiaoyan.compose.ui.components.item.Item].
 *
 * Padding / shape / border / background live in Style so callers can override via
 * `style = Style { contentPadding(...) }` (`then` merges by property).
 */
@Composable
fun ItemVariant.rememberStyle(size: ItemSize = ItemSize.Default): Style {
    val receiver = this
    return rememberTheme(receiver, size) {
        val padH =
            when (size) {
                ItemSize.Default -> spacing.lg
                ItemSize.Sm, ItemSize.Xs -> spacing.md
            }
        val padV =
            when (size) {
                ItemSize.Default -> spacing.lg
                ItemSize.Sm -> spacing.md
                ItemSize.Xs -> spacing.sm
            }
        when (receiver) {
            ItemVariant.Default ->
                Style {
                    width(1f)
                    shape(RoundedCornerShape(radius.md))
                    contentPadding(horizontal = padH, vertical = padV)
                }

            ItemVariant.Outline ->
                Style {
                    width(1f)
                    shape(RoundedCornerShape(radius.md))
                    border(colors.border)
                    contentPadding(horizontal = padH, vertical = padV)
                }

            ItemVariant.Muted ->
                Style {
                    width(1f)
                    shape(RoundedCornerShape(radius.md))
                    background(colors.muted)
                    contentPadding(horizontal = padH, vertical = padV)
                }
        }
    }
}

@Composable
fun ItemSize.itemGap(): Dp =
    when (this) {
        ItemSize.Default, ItemSize.Sm -> twSpacing.md
        ItemSize.Xs -> twSpacing.sm
    }
