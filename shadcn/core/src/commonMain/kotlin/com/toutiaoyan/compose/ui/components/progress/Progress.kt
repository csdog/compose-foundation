@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.progress
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.math.roundToInt

private val LocalProgressValue = staticCompositionLocalOf<Float?> { null }

private val LocalProgressIndeterminate = staticCompositionLocalOf { false }

/** Animated fill of the nearest [Progress]. */
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
 * Track chrome is Style-driven — override with [style]. Indicator width stays animated via Modifier.
 *
 * Usage:
 * ```
 * Progress(value = 0.6f)
 * Progress(value = 0.6f) { ProgressTrack { ProgressIndicator() } }
 * Progress(value = 0f, indeterminate = true)
 * Progress(value = 60f, max = 100f)
 * Progress(value = 0.56f) {
 *     ProgressLabel("Upload progress")
 *     ProgressValue()
 *     ProgressTrack()
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
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val fraction = if (max <= 0f) 0f else (value / max).coerceIn(0f, 1f)
    val animatedValue by animateFloatAsState(fraction, label = "progress")
    val semanticsModifier =
        Modifier.semantics {
            progressBarRangeInfo =
                ProgressBarRangeInfo(
                    current = animatedValue,
                    range = 0f..1f,
                    steps = 0,
                )
            if (getValueLabel != null) {
                contentDescription = getValueLabel(animatedValue)
            }
        }
    CompositionLocalProvider(
        LocalProgressValue provides animatedValue,
        LocalProgressIndeterminate provides indeterminate,
    ) {
        if (content != null) {
            Column(
                modifier = modifier.then(semanticsModifier),
                verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                content = content,
            )
        } else {
            ProgressTrack(modifier = modifier.then(semanticsModifier), style = style)
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
            ),
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
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.BodySmall,
        textStyle = TextStyle(fontWeight = FontWeight.Medium),
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
    val label = text ?: "${(state.value * 100f).roundToInt()}%"
    Text(
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
        modifier = modifier.styleable(styleState, style),
    ) { label }
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
                        .background(currentTheme.colors.primary, RoundedCornerShape(twRadius.full)),
            )
        }
    } else {
        Box(
            modifier =
                modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value)
                    .align(Alignment.CenterStart)
                    .background(currentTheme.colors.primary, RoundedCornerShape(twRadius.full)),
        )
    }
}
