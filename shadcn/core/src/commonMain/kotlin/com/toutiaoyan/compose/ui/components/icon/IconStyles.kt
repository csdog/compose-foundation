@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.icon

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.size
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme

/**
 * Size variants for [Icon], keyed off theme [com.toutiaoyan.compose.ui.tokens.IconStyles]
 * (`standardSize` / `smallSize`) the same way [com.toutiaoyan.compose.ui.components.button.ButtonSize]
 * maps onto fixed dp boxes.
 */
sealed interface IconVariant {
    /** Theme `icons.standardSize` (default). */
    data object Default : IconVariant

    /** Theme `icons.smallSize`. */
    data object Small : IconVariant
}

@Composable
fun IconVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            IconVariant.Default ->
                Style {
                    size(icons.standardSize)
                }

            IconVariant.Small ->
                Style {
                    size(icons.smallSize)
                }
        }
    }
}
