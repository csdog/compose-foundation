@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.pressed
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.marker.*
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs

// region shared
private const val ShimmerMessage = "Generating response…"

@Composable
private fun ShimmerSourceTheme(content: @Composable () -> Unit) {
    val inherited = currentTheme
    Theme(
        preset = StylePreset.Rhea,
        baseColor = inherited.activeBaseColor,
        accent = inherited.activeAccent,
        isDark = inherited.isDark,
        baseRadius = inherited.baseRadius,
        fontFamily = inherited.fontFamily,
        ring = StylePreset.Rhea.ring.copy(enabled = true),
        minTouchTarget = null,
        content = content,
    )
}

@Composable
private fun shimmerViewportWidth(): Dp =
    with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }

@Composable
private fun shimmerRecipeTextStyle() =
    TextStyle(
        color = currentTheme.colors.mutedForeground,
        fontFamily = currentTheme.fontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )

@Composable
private fun shimmerRecipeSpread(style: TextStyle): Dp {
    val zero = rememberTextMeasurer().measure("0", style = style, softWrap = false)
    val advance = abs(zero.getHorizontalPosition(1, true) - zero.getHorizontalPosition(0, true))
    return with(LocalDensity.current) { (3f * advance).toDp() } + 40.dp
}

@Composable
private fun ShimmerText(
    value: String = ShimmerMessage,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color? = null,
    duration: Int = 2000,
    spread: Dp = Dp.Unspecified,
    angle: Float = 20f,
    reverse: Boolean = false,
    once: Boolean = false,
) {
    val textStyle = shimmerRecipeTextStyle()
    val resolvedSpread = if (spread == Dp.Unspecified) shimmerRecipeSpread(textStyle) else spread
    Text(
        modifier,
        textStyle = textStyle.copy(textAlign = TextAlign.Center),
        style =
            rememberShimmerStyle(
                enabled =
                enabled,
                color =
                color,
                durationMillis =
                duration,
                spread =
                resolvedSpread,
                angleDegrees =
                angle,
                reverse =
                reverse,
                once =
                once,
            ),
    ) { value }
}

@Composable
private fun ShimmerLabel(value: String) {
    Text(
        textStyle =
            TextStyle(
                color = currentTheme.colors.mutedForeground,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
            ),
    ) { value }
}

@Composable
private fun ShimmerCell(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth(), propagateMinConstraints = true) { content() }
        ShimmerLabel(label)
    }
}

@Composable
private fun ShimmerComparison(
    leftLabel: String,
    rightLabel: String,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    val wide = shimmerViewportWidth() >= 640.dp
    Box(Modifier.widthIn(max = 512.dp).fillMaxWidth().testTag("shimmer-example")) {
        if (wide) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Box(Modifier.weight(1f).testTag("shimmer-left")) { ShimmerCell(leftLabel, left) }
                Box(Modifier.weight(1f).testTag("shimmer-right")) { ShimmerCell(rightLabel, right) }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Box(Modifier.testTag("shimmer-left")) { ShimmerCell(leftLabel, left) }
                Box(Modifier.testTag("shimmer-right")) { ShimmerCell(rightLabel, right) }
            }
        }
    }
}
// endregion shared

// region default
@Composable
internal fun ShimmerDefaultRecipe() =
    ShimmerSourceTheme {
        Box(Modifier.testTag("shimmer-example")) { ShimmerText(modifier = Modifier.testTag("shimmer-text")) }
    }
// endregion default

// region marker
@Composable
internal fun ShimmerMarkerRecipe() =
    ShimmerSourceTheme {
        val shimmer = rememberShimmerStyle(spread = shimmerRecipeSpread(shimmerRecipeTextStyle()))
        Column(
            Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("shimmer-example"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Marker(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
                MarkerIcon { Spinner() }
                MarkerContent(style = shimmer) { Text { "Thinking..." } }
            }
            Marker(
                Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
                variant = MarkerVariant.Separator,
            ) { MarkerContent(style = shimmer) { Text { "Reading 4 files" } } }
        }
    }
// endregion marker

// region color
@Composable
internal fun ShimmerColorRecipe() =
    ShimmerSourceTheme {
        Column(
            Modifier.testTag("shimmer-example"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShimmerText(color = TwColors.blue500.copy(alpha = .6f), modifier = Modifier.testTag("shimmer-blue"))
            ShimmerText(color = Color(0xFF378ADD), modifier = Modifier.testTag("shimmer-custom"))
        }
    }
// endregion color

// region duration
@Composable
internal fun ShimmerDurationRecipe() =
    ShimmerSourceTheme {
        ShimmerComparison("shimmer", "shimmer-duration-1000", {
            ShimmerText(modifier = Modifier.testTag("shimmer-default"))
        }, { ShimmerText(duration = 1000, modifier = Modifier.testTag("shimmer-fast")) })
    }
// endregion duration

// region spread
@Composable
internal fun ShimmerSpreadRecipe() =
    ShimmerSourceTheme {
        ShimmerComparison("shimmer-spread-4", "shimmer-spread-24", {
            ShimmerText(spread = 16.dp, modifier = Modifier.testTag("shimmer-narrow"))
        }, { ShimmerText(spread = 96.dp, modifier = Modifier.testTag("shimmer-wide")) })
    }
// endregion spread

// region angle
@Composable
internal fun ShimmerAngleRecipe() =
    ShimmerSourceTheme {
        ShimmerComparison("shimmer", "shimmer-angle-45", {
            ShimmerText(modifier = Modifier.testTag("shimmer-default"))
        }, { ShimmerText(angle = 45f, modifier = Modifier.testTag("shimmer-angle")) })
    }
// endregion angle

// region reverse
@Composable
internal fun ShimmerReverseRecipe() =
    ShimmerSourceTheme {
        ShimmerComparison("shimmer", "shimmer-reverse", {
            ShimmerText(modifier = Modifier.testTag("shimmer-forward"))
        }, {
            ShimmerText(modifier = Modifier.testTag("shimmer-reverse"), reverse = true)
        })
    }
// endregion reverse

// region once
@Composable
internal fun ShimmerOnceRecipe() =
    ShimmerSourceTheme {
        var replay by remember { mutableIntStateOf(0) }
        Column(
            Modifier.testTag("shimmer-example"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            key(replay) { ShimmerText(duration = 1100, once = true, modifier = Modifier.testTag("shimmer-once")) }
            Button(
                "Replay",
                variant = ButtonVariant.Outline,
                size = ButtonSize.Sm,
                style = Style { pressed { translationY(1.dp.toPx()) } },
                onClick = { replay++ },
            )
        }
    }
// endregion once

// region none
@Composable
internal fun ShimmerNoneRecipe() =
    ShimmerSourceTheme {
        val enabled = shimmerViewportWidth() < 768.dp
        Column(
            Modifier.testTag("shimmer-example"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShimmerText(enabled = enabled, modifier = Modifier.testTag("shimmer-responsive"))
            ShimmerLabel("shimmer md:shimmer-none")
        }
    }
// endregion none

// region rtl
@Composable
internal fun ShimmerRtlRecipe() =
    ShimmerSourceTheme {
        ShimmerComparison("dir=\"ltr\"", "dir=\"rtl\"", {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                ShimmerText(modifier = Modifier.testTag("shimmer-ltr"))
            }
        }, {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ShimmerText("جارٍ إنشاء الرد…", modifier = Modifier.testTag("shimmer-rtl"))
            }
        })
    }
// endregion rtl

// region overlayModifier
@Composable
internal fun ShimmerOverlayModifierRecipe() {
    Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) {
        "Generating response…"
    }
}
// endregion overlayModifier

// region styleContentBrush
@Composable
internal fun ShimmerStyleContentBrushRecipe() {
    Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) {
        "Generating response…"
    }
}
// endregion styleContentBrush

// region durationAngleSpread
@Composable
internal fun ShimmerDurationAngleSpreadRecipe() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            textStyle = TextColors.Muted,
            modifier = Modifier.shimmer(durationMillis = 1000),
        ) { "duration 1000" }
        Text(
            textStyle = TextColors.Muted,
            modifier = Modifier.shimmer(angleDegrees = 45f),
        ) { "angle 45°" }
        Text(
            textStyle = TextColors.Muted,
            modifier = Modifier.shimmer(spread = 24.spacing.dp),
        ) { "spread-24" }
    }
}
// endregion durationAngleSpread

// region reverseAndOnce
@Composable
internal fun ShimmerReverseAndOnceRecipe() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            textStyle = TextColors.Muted,
            modifier = Modifier.shimmer(reverse = true),
        ) { "reverse" }
        Text(
            textStyle = TextColors.Muted,
            modifier = Modifier.shimmer(once = true, durationMillis = 1100),
        ) { "once" }
    }
}
// endregion reverseAndOnce

internal val shimmerOfficialExamples =
    listOf(
        ComponentExample("Default", code = shimmerDefaultRecipeCode, preview = { ShimmerDefaultRecipe() }),
        ComponentExample(
            "With Marker",
            code = shimmerMarkerRecipeCode,
            descriptionMarkdown =
                "The shimmer composes with any component that renders text. A common pattern " +
                    "is a `Marker` showing a live status while the assistant is working:",
            preview = { ShimmerMarkerRecipe() },
        ),
        ComponentExample(
            "Color",
            code = shimmerColorRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-color-<color>` to set the highlight color explicitly. It accepts " +
                    "theme colors with an optional opacity modifier, or any arbitrary color value.",
            preview = { ShimmerColorRecipe() },
        ),
        ComponentExample(
            "Duration",
            code = shimmerDurationRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-duration-<number>` to set the duration of one sweep in " +
                    "milliseconds. The default is `2000`, i.e. `2s`.",
            preview = { ShimmerDurationRecipe() },
        ),
        ComponentExample(
            "Spread",
            code = shimmerSpreadRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-spread-<number>` to set the width of the highlight band using " +
                    "the spacing scale. The default is `calc(3ch + 40px)`: a fixed base plus a " +
                    "`3ch` term that scales with the font size. For one-off values, use an " +
                    "arbitrary length or percentage.",
            preview = { ShimmerSpreadRecipe() },
        ),
        ComponentExample(
            "Angle",
            code = shimmerAngleRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-angle-<number>` to set the tilt of the highlight band in " +
                    "degrees. The default is `20`.",
            preview = { ShimmerAngleRecipe() },
        ),
        ComponentExample(
            "Reverse",
            code = shimmerReverseRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-reverse` to sweep the highlight in the opposite direction. In " +
                    "RTL layouts the sweep already follows the reading direction. The official " +
                    "page documents this class with code only; the preview above is added for " +
                    "the Compose page.",
            preview = { ShimmerReverseRecipe() },
        ),
        ComponentExample(
            "Play Once",
            code = shimmerOnceRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-once` to play a single sweep instead of looping, useful as a " +
                    "reveal when streaming completes. Pair it with `shimmer-duration-<number>` " +
                    "to control how long the sweep takes.",
            preview = { ShimmerOnceRecipe() },
        ),
        ComponentExample(
            "Disabling the Shimmer",
            code = shimmerNoneRecipeCode,
            descriptionMarkdown =
                "Use `shimmer-none` to turn the effect off and render the text normally. It " +
                    "works in any class order, so the typical use is responsive or stateful:",
            preview = { ShimmerNoneRecipe() },
        ),
        ComponentExample(
            "RTL",
            code = shimmerRtlRecipeCode,
            descriptionMarkdown =
                "To enable RTL support in shadcn/ui, see the " +
                    "[RTL configuration guide](https://ui.shadcn.com/docs/rtl). The sweep follows " +
                    "the reading direction, left to right in LTR and right to left in RTL, with " +
                    "no extra classes. Like the CSS, `reverse = true` selects the reverse " +
                    "direction; it does not cancel the RTL default.",
            preview = { ShimmerRtlRecipe() },
        ),
    )

internal val shimmerKotlinExamples =
    listOf(
        ComponentExample(
            title = "Kotlin extension: Overlay modifier",
            code = shimmerOverlayModifierRecipeCode,
            preview = { ShimmerOverlayModifierRecipe() },
        ),
        ComponentExample(
            title = "Kotlin extension: Style content brush",
            code = shimmerStyleContentBrushRecipeCode,
            preview = { ShimmerStyleContentBrushRecipe() },
        ),
        ComponentExample(
            title = "Kotlin extension: Duration, angle, spread",
            code = shimmerDurationAngleSpreadRecipeCode,
            preview = { ShimmerDurationAngleSpreadRecipe() },
        ),
        ComponentExample(
            title = "Kotlin extension: Reverse and once",
            code = shimmerReverseAndOnceRecipeCode,
            preview = { ShimmerReverseAndOnceRecipe() },
        ),
    )
