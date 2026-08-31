@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

// Matches shadcn/ui's real button.tsx (github.com/shadcn-ui/ui) as closely as our
// Style API allows: most variants have zero border (only Outline has one), hover
// is an alpha-blended background rather than a whole-node dim. Every variant gets
// the same `focusRing(...)` ring -- real shadcn's shared `.cn-button` class applies
// focus-visible:ring-* regardless of variant, including Link.

sealed interface ButtonVariant {
    data object Default : ButtonVariant

    data object Outline : ButtonVariant

    data object Secondary : ButtonVariant

    data object Ghost : ButtonVariant

    data object Destructive : ButtonVariant

    data object Link : ButtonVariant
}

/**
 * Press feedback for [com.toutiaoyan.compose.ui.components.button.Button].
 *
 * Inspired by RikkaUi `ButtonAnimation`. Chip / Toggle still take this as a Style
 * merge; [Button] uses [androidx.compose.foundation.Indication] instead.
 */
sealed interface ButtonAnimation {
    /** Subtle spring scale-down on press (RikkaUi: 0.97 + medium-bouncy spring). */
    data object Scale : ButtonAnimation

    /** Vertical spring bounce on press ([com.toutiaoyan.compose.ui.styles.pressedBounce] / [com.toutiaoyan.compose.ui.modifiers.BounceIndication]). */
    data object Bounce : ButtonAnimation

    /** Nudge down via [pressedMoveDown] (default tactile feel). */
    data object MoveDown : ButtonAnimation

    /** No press transform. */
    data object None : ButtonAnimation
}

/**
 * The same color each variant passes to `contentColor(...)` below, factored out so
 * [com.toutiaoyan.compose.ui.components.button.Button] can also provide it via
 * [com.toutiaoyan.compose.ui.theme.LocalContentColor] for
 * [com.toutiaoyan.compose.ui.components.icon.Icon] to read -- the Style API's own
 * `contentColor` has no public reader, only [rememberStyle]'s `Style { }` block can set it.
 * Ignores hover/disabled state (unlike the `Style` block below): a static per-variant color is
 * enough for icon tinting, and tracking those dynamically here would duplicate this same `when`
 * a second time for no real visual benefit.
 */
fun ButtonVariant.iconTint(colors: Colors): Color =
    when (this) {
        ButtonVariant.Default -> colors.primaryForeground
        ButtonVariant.Outline -> colors.foreground
        ButtonVariant.Secondary -> colors.secondaryForeground
        ButtonVariant.Ghost -> colors.foreground
        ButtonVariant.Destructive -> colors.destructiveForeground
        ButtonVariant.Link -> colors.primary
    }

@Composable
fun ButtonVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            // Press motion is owned by [ButtonAnimation] on Button (not here),
            // so Scale / Bounce / MoveDown / None can be chosen without fighting this block.
            ButtonVariant.Default ->
                Style {
                    background(colors.primary)
                    contentColor(colors.primaryForeground)
                    hovered { background(colors.primary.copy(alpha = 0.9f)) }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ButtonVariant.Outline ->
                Style {
                    background(colors.background)
                    contentColor(colors.foreground)
                    border(colors.border)
                    hovered {
                        background(colors.secondary)
                        contentColor(colors.secondaryForeground)
                    }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ButtonVariant.Secondary ->
                Style {
                    background(colors.secondary)
                    contentColor(colors.secondaryForeground)
                    hovered { background(colors.secondary.copy(alpha = 0.8f)) }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ButtonVariant.Ghost ->
                Style {
                    contentColor(colors.foreground)
                    hovered {
                        background(colors.secondary)
                        contentColor(colors.secondaryForeground)
                    }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ButtonVariant.Destructive ->
                Style {
                    background(colors.destructive)
                    contentColor(colors.destructiveForeground)
                    hovered { background(colors.destructive.copy(alpha = 0.9f)) }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }

            ButtonVariant.Link ->
                Style {
                    contentColor(colors.primary)
                    hovered { textDecoration(TextDecoration.Underline) }
                    focusRing(RoundedCornerShape(radius.lg))
                    disabledDim()
                }
        }
    }
}

sealed interface ButtonSize {
    data object Xs : ButtonSize

    data object Sm : ButtonSize

    data object Md : ButtonSize

    data object Lg : ButtonSize

    /** Square icon button — base-nova `size="icon"` (`size-8` / 32px). */
    data object Icon : ButtonSize

    /** base-nova `size="icon-xs"` (`size-6` / 24px). */
    data object IconXs : ButtonSize

    /** base-nova `size="icon-sm"` (`size-7` / 28px). */
    data object IconSm : ButtonSize

    /** base-nova `size="icon-lg"` (`size-9` / 36px). */
    data object IconLg : ButtonSize
}

@Composable
fun ButtonSize.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            // base-nova `xs`: `h-6 px-2 text-xs`
            ButtonSize.Xs ->
                Style {
                    contentPadding(horizontal = spacing.sm, vertical = 0.dp)
                    fontSize(12.sp)
                    height(24.dp)
                }

            // base-nova `sm`: `h-7 px-2.5`
            ButtonSize.Sm ->
                Style {
                    contentPadding(horizontal = spacing.scale2p5, vertical = 0.dp)
                    fontSize(12.8.sp)
                    height(28.dp)
                }

            // base-nova `default`: `h-8 px-2.5`
            ButtonSize.Md ->
                Style {
                    contentPadding(horizontal = spacing.scale2p5, vertical = 0.dp)
                    fontSize(14.sp)
                    height(32.dp)
                }

            // base-nova `lg`: `h-9 px-2.5`
            ButtonSize.Lg ->
                Style {
                    contentPadding(horizontal = spacing.scale2p5, vertical = 0.dp)
                    fontSize(14.sp)
                    height(36.dp)
                }

            // Square icon sizes: size-8 / size-6 / size-7 / size-9
            ButtonSize.Icon ->
                Style {
                    contentPadding(0.dp)
                    width(32.dp)
                    height(32.dp)
                }

            ButtonSize.IconXs ->
                Style {
                    contentPadding(0.dp)
                    width(24.dp)
                    height(24.dp)
                }

            ButtonSize.IconSm ->
                Style {
                    contentPadding(0.dp)
                    width(28.dp)
                    height(28.dp)
                }

            ButtonSize.IconLg ->
                Style {
                    contentPadding(0.dp)
                    width(36.dp)
                    height(36.dp)
                }
        }
    }
}
