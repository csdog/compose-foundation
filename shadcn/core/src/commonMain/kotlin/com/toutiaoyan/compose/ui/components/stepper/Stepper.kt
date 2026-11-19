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
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing
import androidx.compose.ui.text.TextStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

internal data class StepperStep(
    val title: String,
    val description: String?,
    val disabled: Boolean,
)

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
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val rootModifier =
        if (orientation == StepperOrientation.Horizontal) {
            modifier.fillMaxWidth()
        } else {
            modifier
        }
    CompositionLocalProvider(LocalStepperState provides StepperState(currentStep, orientation)) {
        Column(modifier = rootModifier.styleable(rootStyleState, style)) {
            val steps = collectStepperSteps { StepperScope().content() }
            when (orientation) {
                StepperOrientation.Horizontal ->
                    HorizontalStepper(steps, currentStep, showLabels, circleStyle, connectorStyle)
                StepperOrientation.Vertical ->
                    VerticalStepper(steps, currentStep, showLabels, circleStyle, connectorStyle)
            }
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
    DeclareStepperStep(StepperStep(title, description, disabled))
}

private const val STEPPER_DISABLED_ALPHA = 0.5f
private val StepCircleSize = 32.dp
private val LabeledVerticalConnectorHeight = 40.dp
private val CompactVerticalConnectorHeight = 16.dp

/** Numbered circle of a step. Matches community shadcn `StepperIndicator`. */
@Composable
fun StepperIndicator(
    index: Int,
    currentStep: Int =
        LocalStepperState.current?.currentStep ?: error(
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
    isDisabled: Boolean = false,
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
                modifier =
                    Modifier.semantics {
                        stateDescription =
                            when {
                                active -> "Current"
                                reached -> "Completed"
                                else -> "Upcoming"
                            }
                        if (isDisabled) disabled()
                    },
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
    circleStyle: Style,
    connectorStyle: Style,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { index, step ->
            Box(modifier = Modifier.then(if (step.disabled) Modifier.alpha(STEPPER_DISABLED_ALPHA) else Modifier)) {
                StepCircle(index, currentStep, circleStyle, isDisabled = step.disabled)
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
                StepperLabel(
                    step = step,
                    upcoming = index > currentStep,
                    modifier =
                        Modifier.weight(1f).then(
                            if (step.disabled) Modifier.alpha(STEPPER_DISABLED_ALPHA) else Modifier,
                        ),
                )
                if (index != steps.lastIndex) {
                    Box(modifier = Modifier.width(twSpacing.md))
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
    circleStyle: Style,
    connectorStyle: Style,
) {
    steps.forEachIndexed { index, step ->
        Row(modifier = Modifier.then(if (step.disabled) Modifier.alpha(STEPPER_DISABLED_ALPHA) else Modifier)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StepCircle(index, currentStep, circleStyle, isDisabled = step.disabled)
                if (index != steps.lastIndex) {
                    StepperSeparator(
                        active = index < currentStep,
                        horizontal = false,
                        modifier =
                            Modifier.height(
                                if (showLabels) LabeledVerticalConnectorHeight else CompactVerticalConnectorHeight,
                            ),
                        style = connectorStyle,
                    )
                }
            }
            if (showLabels) {
                StepperLabel(
                    step = step,
                    upcoming = index > currentStep,
                    modifier = Modifier.padding(start = twSpacing.sm, top = twSpacing.xs2),
                )
            }
        }
    }
}

@Composable
private fun StepperLabel(
    step: StepperStep,
    upcoming: Boolean,
    modifier: Modifier,
) {
    Column(modifier) {
        StepperTitle(text = step.title, upcoming = upcoming)
        step.description?.let { StepperDescription(text = it) }
    }
}
