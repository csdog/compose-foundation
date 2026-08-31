package com.toutiaoyan.compose.tailwind.core

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailwind's default `sm:`/`md:`/`lg:`/`xl:`/`2xl:` breakpoints, as exact dp equivalents of
 * Tailwind v4.3.2's `--breakpoint-*` values (40rem/48rem/64rem/80rem/96rem at the project's
 * 1rem == 16dp convention). These are `min-width` thresholds -- Tailwind's cascade is
 * mobile-first, so multiple breakpoints are simultaneously "active" at any width above `sm`.
 * See [twResponsive] for how a single value is resolved from these.
 */
object TwBreakpoint {
    val sm: Dp = 640.dp
    val md: Dp = 768.dp
    val lg: Dp = 1024.dp
    val xl: Dp = 1280.dp
    val xl2: Dp = 1536.dp
}

/**
 * The window's current width, as the `min-width` signal Tailwind's `sm:`/`md:`/`lg:`/`xl:`/
 * `2xl:` breakpoints are evaluated against. Backed by [LocalWindowInfo] -- JetBrains's own
 * doc comment on [androidx.compose.ui.platform.WindowInfo.containerDpSize] says it "should be
 * used as a breakpoint when changing between UI configurations", which is exactly this.
 *
 * Deliberately not `BoxWithConstraints` (that measures the local *container*, which is
 * Tailwind's different `@container` query concept) and not Material's `WindowSizeClass`
 * (whose 600dp/840dp breakpoints don't match Tailwind's 640/768/1024/1280/1536 scale).
 */
@Composable
fun currentTwWindowWidth(): Dp = LocalWindowInfo.current.containerDpSize.width

/**
 * Resolves a single value against [TwBreakpoint] the same way Tailwind's cascade does:
 * mobile-first, largest-matching-breakpoint wins.
 *
 * Unlike `twDark`, this is **not** a chainable `Modifier`/`TextStyle` lambda -- Tailwind's
 * breakpoints are `min-width` thresholds that stack (at a 1024dp-wide window, `sm`, `md`, and
 * `lg` are all simultaneously true), unlike `dark:`'s two mutually-exclusive states. Chaining
 * conditional modifier blocks the way `twDark { }` does would apply *all* matching blocks
 * (e.g. stacking three separate `.padding()` calls) instead of picking the one Tailwind's
 * cascade would actually resolve to, so this instead resolves to a single value up front:
 *
 * ```
 * Modifier.padding(twResponsive(default = twSpacing.scale4, md = twSpacing.scale6, lg = twSpacing.scale8))
 * ```
 *
 * For parent-element `@container` sizes use [twContainerQuery] instead.
 */
@Composable
fun <T> twResponsive(
    default: T,
    sm: T? = null,
    md: T? = null,
    lg: T? = null,
    xl: T? = null,
    xl2: T? = null,
): T {
    val width = currentTwWindowWidth()
    return when {
        xl2 != null && width >= TwBreakpoint.xl2 -> xl2
        xl != null && width >= TwBreakpoint.xl -> xl
        lg != null && width >= TwBreakpoint.lg -> lg
        md != null && width >= TwBreakpoint.md -> md
        sm != null && width >= TwBreakpoint.sm -> sm
        else -> default
    }
}
