package com.toutiaoyan.compose.tailwind.core

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailwind's default `@container` size scale (`@3xs` … `@7xl`), as exact dp equivalents of
 * Tailwind v4's `--container-*` theme values (16rem … 80rem at the project's 1rem == 16dp
 * convention).
 *
 * These are **not** the same as [TwBreakpoint]:
 * breakpoints are viewport `min-width` thresholds (`sm:` / `md:` …); container sizes are
 * parent-element thresholds (`@sm:` / `@md:` …). See
 * https://tailwindcss.com/docs/responsive-design#container-queries
 *
 * Naming mirrors [TwBreakpoint]'s `xl2` for `2xl`
 * (Kotlin identifiers cannot start with a digit): `xs3` = `@3xs`, `xl7` = `@7xl`.
 *
 * Originally a fill until upstream shipped `TwContainer`; now kept as the local
 * `@container` size scale alongside vendored tailwind tokens.
 */
object TwContainer {
    val xs3: Dp = 256.dp // 16rem — @3xs
    val xs2: Dp = 288.dp // 18rem — @2xs
    val xs: Dp = 320.dp // 20rem — @xs
    val sm: Dp = 384.dp // 24rem — @sm
    val md: Dp = 448.dp // 28rem — @md
    val lg: Dp = 512.dp // 32rem — @lg
    val xl: Dp = 576.dp // 36rem — @xl
    val xl2: Dp = 672.dp // 42rem — @2xl
    val xl3: Dp = 768.dp // 48rem — @3xl
    val xl4: Dp = 896.dp // 56rem — @4xl
    val xl5: Dp = 1024.dp // 64rem — @5xl
    val xl6: Dp = 1152.dp // 72rem — @6xl
    val xl7: Dp = 1280.dp // 80rem — @7xl
}

/**
 * Resolves a single value against [TwContainer] the same way `twResponsive` resolves
 * viewport breakpoints: mobile-first, largest matching size wins.
 *
 * Pass the **container** width (e.g. [BoxWithConstraintsScope.maxWidth]), not the window
 * width — that is what `@container` / `@md:` style against. For viewport `sm:`/`md:` use
 * [com.toutiaoyan.compose.tailwind.core.twResponsive] instead.
 *
 * ```
 * BoxWithConstraints {
 *     val columns = twContainerQuery(
 *         containerWidth = maxWidth,
 *         default = 1,
 *         sm = 2,
 *         lg = 3,
 *     )
 * }
 * ```
 */
fun <T> twContainerQuery(
    containerWidth: Dp,
    default: T,
    xs3: T? = null,
    xs2: T? = null,
    xs: T? = null,
    sm: T? = null,
    md: T? = null,
    lg: T? = null,
    xl: T? = null,
    xl2: T? = null,
    xl3: T? = null,
    xl4: T? = null,
    xl5: T? = null,
    xl6: T? = null,
    xl7: T? = null,
): T =
    when {
        xl7 != null && containerWidth >= TwContainer.xl7 -> xl7
        xl6 != null && containerWidth >= TwContainer.xl6 -> xl6
        xl5 != null && containerWidth >= TwContainer.xl5 -> xl5
        xl4 != null && containerWidth >= TwContainer.xl4 -> xl4
        xl3 != null && containerWidth >= TwContainer.xl3 -> xl3
        xl2 != null && containerWidth >= TwContainer.xl2 -> xl2
        xl != null && containerWidth >= TwContainer.xl -> xl
        lg != null && containerWidth >= TwContainer.lg -> lg
        md != null && containerWidth >= TwContainer.md -> md
        sm != null && containerWidth >= TwContainer.sm -> sm
        xs != null && containerWidth >= TwContainer.xs -> xs
        xs2 != null && containerWidth >= TwContainer.xs2 -> xs2
        xs3 != null && containerWidth >= TwContainer.xs3 -> xs3
        else -> default
    }

/**
 * Convenience overload that reads [BoxWithConstraintsScope.maxWidth] as the container size.
 */
fun <T> BoxWithConstraintsScope.twContainerQuery(
    default: T,
    xs3: T? = null,
    xs2: T? = null,
    xs: T? = null,
    sm: T? = null,
    md: T? = null,
    lg: T? = null,
    xl: T? = null,
    xl2: T? = null,
    xl3: T? = null,
    xl4: T? = null,
    xl5: T? = null,
    xl6: T? = null,
    xl7: T? = null,
): T =
    twContainerQuery(
        containerWidth = maxWidth,
        default = default,
        xs3 = xs3,
        xs2 = xs2,
        xs = xs,
        sm = sm,
        md = md,
        lg = lg,
        xl = xl,
        xl2 = xl2,
        xl3 = xl3,
        xl4 = xl4,
        xl5 = xl5,
        xl6 = xl6,
        xl7 = xl7,
    )
