// SpacingUnit / TwSpacingScale / n.spacing.dp|sp are hand-maintained.
// Numeric scale steps mirror Tailwind's default spacing scale (1rem == 16dp).

package com.toutiaoyan.compose.tailwind.core

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.TwSpacingScale.Companion.fromUnit
import com.toutiaoyan.compose.tailwind.core.TwSpacingScale.Companion.named
import kotlin.jvm.JvmInline

/**
 * Tailwind spacing step: `n * 0.25rem`. With default `1rem = 16px` that is **`n * 4.dp`**.
 *
 * - Arbitrary gaps / type: `4.spacing.dp`, `3.spacing.sp` (reads [LocalUnitSpacing])
 * - Full scale table + optional named aliases: [TwSpacingScale] / [twSpacing]
 * - Rem step override: [LocalUnitSpacing] / [DefaultUnitSpacing] (Theme `unitSpacing`)
 *
 * Use [SpacingUnit.sp] rather than `Int.sp` to avoid clashing with `androidx.compose.ui.unit.sp`.
 */
@Immutable
@JvmInline
value class SpacingUnit(
    val value: Float,
) : Comparable<SpacingUnit> {
    override fun compareTo(other: SpacingUnit): Int = value.compareTo(other.value)
}

/**
 * Default spacing step in dp (`0.25rem` when `1rem = 16px`).
 * Themes override via [LocalUnitSpacing].
 */
const val DefaultUnitSpacing = 16f * 0.25f

/**
 * Active rem spacing step (`n.spacing` multiplier). Themes override this;
 * bare usage keeps [DefaultUnitSpacing].
 */
val LocalUnitSpacing: ProvidableCompositionLocal<Float> =
    staticCompositionLocalOf { DefaultUnitSpacing }

/** Composable read of the active step. */
val unitSpacing: Float
    @Composable get() = LocalUnitSpacing.current

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.unitSpacing: Float
    get() = LocalUnitSpacing.currentValue

inline val Float.spacing: SpacingUnit
    get() = SpacingUnit(this)

inline val Double.spacing: SpacingUnit
    get() = SpacingUnit(this.toFloat())

inline val Int.spacing: SpacingUnit
    get() = SpacingUnit(this.toFloat())

/** Convert with an explicit step (defaults / object init / tests). */
fun SpacingUnit.toDp(unitSpacing: Float = DefaultUnitSpacing): Dp =
    (value * unitSpacing).dp

fun SpacingUnit.toSp(unitSpacing: Float = DefaultUnitSpacing): TextUnit =
    (value * unitSpacing).sp

/** Theme-aware dp (`LocalUnitSpacing`). Prefer this in `@Composable` call sites. */
val SpacingUnit.dp: Dp
    @Composable get() = toDp(LocalUnitSpacing.current)

/** Theme-aware sp (`LocalUnitSpacing`). Prefer this in `@Composable` call sites. */
val SpacingUnit.sp: TextUnit
    @Composable get() = toSp(LocalUnitSpacing.current)

/** Style-block conversion using [LocalUnitSpacing] at consume time. */
@OptIn(ExperimentalFoundationStyleApi::class)
fun StyleScope.dp(unit: SpacingUnit): Dp = unit.toDp(LocalUnitSpacing.currentValue)

@OptIn(ExperimentalFoundationStyleApi::class)
fun StyleScope.sp(unit: SpacingUnit): TextUnit = unit.toSp(LocalUnitSpacing.currentValue)


/**
 * Tailwind spacing scale (`scale4` == Tailwind `4` == `1rem` at default unit) plus optional
 * named aliases (`xs`/`sm`/`md`/…) for StylePreset density personalities.
 *
 * [fromUnit] builds the numeric table from the rem step; named aliases default to
 * `0.5/1/2/3/4/5/6/8 × unit`. Presets like Nova override only the named slots via [named].
 */
@Immutable
data class TwSpacingScale(
    val scale0: Dp,
    val scalePx: Dp,
    val scale0p5: Dp,
    val scale1: Dp,
    val scale1p5: Dp,
    val scale2: Dp,
    val scale2p5: Dp,
    val scale3: Dp,
    val scale3p5: Dp,
    val scale4: Dp,
    val scale5: Dp,
    val scale6: Dp,
    val scale7: Dp,
    val scale8: Dp,
    val scale9: Dp,
    val scale10: Dp,
    val scale11: Dp,
    val scale12: Dp,
    val scale14: Dp,
    val scale16: Dp,
    val scale20: Dp,
    val scale24: Dp,
    val scale28: Dp,
    val scale32: Dp,
    val scale36: Dp,
    val scale40: Dp,
    val scale44: Dp,
    val scale48: Dp,
    val scale52: Dp,
    val scale56: Dp,
    val scale60: Dp,
    val scale64: Dp,
    val scale72: Dp,
    val scale80: Dp,
    val scale96: Dp,
    /** Design-system aliases (not Tailwind built-ins). Default: `0.5×` unit. */
    val xs2: Dp,
    /** Default: `1×` unit (= [scale1]). */
    val xs: Dp,
    /** Default: `2×` unit (= [scale2]). */
    val sm: Dp,
    /** Default: `3×` unit (= [scale3]). */
    val md: Dp,
    /** Default: `4×` unit (= [scale4]). */
    val lg: Dp,
    /** Default: `5×` unit (= [scale5]). */
    val xl: Dp,
    /** Default: `6×` unit (= [scale6]). */
    val xl2: Dp,
    /** Default: `8×` unit (= [scale8]). */
    val xl3: Dp,
) {
    companion object {
        /** Full Tailwind numeric table + named aliases for [unitSpacing]. */
        fun fromUnit(unitSpacing: Float = DefaultUnitSpacing): TwSpacingScale {
            fun step(n: Float): Dp = (n * unitSpacing).dp
            return TwSpacingScale(
                xs2 = step(0.5f),
                xs = step(1f),
                sm = step(2f),
                md = step(3f),
                lg = step(4f),
                xl = step(5f),
                xl2 = step(6f),
                xl3 = step(8f),
                scale0 = 0.dp,
                scalePx = 1.dp,
                scale0p5 = step(0.5f),
                scale1 = step(1f),
                scale1p5 = step(1.5f),
                scale2 = step(2f),
                scale2p5 = step(2.5f),
                scale3 = step(3f),
                scale3p5 = step(3.5f),
                scale4 = step(4f),
                scale5 = step(5f),
                scale6 = step(6f),
                scale7 = step(7f),
                scale8 = step(8f),
                scale9 = step(9f),
                scale10 = step(10f),
                scale11 = step(11f),
                scale12 = step(12f),
                scale14 = step(14f),
                scale16 = step(16f),
                scale20 = step(20f),
                scale24 = step(24f),
                scale28 = step(28f),
                scale32 = step(32f),
                scale36 = step(36f),
                scale40 = step(40f),
                scale44 = step(44f),
                scale48 = step(48f),
                scale52 = step(52f),
                scale56 = step(56f),
                scale60 = step(60f),
                scale64 = step(64f),
                scale72 = step(72f),
                scale80 = step(80f),
                scale96 = step(96f),
            )
        }

        /**
         * Absolute named ladder on top of [fromUnit] numerics — for StylePreset personalities
         * (Nova / Mira / …) that tune density without changing the rem step table.
         */
        fun named(
            xs2: Dp,
            xs: Dp,
            sm: Dp,
            md: Dp,
            lg: Dp,
            xl: Dp,
            xl2: Dp,
            xl3: Dp,
            unitSpacing: Float = DefaultUnitSpacing,
        ): TwSpacingScale =
            fromUnit(unitSpacing).copy(
                xs2 = xs2,
                xs = xs,
                sm = sm,
                md = md,
                lg = lg,
                xl = xl,
                xl2 = xl2,
                xl3 = xl3,
            )


        /** Default Tailwind-aligned scale (no theme override). */
        val Default: TwSpacingScale = fromUnit()
    }
}

/**
 * Active spacing scale. Themes override this; bare Tailwind usage keeps
 * [TwSpacingScale.Default].
 */
val LocalTwSpacing: ProvidableCompositionLocal<TwSpacingScale> =
    staticCompositionLocalOf { TwSpacingScale.Default }

/** Composable read of the active scale (Theme override or [TwSpacingScale.Default]). */
val twSpacing: TwSpacingScale
    @Composable get() = LocalTwSpacing.current

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.twSpacing: TwSpacingScale
    get() = LocalTwSpacing.currentValue

