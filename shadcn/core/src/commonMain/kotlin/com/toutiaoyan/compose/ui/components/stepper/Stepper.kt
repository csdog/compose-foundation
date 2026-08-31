@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.stepper

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing
import androidx.compose.ui.text.TextStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

private data class StepperStep(
    val title: String,
    val description: String? = null,
    val disabled: Boolean = false,
)

private class StepperCollector {
    private val buffer = mutableListOf<StepperStep>()

    fun begin() {
        buffer.clear()
    }

    fun add(step: StepperStep) {
        buffer.add(step)
    }

    fun snapshot(): List<StepperStep> = buffer.toList()
}

private val LocalStepperCollector = staticCompositionLocalOf<StepperCollector?> { null }

class StepperScope internal constructor()

/** Current index / orientation of the nearest [Stepper]. */
class StepperState(
    val currentStep: Int,
    val orientation: StepperOrientation,
)

private val LocalStepperState = staticCompositionLocalOf<StepperState?> { null }

/** Reads the nearest [Stepper]. */
@Composable
fun stepperState(): StepperState =
    checkNotNull(LocalStepperState.current) {
        "stepperState() must be used inside Stepper { … }"
    }

/** Layout direction for [Stepper] -- [Horizontal] for wide screens, [Vertical] for narrow/mobile or long labels. */
enum class StepperOrientation { Horizontal, Vertical }

/**
 * A multi-step progress indicator matching shadcn/ui community stepper patterns:
 *
 * ```
 * Stepper
 * └── StepperItem
 * ```
 *
 * Numbered circles connected by a line, with each step's title/description
 * alongside. Steps before [currentStep] render as completed (filled), the step
 * at [currentStep] as active (filled + ring), the rest as upcoming (muted).
 *
 * Usage:
 * ```
 * var step by remember { mutableStateOf(0) }
 * Stepper(currentStep = step) {
 *     StepperItem("Details", "Enter the required details")
 *     StepperItem("Review", "Confirm your information")
 *     StepperItem("Done", "All set")
 * }
 * ```
 */
@Composable
fun Stepper(
    currentStep: Int,
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: StepperOrientation = StepperOrientation.Horizontal,
    showLabels: Boolean = true,
    circleStyle: Style = Style,
    connectorStyle: Style = Style,
    content: @Composable StepperScope.() -> Unit,
) {
    val collector = remember { StepperCollector() }
    collector.begin()
    CompositionLocalProvider(
        LocalStepperCollector provides collector,
        LocalStepperState provides StepperState(currentStep, orientation),
    ) {
        StepperScope().content()
        val steps = collector.snapshot()
        when (orientation) {
            StepperOrientation.Horizontal ->
                HorizontalStepper(steps, currentStep, showLabels, modifier, style, circleStyle, connectorStyle)
            StepperOrientation.Vertical ->
                VerticalStepper(steps, currentStep, showLabels, modifier, style, circleStyle, connectorStyle)
        }
    }
}

/** One step: numbered circle plus title and optional description. */
@Composable
fun StepperScope.StepperItem(
    title: String,
    description: String? = null,
    disabled: Boolean = false,
) {
    val collector =
        checkNotNull(LocalStepperCollector.current) {
            "StepperItem must be used inside Stepper { … }"
        }
    collector.add(StepperStep(title = title, description = description, disabled = disabled))
}

private val StepCircleSize = 32.dp

/** Numbered circle of a step. Matches community shadcn `StepperIndicator`. */
@Composable
fun StepperIndicator(
    index: Int,
    currentStep: Int = LocalStepperState.current?.currentStep ?: error(
        "StepperIndicator must be used inside Stepper, or pass currentStep=",
    ),
    circleStyle: Style = Style,
) {
    StepCircle(index = index, currentStep = currentStep, circleStyle = circleStyle)
}

/** Title line under/beside a step. Matches community shadcn `StepperTitle`. */
@Composable
fun StepperTitle(
    text: String,
    upcoming: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.LabelLarge,
        textStyle = if (upcoming) TextColors.Muted else TextStyle.Default,
    ) { text }
}

@Composable
@JvmName("StepperTitleLambda")
@OverloadResolutionByLambdaReturnType
fun StepperTitle(
    upcoming: Boolean,
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    StepperTitle(text = text(), upcoming = upcoming, modifier = modifier)
}

/** Supporting copy under [StepperTitle]. Matches community shadcn `StepperDescription`. */
@Composable
fun StepperDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
    ) { text }
}

@Composable
@JvmName("StepperDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun StepperDescription(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    StepperDescription(text = text(), modifier = modifier)
}

/** Line between step circles. Matches community shadcn `StepperSeparator`. */
@Composable
fun StepperSeparator(
    active: Boolean,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                rememberStepConnectorStyle(active = active, horizontal = horizontal) then style,
            ),
    )
}

@Composable
private fun StepCircle(
    index: Int,
    currentStep: Int,
    circleStyle: Style,
) {
    val theme = currentTheme
    val reached = index <= currentStep
    val active = index == currentStep
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    // Real shadcn rings sit *outside* the shape with a gap (`ring-offset`), not flush against its
    // own edge -- a plain `border(...)` at the circle's own size draws inset on the boundary,
    // reading as a thin scratch on the fill rather than a ring. Sizing the bordered Box larger
    // than the filled circle (by ring.width on each side) and centering the circle inside it
    // reproduces the offset: Modifier.border always draws at *its own* Box's edge, which is now
    // further out than the circle underneath.
    Box(
        modifier =
            Modifier
                .size(StepCircleSize + if (active) theme.ring.width * 2 else 0.dp)
                .let {
                    if (active) {
                        it.border(
                            theme.ring.width,
                            theme.colors.ring.copy(alpha = theme.ring.opacity),
                            CircleShape,
                        )
                    } else {
                        it
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier.styleable(
                    styleState,
                    rememberStepCircleStyle(reached) then circleStyle,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                variant = TextVariant.LabelLarge,
                textStyle =
                    if (reached) {
                        TextStyle(color = theme.colors.primaryForeground)
                    } else {
                        TextColors.Muted
                    },
            ) { (index + 1).toString() }
        }
    }
}

@Composable
private fun HorizontalStepper(
    steps: List<StepperStep>,
    currentStep: Int,
    showLabels: Boolean,
    modifier: Modifier,
    style: Style,
    circleStyle: Style,
    connectorStyle: Style,
) {
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(modifier = modifier.fillMaxWidth().styleable(rootStyleState, style)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            steps.forEachIndexed { index, step ->
                Box(modifier = Modifier.then(if (step.disabled) Modifier.alpha(0.5f) else Modifier)) {
                    StepperIndicator(index, currentStep, circleStyle)
                }
                if (index != steps.lastIndex) {
                    StepperSeparator(
                        active = index < currentStep,
                        horizontal = true,
                        modifier = Modifier.weight(1f),
                        style = connectorStyle,
                    )
                }
            }
        }
        if (showLabels) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = twSpacing.sm)) {
                steps.forEachIndexed { index, step ->
                    val upcoming = index > currentStep
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .then(if (step.disabled) Modifier.alpha(0.5f) else Modifier),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        StepperTitle(text = step.title, upcoming = upcoming)
                        step.description?.let { StepperDescription(text = it) }
                    }
                    if (index != steps.lastIndex) {
                        Box(modifier = Modifier.width(twSpacing.md))
                    }
                }
            }
        }
    }
}

@Composable
private fun VerticalStepper(
    steps: List<StepperStep>,
    currentStep: Int,
    showLabels: Boolean,
    modifier: Modifier,
    style: Style,
    circleStyle: Style,
    connectorStyle: Style,
) {
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(modifier = modifier.styleable(rootStyleState, style)) {
        steps.forEachIndexed { index, step ->
            Row(modifier = Modifier.then(if (step.disabled) Modifier.alpha(0.5f) else Modifier)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StepperIndicator(index, currentStep, circleStyle)
                    if (index != steps.lastIndex) {
                        StepperSeparator(
                            active = index < currentStep,
                            horizontal = false,
                            modifier = Modifier.height(if (showLabels) 40.dp else 16.dp),
                            style = connectorStyle,
                        )
                    }
                }
                if (showLabels) {
                    val upcoming = index > currentStep
                    Column(
                        modifier = Modifier.padding(start = twSpacing.sm, top = twSpacing.xs2),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        StepperTitle(text = step.title, upcoming = upcoming)
                        step.description?.let { StepperDescription(text = it) }
                    }
                }
            }
        }
    }
}
