@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.marker.*
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.DefaultShimmerSpread
import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

// region shared
private const val ShimmerMessage = "Generating response…"

@Composable
private fun shimmerViewportWidth(): Dp =
    with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }

@Composable
private fun ShimmerText(
    value: String = ShimmerMessage,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color? = null,
    duration: Int = 2000,
    spread: Dp = DefaultShimmerSpread,
    angle: Float = 20f,
    reverse: Boolean = false,
    once: Boolean = false,
) {
    Text(
        modifier,
        textStyle =
            TextStyle(
                color = currentTheme.colors.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
            ),
        style =
            rememberShimmerStyle(
                enabled =
                enabled,
                color =
                color,
                durationMillis =
                duration,
                spread =
                spread,
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
internal fun ShimmerDefaultRecipe() {
    Box(Modifier.testTag("shimmer-example")) { ShimmerText(modifier = Modifier.testTag("shimmer-text")) }
}
// endregion default

// region marker
@Composable
internal fun ShimmerMarkerRecipe() {
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("shimmer-example"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Marker(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
            MarkerIcon { Spinner() }
            MarkerContent("Thinking...", style = rememberShimmerStyle())
        }
        Marker(
            Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            variant = MarkerVariant.Separator,
        ) { MarkerContent("Reading 4 files", style = rememberShimmerStyle()) }
    }
}
// endregion marker

// region color
@Composable
internal fun ShimmerColorRecipe() {
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
internal fun ShimmerDurationRecipe() {
    ShimmerComparison("shimmer", "shimmer-duration-1000", {
        ShimmerText(modifier = Modifier.testTag("shimmer-default"))
    }, { ShimmerText(duration = 1000, modifier = Modifier.testTag("shimmer-fast")) })
}
// endregion duration

// region spread
@Composable
internal fun ShimmerSpreadRecipe() {
    ShimmerComparison("shimmer-spread-4", "shimmer-spread-24", {
        ShimmerText(spread = 16.dp, modifier = Modifier.testTag("shimmer-narrow"))
    }, { ShimmerText(spread = 96.dp, modifier = Modifier.testTag("shimmer-wide")) })
}
// endregion spread

// region angle
@Composable
internal fun ShimmerAngleRecipe() {
    ShimmerComparison("shimmer", "shimmer-angle-45", {
        ShimmerText(modifier = Modifier.testTag("shimmer-default"))
    }, { ShimmerText(angle = 45f, modifier = Modifier.testTag("shimmer-angle")) })
}
// endregion angle

// region reverse
@Composable
internal fun ShimmerReverseRecipe() {
    ShimmerComparison("shimmer", "shimmer-reverse", {
        ShimmerText(modifier = Modifier.testTag("shimmer-forward"))
    }, {
        ShimmerText(modifier = Modifier.testTag("shimmer-reverse"), reverse = true)
    })
}
// endregion reverse

// region once
@Composable
internal fun ShimmerOnceRecipe() {
    var replay by remember { mutableIntStateOf(0) }
    Column(
        Modifier.testTag("shimmer-example"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        key(replay) { ShimmerText(duration = 1100, once = true, modifier = Modifier.testTag("shimmer-once")) }
        Button("Replay", variant = ButtonVariant.Outline, size = ButtonSize.Sm, onClick = { replay++ })
    }
}
// endregion once

// region none
@Composable
internal fun ShimmerNoneRecipe() {
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
internal fun ShimmerRtlRecipe() {
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
