@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toggle
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.checked
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

// Matches shadcn/ui's real toggle.tsx: shared hover:bg-muted, data-[state=on]:bg-accent
// (our `secondary` token stands in for `accent`, since they're the same value in
// the reference theme). `checked` follows `hovered`, and `pressed` follows both, so each
// stronger interaction state wins when several are active at once, matching CSS cascade order.
sealed interface ToggleVariant {
    data object Default : ToggleVariant

    data object Outline : ToggleVariant
}

/** Same color each variant passes to Style `contentColor(...)`, for [LocalContentColor]. */
fun ToggleVariant.contentColor(colors: Colors): Color =
    when (this) {
        ToggleVariant.Default, ToggleVariant.Outline -> colors.foreground
    }

/**
 * Heights / min-widths matching base-nova toggle:
 * `default` → [Md] (`h-8`/`min-w-8`), `sm` → [Sm] (`h-7`/`min-w-7`), `lg` → [Lg] (`h-9`/`min-w-9`).
 */
sealed interface ToggleSize {
    data object Sm : ToggleSize

    data object Md : ToggleSize

    data object Lg : ToggleSize
}

/** Logical icon edge, matching shadcn's `data-icon="inline-start|inline-end"`. */
enum class ToggleIconPlacement { Start, End, Both }

@Composable
fun ToggleVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        val foreground = receiver.contentColor(colors)
        val pressedSurface =
            colors.foreground.copy(alpha = 0.08f).compositeOver(
                if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
            )
        // Foundation interpolates straight color channels. Transparent black would
        // darken a light surface mid-fade; preserve the destination RGB at zero alpha.
        // The resting fill remains fully transparent over any caller surface.
        val transparentSurface =
            if (theme.activePreset == StylePreset.Nova) colors.muted.copy(alpha = 0f) else Color.Transparent
        val colorTransition =
            if (theme.activePreset == StylePreset.Nova) {
                tween<Float>(durationMillis = 150, easing = FastOutSlowInEasing)
            } else {
                spring<Float>()
            }
        when (receiver) {
            ToggleVariant.Default ->
                Style {
                    background(transparentSurface)
                    contentColor(foreground)
                    animate(toSpec = colorTransition, fromSpec = colorTransition) {
                        hovered { background(colors.muted) }
                        checked {
                            background(
                                if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
                            )
                        }
                        if (theme.activePreset != StylePreset.Nova) {
                            pressed { background(pressedSurface) }
                        }
                    }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ToggleVariant.Outline ->
                Style {
                    background(transparentSurface)
                    contentColor(foreground)
                    border(if (theme.activePreset == StylePreset.Nova) colors.input else colors.border)
                    animate(toSpec = colorTransition, fromSpec = colorTransition) {
                        hovered {
                            background(
                                if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
                            )
                        }
                        checked {
                            background(
                                if (theme.activePreset == StylePreset.Nova) colors.muted else colors.secondary,
                            )
                        }
                        if (theme.activePreset != StylePreset.Nova) {
                            pressed { background(pressedSurface) }
                        }
                    }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }
        }
    }
}

@Composable
fun ToggleSize.rememberStyle(): Style = rememberStyle(iconPlacement = null)

@Composable
fun ToggleSize.rememberStyle(iconPlacement: ToggleIconPlacement?): Style {
    val receiver = this
    val direction = LocalLayoutDirection.current
    return rememberTheme(receiver, iconPlacement, direction) {
        val horizontal = spacing.scale2p5
        val icon =
            when (receiver) {
                ToggleSize.Sm -> spacing.scale1p5
                ToggleSize.Md, ToggleSize.Lg -> spacing.scale2
            }
        val logicalStart =
            if (
                iconPlacement == ToggleIconPlacement.Start ||
                iconPlacement == ToggleIconPlacement.Both
            ) {
                icon
            } else {
                horizontal
            }
        val logicalEnd =
            if (
                iconPlacement == ToggleIconPlacement.End ||
                iconPlacement == ToggleIconPlacement.Both
            ) {
                icon
            } else {
                horizontal
            }
        val padding: StyleScope.() -> Unit =
            if (iconPlacement == null) {
                { contentPadding(horizontal = horizontal, vertical = 0.dp) }
            } else {
                {
                    // Foundation scalar padding places physical edges; map logical edges once.
                    contentPadding(
                        start = if (direction == LayoutDirection.Ltr) logicalStart else logicalEnd,
                        top = 0.dp,
                        end = if (direction == LayoutDirection.Ltr) logicalEnd else logicalStart,
                        bottom = 0.dp,
                    )
                }
            }
        when (receiver) {
            // base-nova sm = h-7 min-w-7 px-2.5
            ToggleSize.Sm ->
                Style {
                    padding()
                    height(28.dp)
                    minWidth(28.dp)
                    fontSize(12.8.sp)
                    if (theme.activePreset == StylePreset.Nova) shape(RoundedCornerShape(minOf(radius.md, 12.dp)))
                }

            // base-nova default = h-8 min-w-8 px-2.5
            ToggleSize.Md ->
                Style {
                    padding()
                    height(32.dp)
                    minWidth(32.dp)
                    fontSize(14.sp)
                }

            // base-nova lg = h-9 min-w-9 px-2.5
            ToggleSize.Lg ->
                Style {
                    padding()
                    height(36.dp)
                    minWidth(36.dp)
                    fontSize(14.sp)
                }
        }
    }
}
