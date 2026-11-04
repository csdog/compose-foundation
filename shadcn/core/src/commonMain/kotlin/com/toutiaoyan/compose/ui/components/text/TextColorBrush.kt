@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.text

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified

/** A text paint whose currentColor is resolved at the actual glyph draw boundary. */
internal interface TextColorBrush {
    val fallbackTextColor: Color get() = Color.Unspecified

    fun resolveTextColor(color: Color): Brush

    fun withFallbackTextColor(color: Color): Brush
}

/** Foundation's contentBrush clears contentColor on the same node. Preserve a preceding
 * color as the brush's fallback, while allowing a nearer explicit text color to override it.
 */
internal fun Style.preserveTextCurrentColor(): Style {
    if (this === Style) return this
    val source = this
    return Style {
        val target = this
        var ownColor = Color.Unspecified
        val scope =
            object : StyleScope by target {
                override fun contentColor(value: Color) {
                    ownColor = value
                    target.contentColor(value)
                }

                override fun contentBrush(value: Brush) {
                    target.contentBrush(
                        if (value is TextColorBrush &&
                            ownColor.isSpecified
                        ) {
                            value.withFallbackTextColor(ownColor)
                        } else {
                            value
                        },
                    )
                }
            }
        with(source) { scope.applyStyle() }
    }
}
