@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.icon

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

/** Inline controls can size default icons; an explicit Icon variant/style still wins. */
internal val LocalInlineIconSize = staticCompositionLocalOf<Dp?> { null }

/**
 * Size variants for [Icon], keyed off theme [com.toutiaoyan.compose.ui.tokens.IconStyles]
 * (`standardSize` / `smallSize`) the same way [com.toutiaoyan.compose.ui.components.button.ButtonSize]
 * maps onto fixed dp boxes.
 */
sealed interface IconVariant {
    /** Inline control default, otherwise theme `icons.standardSize`. */
    data object Default : IconVariant

    /** Theme `icons.smallSize`. */
    data object Small : IconVariant
}

@Composable
fun IconVariant.rememberStyle(): Style {
    val receiver = this
    val inlineSize = LocalInlineIconSize.current
    return rememberTheme(receiver, inlineSize) {
        val iconSize =
            when (receiver) {
                IconVariant.Default -> inlineSize ?: icons.standardSize
                IconVariant.Small -> icons.smallSize
            }
        Style { size(iconSize) }
    }
}
