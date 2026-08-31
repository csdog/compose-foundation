@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Renders an [ImageVector] icon from any icon set (Lucide, custom vectors, ...), tinted to
 * match its surroundings the same way [Text] does.
 *
 * [tint] resolution, in priority order: an explicit [tint] always wins; otherwise this reads
 * [LocalContentColor] (provided by [Button] and other content-color-setting
 * components); if neither applies, falls back to `colors.foreground` -- the same default
 * [Text] uses.
 *
 * Style merge (later wins): `variant → style`.
 *
 * Usage:
 * ```
 * Button(onClick = {}) {
 *     Icon(Heroicons.Outline.Bolt)
 *     Text { "Generate" }
 * }
 * Icon(Lucide.Search, variant = IconVariant.Small)
 * ```
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    style: Style = Style,
    tint: Color = Color.Unspecified,
    variant: IconVariant = IconVariant.Default,
    contentDescription: String? = null,
) {
    val theme = currentTheme
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val resolvedTint =
        when {
            tint != Color.Unspecified -> tint
            LocalContentColor.current != Color.Unspecified -> LocalContentColor.current
            else -> theme.colors.foreground
        }
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier =
            modifier.styleable(
                styleState,
                variant.rememberStyle() then style,
            ),
        colorFilter = ColorFilter.tint(resolvedTint),
    )
}
