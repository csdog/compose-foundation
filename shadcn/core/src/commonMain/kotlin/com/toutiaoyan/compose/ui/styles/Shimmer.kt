@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import com.toutiaoyan.compose.ui.components.text.preserveTextCurrentColor
import com.toutiaoyan.compose.ui.components.text.TextColorBrush
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.skeleton.rememberSkeletonChrome
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fixed highlight-band half-width approximation. CSS uses `calc(3ch + 40px)`;
 * this constant assumes 3ch ≈ 24dp and does not measure the actual font.
 */
val DefaultShimmerSpread: Dp = 64.dp

/** Default tilt in degrees — real shadcn `--shimmer-angle` initial value. */
const val DefaultShimmerAngleDegrees: Float = 20f

/**
 * A sweeping highlight animation, matching real shadcn/ui's `shimmer` utility
 * ([docs](https://ui.shadcn.com/docs/utils/shimmer)):
 *
 * | CSS | Compose |
 * |---|---|
 * | `shimmer` | [Modifier.shimmer] / [rememberShimmerStyle] |
 * | `shimmer-none` | `enabled = false` |
 * | `shimmer-color-*` | [color] |
 * | `shimmer-duration-*` (default 2s) | [durationMillis] |
 * | `shimmer-spread-*` (default `3ch + 40px`) | [spread] |
 * | `shimmer-angle-*` (default `20`) | [angleDegrees] |
 * | `shimmer-reverse` / RTL | [reverse] / [LocalLayoutDirection] |
 * | `shimmer-once` | [once] |
 *
 * Two forms:
 *
 * - **[Modifier.shimmer]** — an overlay extension for already-drawn glyphs, icons,
 *   skeleton blocks or groups. Its white SrcAtop highlight is distinct from CSS text fill.
 * - **[rememberShimmerStyle] / [Style.shimmer]** — Style `contentBrush` with a moving
 *   `base → highlight → base` gradient, matching CSS `background-clip: text`. Use on
 *   [Text]; keep [Modifier.shimmer] for whole-card / solid-block sweeps.
 *
 * Usage:
 * ```
 * Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) { "Generating response…" }
 * Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) { "Generating response…" }
 * Skeleton(style = rememberSkeletonChrome(), modifier = Modifier.fillMaxWidth().height(16.dp).shimmer())
 * ```
 */
fun Modifier.shimmer(
    enabled: Boolean = true,
    color: Color? = null,
    durationMillis: Int = 2000,
    spread: Dp = DefaultShimmerSpread,
    angleDegrees: Float = DefaultShimmerAngleDegrees,
    reverse: Boolean = false,
    once: Boolean = false,
): Modifier =
    composed {
        if (!enabled) return@composed this

        val theme = Theme.LocalTheme.current
        val layoutDirection = LocalLayoutDirection.current
        val density = LocalDensity.current
        val spreadPx = with(density) { spread.toPx() }
        val highlightColor = color ?: defaultShimmerHighlight(isLight = theme.isLight)
        val sweepReverse =
            reverse xor (layoutDirection == LayoutDirection.Rtl)
        val progress = rememberShimmerProgress(durationMillis = durationMillis, once = once)

        // BlendMode.SrcAtop needs an offscreen compositing layer to composite only
        // against this modifier's own drawContent() output -- without it, the blend
        // reads whatever's already on the shared canvas (background/siblings drawn
        // earlier), so the sweep visibly tints/punches through the surrounding
        // background instead of stopping exactly at the content's opaque pixels.
        graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()

                val band =
                    shimmerBand(
                        width = size.width,
                        height = size.height,
                        progress = progress.value,
                        spreadPx = spreadPx,
                        angleDegrees = angleDegrees,
                        sweepReverse = sweepReverse,
                    )
                val brush =
                    Brush.linearGradient(
                        colorStops =
                            arrayOf(
                                0f to Color.Transparent,
                                0.25f to highlightColor.copy(alpha = highlightColor.alpha * 0.5f),
                                0.5f to highlightColor,
                                0.75f to highlightColor.copy(alpha = highlightColor.alpha * 0.5f),
                                1f to Color.Transparent,
                            ),
                        start = band.start,
                        end = band.end,
                    )
                drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
            }
    }

/**
 * Style form of shimmer for text — `contentBrush` with a size-aware moving gradient
 * (`base → highlight → base`), matching CSS `background-clip: text`.
 *
 * Prefer chaining onto a content-color style:
 * `textStyle = TextColors.Muted, style = rememberShimmerStyle()`.
 *
 * [baseColor] defaults to Text's actual currentColor at draw time. [color] overrides
 * the highlight; otherwise light mode scales currentColor alpha to20%, and dark mode
 * raises its OKLCH lightness/alpha according to the official CSS. Explicit highlight
 * alpha is retained, and the middle stops use premultiplied OKLCH color mixing.
 */
@Composable
fun rememberShimmerStyle(
    enabled: Boolean = true,
    color: Color? = null,
    baseColor: Color? = null,
    durationMillis: Int = 2000,
    spread: Dp = DefaultShimmerSpread,
    angleDegrees: Float = DefaultShimmerAngleDegrees,
    reverse: Boolean = false,
    once: Boolean = false,
): Style {
    if (!enabled) return Style

    val theme = Theme.LocalTheme.current
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val spreadPx = with(density) { spread.toPx() }
    val highlight = color?.takeIf { it.isSpecified }
    val resolvedBase = baseColor?.takeIf { it.isSpecified }
    val colors = remember(highlight, theme.isLight) { ShimmerColorCache(highlight, dark = !theme.isLight) }
    val sweepReverse = reverse xor (layoutDirection == LayoutDirection.Rtl)
    val progress = rememberShimmerProgress(durationMillis = durationMillis, once = once)

    // Style observes the animation in its own paint scope rather than the caller's
    // composition. A new brush per resolved frame also avoids ShaderBrush's size cache.
    return remember(progress, spreadPx, angleDegrees, sweepReverse, resolvedBase, colors) {
        Style {
            contentBrush(
                ShimmerContentBrush(
                    progress = progress.value,
                    spreadPx = spreadPx,
                    angleDegrees = angleDegrees,
                    sweepReverse = sweepReverse,
                    baseColor = resolvedBase,
                    colorCache = colors,
                    fallbackTextColor = Color.Unspecified,
                ),
            )
        }
    }
}

/** Chains [rememberShimmerStyle] onto this [Style], same pattern as other Style `then` merges. */
@Composable
fun Style.shimmer(
    enabled: Boolean = true,
    color: Color? = null,
    baseColor: Color? = null,
    durationMillis: Int = 2000,
    spread: Dp = DefaultShimmerSpread,
    angleDegrees: Float = DefaultShimmerAngleDegrees,
    reverse: Boolean = false,
    once: Boolean = false,
): Style {
    val animation = rememberShimmerStyle(enabled, color, baseColor, durationMillis, spread, angleDegrees, reverse, once)
    val source = this
    return remember(source, animation) { (source then animation).preserveTextCurrentColor() }
}

/**
 * Highlight for the Modifier overlay extension. This is intentionally separate
 * from the CSS text-fill colors resolved by [rememberShimmerStyle].
 */
internal fun defaultShimmerHighlight(isLight: Boolean): Color =
    if (isLight) {
        Color.White.copy(alpha = 0.55f)
    } else {
        Color.White.copy(alpha = 0.22f)
    }

@Composable
internal fun rememberShimmerProgress(
    durationMillis: Int,
    once: Boolean,
): State<Float> =
    if (once) {
        val animatable = remember(durationMillis) { Animatable(0f) }
        LaunchedEffect(durationMillis) {
            animatable.snapTo(0f)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis, easing = LinearEasing),
            )
        }
        animatable.asState()
    } else {
        val transition = rememberInfiniteTransition(label = "shadcn-shimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis, easing = LinearEasing),
                ),
            label = "shadcn-shimmer-progress",
        )
    }

internal data class ShimmerBand(
    val start: Offset,
    val end: Offset,
)

/** CSS: `linear-gradient(calc(90deg + var(--shimmer-angle)), …)` traveling across [width]. */
internal fun shimmerBand(
    width: Float,
    height: Float,
    progress: Float,
    spreadPx: Float,
    angleDegrees: Float,
    sweepReverse: Boolean,
): ShimmerBand {
    val bandWidth = (spreadPx * 2f).coerceAtLeast(1f)
    val travel = width + bandWidth
    val t = if (sweepReverse) 1f - progress else progress
    val centerX = -bandWidth / 2f + t * travel
    val centerY = height / 2f

    val gradientAngleRad = (90.0 + angleDegrees) * PI / 180.0
    val dirX = sin(gradientAngleRad).toFloat()
    val dirY = (-cos(gradientAngleRad)).toFloat()
    val half = bandWidth / 2f

    return ShimmerBand(
        start = Offset(centerX - dirX * half, centerY - dirY * half),
        end = Offset(centerX + dirX * half, centerY + dirY * half),
    )
}

/** Size-aware CSS text brush; actual currentColor is supplied by Text's draw boundary. */
@Immutable
internal class ShimmerContentBrush(
    private val progress: Float,
    private val spreadPx: Float,
    private val angleDegrees: Float,
    private val sweepReverse: Boolean,
    private val baseColor: Color?,
    private val colorCache: ShimmerColorCache,
    override val fallbackTextColor: Color,
) : ShaderBrush(), TextColorBrush {
    override fun resolveTextColor(color: Color): Brush = copyWithColor(baseColor ?: color, fallbackTextColor)

    override fun withFallbackTextColor(color: Color): Brush =
        if (color == fallbackTextColor) this else copyWithColor(baseColor, color)

    private fun copyWithColor(
        base: Color?,
        fallback: Color,
    ) = ShimmerContentBrush(
        progress,
        spreadPx,
        angleDegrees,
        sweepReverse,
        base,
        colorCache,
        fallback,
    )

    override fun createShader(size: Size): Shader {
        val band = shimmerBand(size.width, size.height, progress, spreadPx, angleDegrees, sweepReverse)
        return LinearGradientShader(
            from = band.start,
            to = band.end,
            colors = colorCache.colors(baseColor ?: fallbackTextColor.takeIf { it.isSpecified } ?: Color.Black),
        )
    }
}

@Preview(showBackground = true, name = "Shimmer")
@Composable
private fun ShimmerPreview() {
    Theme(baseColor = BaseColor.Neutral) {
        Box(
            modifier =
                Modifier
                    .background(currentTheme.colors.background)
                    .padding(4.spacing.dp),
        ) {
            Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) {
                "Generating response…"
            }
        }
    }
}

@Preview(showBackground = true, name = "Shimmer Style")
@Composable
private fun ShimmerStylePreview() {
    Theme(baseColor = BaseColor.Neutral) {
        Box(
            modifier =
                Modifier
                    .background(currentTheme.colors.background)
                    .padding(4.spacing.dp),
        ) {
            Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) {
                "Generating response…"
            }
        }
    }
}

@Preview(showBackground = true, name = "Shimmer angle / spread")
@Composable
private fun ShimmerVariantsPreview() {
    Theme(baseColor = BaseColor.Neutral) {
        Column(
            modifier =
                Modifier
                    .background(currentTheme.colors.background)
                    .padding(4.spacing.dp),
            verticalArrangement = Arrangement.spacedBy(3.spacing.dp),
        ) {
            Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) {
                "Default (angle 20°, 2s)"
            }
            Text(
                textStyle = TextColors.Muted,
                modifier = Modifier.shimmer(angleDegrees = 45f, durationMillis = 1000),
            ) {
                "angle 45° · duration 1000"
            }
            Text(
                textStyle = TextColors.Muted,
                modifier = Modifier.shimmer(spread = 24.spacing.dp, reverse = true),
            ) {
                "spread-24 · reverse"
            }
        }
    }
}

@Preview(showBackground = true, name = "Shimmer on Skeleton")
@Composable
private fun ShimmerBlockPreview() {
    Theme(baseColor = BaseColor.Neutral) {
        Column(
            modifier =
                Modifier
                    .background(currentTheme.colors.background)
                    .padding(4.spacing.dp)
                    .fillMaxWidth()
                    .shimmer(),
            verticalArrangement = Arrangement.spacedBy(2.spacing.dp),
        ) {
            repeat(4) {
                Skeleton(
                    style = rememberSkeletonChrome(),
                    modifier = Modifier.fillMaxWidth().height(4.spacing.dp),
                )
            }
        }
    }
}
