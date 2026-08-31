package com.toutiaoyan.compose.tailwind.core

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Corner-radius scale for Tailwind's `rounded-*` utilities.
 *
 * Defaults match Tailwind v4's built-in `--radius-*` fixed rem values
 * (`xs`/`sm`/`md`/`lg`/… at 2/4/6/8/… dp when `1rem = 16px`).
 *
 * Under a shadcn [Theme][com.toutiaoyan.compose.ui.theme.Theme], the theme
 * provides [LocalTwRadius] with this scale (from `--radius` via [fromBaseRadius], or a
 * style-preset personality), mirroring how real shadcn overrides `@theme` `--radius-*`.
 * Without a Theme, [twRadius] falls back to [Default].
 */
@Immutable
data class TwRadiusScale(
    val none: Dp = 0.dp,
    val xs: Dp = 2.dp,
    val sm: Dp = 4.dp,
    val md: Dp = 6.dp,
    val lg: Dp = 8.dp,
    val xl: Dp = 12.dp,
    val xl2: Dp = 16.dp,
    val xl3: Dp = 24.dp,
    val xl4: Dp = 32.dp,
    val full: Dp = 9999.dp,
) {
    companion object {
        /** Tailwind v4 default scale (no theme override). */
        val Default: TwRadiusScale = TwRadiusScale()

        /**
         * Derives a scale from one base radius, mirroring shadcn/ui v4's
         * `--radius-*` formula: `sm = base * 0.6`, `md = base * 0.8`,
         * `lg = base` (`--radius-lg: var(--radius)`), `xl = base * 1.4`,
         * `2xl`/`3xl`/`4xl` = `1.8`/`2.2`/`2.6`. `none`/`full` stay fixed.
         */
        fun fromBaseRadius(baseRadius: Dp): TwRadiusScale =
            TwRadiusScale(
                xs = baseRadius * 0.4f,
                sm = baseRadius * 0.6f,
                md = baseRadius * 0.8f,
                lg = baseRadius,
                xl = baseRadius * 1.4f,
                xl2 = baseRadius * 1.8f,
                xl3 = baseRadius * 2.2f,
                xl4 = baseRadius * 2.6f,
            )

        /**
         * Explicit ladder for style personalities that aren't a scalar of one base.
         * Fills `xl3`/`xl4` from [lg] with the same `* 2.2` / `* 2.6` ratios as
         * [fromBaseRadius].
         */
        fun of(
            xs: Dp,
            sm: Dp,
            md: Dp,
            lg: Dp,
            xl: Dp,
            xl2: Dp,
            none: Dp = 0.dp,
            full: Dp = 9999.dp,
        ): TwRadiusScale =
            TwRadiusScale(
                none = none,
                xs = xs,
                sm = sm,
                md = md,
                lg = lg,
                xl = xl,
                xl2 = xl2,
                xl3 = lg * 2.2f,
                xl4 = lg * 2.6f,
                full = full,
            )
    }
}

/**
 * Active `rounded-*` scale. Themes override this; bare Tailwind usage keeps [TwRadiusScale.Default].
 */
val LocalTwRadius: ProvidableCompositionLocal<TwRadiusScale> =
    staticCompositionLocalOf { TwRadiusScale.Default }

/** Composable read of the active scale (Theme override or [TwRadiusScale.Default]). */
val twRadius: TwRadiusScale
    @Composable get() = LocalTwRadius.current

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.twRadius: TwRadiusScale
    get() = LocalTwRadius.currentValue
