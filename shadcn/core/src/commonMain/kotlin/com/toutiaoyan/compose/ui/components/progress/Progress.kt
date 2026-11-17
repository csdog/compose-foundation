@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.progress
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.math.roundToInt

private val LocalProgressValue = staticCompositionLocalOf<Float?> { null }

private val LocalProgressIndeterminate = staticCompositionLocalOf { false }

private class ProgressName(text: String) {
    var text by mutableStateOf(text)
}

private val LocalProgressNames = staticCompositionLocalOf<MutableList<ProgressName>?> { null }

private fun progressPercentage(fraction: Float): String = "${(fraction * 100f).roundToInt()}%"

/** Logical fraction of the nearest [Progress]; indicator width animates independently. */
class ProgressState(val value: Float)

/** Reads the nearest [Progress]. */
@Composable
fun progressState(): ProgressState {
    val value =
        checkNotNull(LocalProgressValue.current) {
            "progressState() / ProgressIndicator() must be used inside Progress { … }"
        }
    return ProgressState(value)
}

/**
 * A linear progress bar. Matches base-nova `progress.tsx` /
 * `.cn-progress-track` (`h-1 w-full rounded-full bg-muted`, indicator `bg-primary`).
 *
 * [style] applies to the root. The content slot adds labels or other content;
 * the root always appends its track, matching the official component.
 * The accessibility range uses [max]. [getValueLabel] receives the normalized fraction.
 * Use standalone [ProgressTrack] and [ProgressIndicator] for a custom track.
 *
 * Usage:
 * ```
 * Progress(value = 0.6f)
 * Progress(value = 0.6f) { ProgressLabel("Upload"); ProgressValue() }
 * Progress(value = 0f, indeterminate = true)
 * Progress(value = 60f, max = 100f)
 * Progress(value = 0.56f) {
 *     ProgressLabel("Upload progress")
 *     ProgressValue()
 * }
 * ```
 */
@Composable
fun Progress(
    value: Float,
    modifier: Modifier = Modifier,
    max: Float = 1f,
    indeterminate: Boolean = false,
    getValueLabel: ((Float) -> String)? = null,
    style: Style = Style,
    content: (@Composable () -> Unit)? = null,
) {
    val validMax = max.isFinite() && max > 0f
    val rangeMax = if (validMax) max else 1f
    val current = if (validMax && !value.isNaN()) value.coerceIn(0f, max) else 0f
    val fraction = current / rangeMax
    val names = remember { mutableStateListOf<ProgressName>() }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val semanticsModifier =
        Modifier.semantics(mergeDescendants = true) {
            progressBarRangeInfo =
                if (indeterminate) {
                    ProgressBarRangeInfo.Indeterminate
                } else {
                    ProgressBarRangeInfo(
                        current = current,
                        range = 0f..rangeMax,
                        steps = 0,
                    )
                }
            val name = names.map { it.text }.filter { it.isNotBlank() }.joinToString(", ")
            if (name.isNotEmpty()) contentDescription = name
            if (!indeterminate) {
                stateDescription = getValueLabel?.invoke(fraction) ?: progressPercentage(fraction)
            }
        }
    CompositionLocalProvider(
        LocalProgressValue provides fraction,
        LocalProgressIndeterminate provides indeterminate,
        LocalProgressNames provides names,
    ) {
        ProgressRootLayout(modifier.then(semanticsModifier).styleable(styleState, Style { width(1f) } then style)) {
            content?.invoke()
            ProgressTrack()
        }
    }
}

/**
 * Official Base `ProgressTrack`: the `h-1` bar. Default [Progress] (no content
 * lambda) is this track plus indicator.
 */
@Composable
fun ProgressTrack(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit = { ProgressIndicator() },
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                rememberProgressTrackStyle() then style,
            ).clip(RoundedCornerShape(twRadius.full)),
        content = content,
    )
}

/** Official Base `ProgressLabel` (`text-sm font-medium`). */
@Composable
fun ProgressLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    progressState()
    val names = LocalProgressNames.current
    val name = remember { ProgressName(text) }
    SideEffect { name.text = text }
    DisposableEffect(names, name) {
        names?.add(name)
        onDispose { names?.remove(name) }
    }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                )
            } else {
                TextStyle(fontWeight = FontWeight.Medium)
            },
        modifier = modifier.styleable(styleState, style),
    ) { text }
}

/** Official Aria `ProgressValue`. Defaults to `56%` from the current fraction. */
@Composable
fun ProgressValue(
    text: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val state = progressState()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val label = text ?: if (LocalProgressIndeterminate.current) "" else progressPercentage(state.value)
    Box(
        modifier =
            modifier
                .layoutId(ProgressValueLayoutId)
                .clearAndSetSemantics { hideFromAccessibility() },
        propagateMinConstraints = true,
    ) {
        Text(
            variant = TextVariant.BodySmall,
            textStyle =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    TextColors.Muted.copy(
                        fontFeatureSettings = "tnum",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    )
                } else {
                    TextColors.Muted
                },
            modifier = Modifier.styleable(styleState, style),
        ) { label }
    }
}

/** Filled portion of a [Progress] track. Matches shadcn's `ProgressIndicator`. */
@Composable
fun BoxScope.ProgressIndicator(
    value: Float =
        checkNotNull(LocalProgressValue.current) {
            "ProgressIndicator must be used inside Progress, or pass value="
        },
    modifier: Modifier = Modifier,
    indeterminate: Boolean = LocalProgressIndeterminate.current,
) {
    if (indeterminate) {
        val infinite = rememberInfiniteTransition(label = "progress-indeterminate")
        val slide by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "progress-indeterminate-slide",
        )
        BoxWithConstraints(
            modifier = modifier.fillMaxHeight().fillMaxWidth().align(Alignment.CenterStart),
        ) {
            val barWidth = maxWidth * 0.3f
            val travel = maxWidth + barWidth
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(barWidth)
                        .offset(x = travel * slide - barWidth)
                        .background(currentTheme.colors.primary),
            )
        }
    } else {
        val animatedValue =
            animateFloatAsState(
                if (value.isNaN()) 0f else value.coerceIn(0f, 1f),
                animationSpec = tween(150, easing = FastOutSlowInEasing),
                label = "progress-width",
            )
        Box(
            modifier =
                modifier
                    .fillMaxHeight()
                    .layout { measurable, constraints ->
                        val width = (constraints.maxWidth * animatedValue.value).roundToInt()
                        val fill = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                        layout(fill.width, fill.height) { fill.placeRelative(0, 0) }
                    }
                    .align(Alignment.CenterStart)
                    .background(currentTheme.colors.primary),
        )
    }
}
