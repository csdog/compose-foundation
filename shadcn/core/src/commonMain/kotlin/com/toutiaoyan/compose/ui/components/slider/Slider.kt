package com.toutiaoyan.compose.ui.components.slider
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import kotlin.math.abs
import kotlin.math.roundToInt

private val THUMB_SIZE = SliderDefaults.ThumbSize
private val TRACK_HEIGHT = SliderDefaults.TrackHeight

/** base-nova Slider tokens (`.cn-slider-track` / `.cn-slider-thumb`). */
object SliderDefaults {
    /** Original preset thumb size, retained for source compatibility. */
    val ThumbSize = 16.dp

    /** base-nova `.cn-slider-thumb` uses `size-3`. */
    val NovaThumbSize = 12.dp

    /** Official horizontal track `h-1` (vertical uses same as thickness). */
    val TrackHeight = 4.dp
}

enum class SliderOrientation { Horizontal, Vertical }

private fun snapSliderValue(
    raw: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
): Float {
    val coerced = raw.coerceIn(range.start, range.endInclusive)
    if (step <= 0f) return coerced
    val n = ((coerced - range.start) / step).roundToInt()
    return (range.start + n * step).coerceIn(range.start, range.endInclusive)
}

@Immutable
class SliderState(
    val value: Float,
    val values: List<Float>,
    val valueRange: ClosedFloatingPointRange<Float>,
    val step: Float,
    val fraction: Float,
    val fractions: List<Float>,
    val usableWidth: Dp,
    val enabled: Boolean,
    val orientation: SliderOrientation,
    val inverted: Boolean,
    val minStepsBetweenThumbs: Int,
    val onValueChange: (Float) -> Unit,
    val onValuesChange: (List<Float>) -> Unit,
) {
    fun visualFraction(valueFraction: Float): Float = if (inverted) 1f - valueFraction else valueFraction
}

internal class SliderController(
    val state: SliderState,
    val interactionSources: List<MutableInteractionSource>,
    val rangeSpan: Float,
    val thumbSize: Dp = THUMB_SIZE,
    val rtl: Boolean = false,
    val focusRequesters: List<FocusRequester> = emptyList(),
    val currentValues: () -> List<Float>,
    val onValuesCommit: ((List<Float>) -> Unit)? = null,
) {
    val interactionSource: MutableInteractionSource
        get() = interactionSources.first()

    fun interactionSourceFor(index: Int): MutableInteractionSource =
        interactionSources.getOrElse(index) { interactionSources.first() }

    fun commit(values: List<Float> = currentValues()) {
        onValuesCommit?.invoke(values)
    }

    fun updateThumb(
        index: Int,
        raw: Float,
    ): List<Float> {
        val current = currentValues()
        if (index !in current.indices) return current
        val range = state.valueRange
        val gap =
            if (state.minStepsBetweenThumbs <= 0) {
                0f
            } else {
                state.minStepsBetweenThumbs * (if (state.step > 0f) state.step else 1f)
            }
        val minBound = if (index == 0) range.start else current[index - 1] + gap
        val maxBound = if (index == current.lastIndex) range.endInclusive else current[index + 1] - gap
        if (minBound > maxBound) return current
        val coerced = snapSliderValue(raw, range, state.step).coerceIn(minBound, maxBound)
        if (coerced == current[index]) return current
        val next = current.toMutableList().also { it[index] = coerced }
        state.onValuesChange(next)
        return next
    }

    fun setFromFraction(fraction: Float) {
        val range = state.valueRange
        val raw = range.start + fraction.coerceIn(0f, 1f) * rangeSpan
        updateThumb(thumbIndexFor(raw), raw)
    }

    fun thumbIndexFor(raw: Float): Int {
        val current = currentValues()
        if (current.size <= 1) return 0
        return current.indices.minBy { abs(current[it] - raw) }
    }
}

/**
 * Visual 0..1 along the track, with 0 at the start edge (left / bottom) measured at the
 * thumb *center*. Matches thumb placement `offset = usableWidth * visualFraction`.
 */
internal fun sliderVisualFractionAlongTrack(
    position: Offset,
    widthPx: Float,
    heightPx: Float,
    thumbSizePx: Float,
    vertical: Boolean,
): Float {
    val usable = if (vertical) heightPx - thumbSizePx else widthPx - thumbSizePx
    if (usable <= 0f) return 0f
    val visual =
        if (vertical) {
            1f - ((position.y - thumbSizePx / 2f) / usable)
        } else {
            (position.x - thumbSizePx / 2f) / usable
        }
    return visual
}

// Compose accessibility divides the range into steps + 1 intervals.
internal fun sliderSemanticsSteps(
    rangeSpan: Float,
    step: Float,
): Int {
    return if (step <= 0f || rangeSpan <= 0f) 0 else ((rangeSpan / step).roundToInt() - 1).coerceAtLeast(0)
}

private val LocalSlider = staticCompositionLocalOf<SliderController?> { null }

/** Reads the nearest [Slider]. Matches Radix slider context. */
@Composable
fun sliderState(): SliderState =
    checkNotNull(LocalSlider.current) {
        "sliderState() must be used inside Slider { … }"
    }.state

/**
 * Usage:
 * ```
 * var volume by remember { mutableStateOf(50f) }
 * Slider(value = volume, onValueChange = { volume = it }, valueRange = 0f..100f)
 * Slider(value = volume, onValueChange = { volume = it }, valueRange = 0f..100f, step = 10f)
 * Slider(value = 25f..75f, onValueChange = { range = it }, valueRange = 0f..100f)
 * Slider(values = listOf(25f, 50f, 75f), onValuesChange = { values = it })
 * Slider(value = volume, onValueChange = { volume = it }, orientation = SliderOrientation.Vertical)
 * Slider(value = volume, onValueChange = { volume = it }, inverted = true)
 * Slider(value = range, onValueChange = { range = it }, minStepsBetweenThumbs = 10)
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    min: Float? = null,
    max: Float? = null,
    step: Float = 0f,
    enabled: Boolean = true,
    disabled: Boolean = false,
    orientation: SliderOrientation = SliderOrientation.Horizontal,
    inverted: Boolean = false,
    minStepsBetweenThumbs: Int = 0,
    label: String = "",
    getValueLabel: ((Float) -> String)? = null,
    onValueCommit: ((Float) -> Unit)? = null,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit = {
        SliderTrack()
        SliderRange()
        SliderThumb(style = style)
    },
) {
    SliderImpl(
        values = listOf(value),
        onValuesChange = { onValueChange(it.first()) },
        modifier = modifier,
        valueRange = resolvedSliderRange(valueRange, min, max),
        step = step,
        enabled = enabled && !disabled && !LocalFieldSetDisabled.current,
        orientation = orientation,
        inverted = inverted,
        minStepsBetweenThumbs = minStepsBetweenThumbs,
        label = label,
        getValueLabel = getValueLabel,
        onValuesCommit = onValueCommit?.let { commit -> { thumbs -> commit(thumbs.first()) } },
        content = content,
    )
}

/** Two-thumb range, matching shadcn `defaultValue={[min, max]}`. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Slider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    min: Float? = null,
    max: Float? = null,
    step: Float = 0f,
    enabled: Boolean = true,
    disabled: Boolean = false,
    orientation: SliderOrientation = SliderOrientation.Horizontal,
    inverted: Boolean = false,
    minStepsBetweenThumbs: Int = 0,
    label: String = "",
    getValueLabel: ((Float) -> String)? = null,
    onValueCommit: ((ClosedFloatingPointRange<Float>) -> Unit)? = null,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit = {
        SliderTrack()
        SliderRange()
        SliderThumb(index = 0, style = style)
        SliderThumb(index = 1, style = style)
    },
) {
    val start = minOf(value.start, value.endInclusive)
    val end = maxOf(value.start, value.endInclusive)
    SliderImpl(
        values = listOf(start, end),
        onValuesChange = { thumbs -> onValueChange(thumbs[0]..thumbs[1]) },
        modifier = modifier,
        valueRange = resolvedSliderRange(valueRange, min, max),
        step = step,
        enabled = enabled && !disabled && !LocalFieldSetDisabled.current,
        orientation = orientation,
        inverted = inverted,
        minStepsBetweenThumbs = minStepsBetweenThumbs,
        label = label,
        getValueLabel = getValueLabel,
        onValuesCommit = onValueCommit?.let { commit -> { thumbs -> commit(thumbs[0]..thumbs[1]) } },
        content = content,
    )
}

/** N thumbs, matching shadcn / Radix `value={number[]}`. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Slider(
    values: List<Float>,
    onValuesChange: (List<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    min: Float? = null,
    max: Float? = null,
    step: Float = 0f,
    enabled: Boolean = true,
    disabled: Boolean = false,
    orientation: SliderOrientation = SliderOrientation.Horizontal,
    inverted: Boolean = false,
    minStepsBetweenThumbs: Int = 0,
    label: String = "",
    getValueLabel: ((Float) -> String)? = null,
    onValuesCommit: ((List<Float>) -> Unit)? = null,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit = {
        val count = sliderState().values.size.coerceAtLeast(1)
        SliderTrack()
        SliderRange()
        repeat(count) { SliderThumb(index = it, style = style) }
    },
) {
    val resolvedRange = resolvedSliderRange(valueRange, min, max)
    SliderImpl(
        values = values.ifEmpty { listOf(resolvedRange.start) },
        onValuesChange = onValuesChange,
        modifier = modifier,
        valueRange = resolvedRange,
        step = step,
        enabled = enabled && !disabled && !LocalFieldSetDisabled.current,
        orientation = orientation,
        inverted = inverted,
        minStepsBetweenThumbs = minStepsBetweenThumbs,
        label = label,
        getValueLabel = getValueLabel,
        onValuesCommit = onValuesCommit,
        content = content,
    )
}

private fun resolvedSliderRange(
    valueRange: ClosedFloatingPointRange<Float>,
    min: Float?,
    max: Float?,
): ClosedFloatingPointRange<Float> {
    if (min == null && max == null) return valueRange
    return (min ?: valueRange.start)..(max ?: valueRange.endInclusive)
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
private fun SliderImpl(
    values: List<Float>,
    onValuesChange: (List<Float>) -> Unit,
    modifier: Modifier,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float,
    enabled: Boolean,
    orientation: SliderOrientation,
    inverted: Boolean,
    minStepsBetweenThumbs: Int,
    label: String,
    getValueLabel: ((Float) -> String)? = null,
    onValuesCommit: ((List<Float>) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    require(valueRange.start <= valueRange.endInclusive) { "Slider range must be increasing" }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val thumbSize = if (nova) SliderDefaults.NovaThumbSize else THUMB_SIZE
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val rangeSpan = valueRange.endInclusive - valueRange.start
    val fractions =
        values.map { value ->
            if (rangeSpan == 0f) 0f else ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)
        }
    val currentValues = rememberUpdatedState(values)
    val currentCommit = rememberUpdatedState(onValuesCommit)
    val interactionSources =
        remember(values.size) { List(values.size.coerceAtLeast(1)) { MutableInteractionSource() } }

    val focusRequesters = remember(values.size) { List(values.size.coerceAtLeast(1)) { FocusRequester() } }
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val outerStyleState = rememberUpdatedStyleState(null)
    val first = values.first()
    val vertical = orientation == SliderOrientation.Vertical
    val geometryStyle =
        if (nova) {
            Style {
                if (vertical) {
                    minWidth(thumbSize)
                    minHeight(160.dp)
                } else {
                    minHeight(thumbSize)
                }
            }
        } else {
            Style
        }

    Box(
        modifier =
            modifier
                .then(if (vertical) Modifier.fillMaxHeight() else Modifier.fillMaxWidth())
                .styleable(outerStyleState, geometryStyle then touchTargetStyle),
        contentAlignment = if (vertical) Alignment.BottomCenter else Alignment.CenterStart,
    ) {
        BoxWithConstraints(
            modifier =
                Modifier
                    .matchParentSize()
                    .semantics(mergeDescendants = true) {
                        role = Role.ValuePicker
                        val valueText = getValueLabel?.invoke(first)
                        val description =
                            when {
                                label.isNotEmpty() && valueText != null -> "$label, $valueText"
                                valueText != null -> valueText
                                label.isNotEmpty() -> label
                                else -> null
                            }
                        if (description != null) {
                            contentDescription = description
                        }
                        if (!enabled) {
                            disabled()
                        }
                        progressBarRangeInfo =
                            ProgressBarRangeInfo(
                                current = first,
                                range = valueRange,
                                steps = sliderSemanticsSteps(rangeSpan, step),
                            )
                        setProgress { target ->
                            if (!enabled) {
                                false
                            } else {
                                val snapped = snapSliderValue(target, valueRange, step)
                                val committed =
                                    if (values.size <= 1) {
                                        listOf(snapped)
                                    } else {
                                        val index = values.indices.minBy { abs(values[it] - snapped) }
                                        val gap =
                                            if (minStepsBetweenThumbs <= 0) {
                                                0f
                                            } else {
                                                minStepsBetweenThumbs * (if (step > 0f) step else 1f)
                                            }
                                        val minBound =
                                            if (index == 0) valueRange.start else values[index - 1] + gap
                                        val maxBound =
                                            if (index == values.lastIndex) {
                                                valueRange.endInclusive
                                            } else {
                                                values[index + 1] - gap
                                            }
                                        if (minBound > maxBound) {
                                            values
                                        } else {
                                            values.toMutableList().also {
                                                it[index] = snapped.coerceIn(minBound, maxBound)
                                            }
                                        }
                                    }
                                if (values.size <= 1 || committed !== values) {
                                    onValuesChange(committed)
                                }
                                currentCommit.value?.invoke(committed)
                                true
                            }
                        }
                    }
                    .alpha(if (enabled) 1f else 0.5f),
            contentAlignment = if (vertical) Alignment.BottomCenter else Alignment.CenterStart,
        ) {
            val usableWidth =
                if (vertical) {
                    (maxHeight - thumbSize).coerceAtLeast(
                        0.dp,
                    )
                } else {
                    (maxWidth - thumbSize).coerceAtLeast(0.dp)
                }
            val controller =
                SliderController(
                    state =
                        SliderState(
                            value = first,
                            values = values,
                            valueRange = valueRange,
                            step = step,
                            fraction = fractions.first(),
                            fractions = fractions,
                            usableWidth = usableWidth,
                            enabled = enabled,
                            orientation = orientation,
                            inverted = inverted,
                            minStepsBetweenThumbs = minStepsBetweenThumbs,
                            onValueChange = { onValuesChange(listOf(it)) },
                            onValuesChange = onValuesChange,
                        ),
                    interactionSources = interactionSources,
                    rangeSpan = rangeSpan,
                    thumbSize = thumbSize,
                    rtl = rtl,
                    focusRequesters = focusRequesters,
                    currentValues = { currentValues.value },
                    onValuesCommit = { currentCommit.value?.invoke(it) },
                )
            CompositionLocalProvider(LocalSlider provides controller) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .sliderPointerInput(controller),
                    contentAlignment = if (vertical) Alignment.BottomCenter else Alignment.CenterStart,
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun Modifier.sliderPointerInput(controller: SliderController): Modifier {
    val latest = rememberUpdatedState(controller)
    return pointerInput(
        controller.state.enabled,
        controller.state.orientation,
        controller.state.inverted,
        controller.rangeSpan,
        controller.thumbSize,
        controller.state.values.size,
    ) {
        val thumbSizePx = controller.thumbSize.toPx()
        awaitEachGesture {
            val slider = latest.value
            if (!slider.state.enabled) return@awaitEachGesture
            val down = awaitFirstDown()
            down.consume()
            val vertical = slider.state.orientation == SliderOrientation.Vertical

            fun valueAt(position: Offset): Float {
                val visual =
                    sliderVisualFractionAlongTrack(
                        position = position,
                        widthPx = size.width.toFloat(),
                        heightPx = size.height.toFloat(),
                        thumbSizePx = thumbSizePx,
                        vertical = vertical,
                    )
                val valueFraction =
                    if (slider.state.inverted xor (!vertical && slider.rtl)) 1f - visual else visual
                return slider.state.valueRange.start +
                    valueFraction.coerceIn(0f, 1f) * slider.rangeSpan
            }
            val raw = valueAt(down.position)
            val index = slider.thumbIndexFor(raw)
            slider.focusRequesters.getOrNull(index)?.requestFocus()
            val press = PressInteraction.Press(down.position)
            val source = slider.interactionSourceFor(index)
            source.tryEmit(press)
            slider.updateThumb(index, raw)
            var completed = false
            try {
                completed =
                    drag(down.id) { change ->
                        change.consume()
                        slider.updateThumb(index, valueAt(change.position))
                    }
                slider.commit()
            } finally {
                source.tryEmit(if (completed) PressInteraction.Release(press) else PressInteraction.Cancel(press))
            }
        }
    }
}

/** Unfilled track of a [Slider]. Base UI does not export SliderTrack. */
@Composable
internal fun SliderTrack(modifier: Modifier = Modifier) {
    val slider = LocalSlider.current
    val resolved =
        if (slider != null) {
            val vertical = slider.state.orientation == SliderOrientation.Vertical
            val sized =
                if (vertical) {
                    Modifier.fillMaxHeight().width(TRACK_HEIGHT)
                } else {
                    Modifier.fillMaxWidth().height(TRACK_HEIGHT)
                }
            sized.then(modifier)
        } else {
            modifier
        }
    val styleState = rememberUpdatedStyleState(null)
    Box(modifier = resolved.styleable(styleState, rememberSliderTrackStyle()))
}

/** Filled range of a [Slider]. Base UI does not export SliderRange. */
@Composable
internal fun SliderRange(modifier: Modifier = Modifier) {
    val slider = LocalSlider.current
    val resolved =
        if (slider != null) {
            val fractions = slider.state.fractions
            val visualFractions = fractions.map { slider.state.visualFraction(it) }
            val startFraction =
                if (fractions.size <= 1) {
                    if (slider.state.inverted) visualFractions.firstOrNull() ?: 0f else 0f
                } else {
                    visualFractions.minOrNull() ?: 0f
                }
            val endFraction =
                if (fractions.size <= 1) {
                    if (slider.state.inverted) 1f else (visualFractions.firstOrNull() ?: 0f)
                } else {
                    visualFractions.maxOrNull() ?: 0f
                }
            val nova = currentTheme.activePreset == StylePreset.Nova
            val start =
                if (nova &&
                    (
                        fractions.size > 1 ||
                            slider.state.inverted
                    )
                ) {
                    slider.thumbSize / 2 +
                        slider.state.usableWidth * startFraction
                } else {
                    slider.state.usableWidth * startFraction
                }
            val end =
                if (nova &&
                    fractions.size <= 1 &&
                    slider.state.inverted
                ) {
                    slider.state.usableWidth +
                        slider.thumbSize
                } else {
                    slider.state.usableWidth * endFraction +
                        slider.thumbSize / 2
                }
            val length = (end - start).coerceAtLeast(0.dp)
            val vertical = slider.state.orientation == SliderOrientation.Vertical
            val sized =
                if (vertical) {
                    Modifier.height(length).width(TRACK_HEIGHT)
                } else {
                    Modifier.width(length).height(TRACK_HEIGHT)
                }
            if (start == 0.dp) {
                sized.then(modifier)
            } else if (vertical) {
                Modifier.offset(y = -start).then(sized).then(modifier)
            } else {
                Modifier.offset(x = start).then(sized).then(modifier)
            }
        } else {
            modifier
        }
    val styleState = rememberUpdatedStyleState(null)
    Box(modifier = resolved.styleable(styleState, rememberSliderRangeStyle()))
}

/** Draggable knob of a [Slider]. Base UI does not export SliderThumb. */
@Composable
internal fun SliderThumb(
    modifier: Modifier = Modifier,
    index: Int = 0,
    interactionSource: MutableInteractionSource =
        LocalSlider.current?.interactionSourceFor(index) ?: remember { MutableInteractionSource() },
    style: Style = Style,
) {
    val slider = LocalSlider.current
    val resolved =
        if (slider != null) {
            val fraction =
                slider.state.visualFraction(
                    slider.state.fractions.getOrElse(index) { slider.state.fraction },
                )
            val vertical = slider.state.orientation == SliderOrientation.Vertical
            val offset =
                if (vertical) {
                    Modifier.offset(y = -slider.state.usableWidth * fraction)
                } else {
                    Modifier.offset(x = slider.state.usableWidth * fraction)
                }
            offset
                .size(slider.thumbSize)
                .keyboardInputMode()
                .focusRequester(slider.focusRequesters[index])
                .onPreviewKeyEvent { event ->
                    if (!slider.state.enabled || event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    val state = slider.state
                    val unit = if (state.step > 0f) state.step else slider.rangeSpan / 100f
                    val delta = if (event.isShiftPressed) unit * 10 else unit
                    val reverse = if (state.inverted xor slider.rtl) -1 else 1
                    val value = state.values[index]
                    val target =
                        when (event.key) {
                            Key.MoveHome -> state.valueRange.start
                            Key.MoveEnd -> state.valueRange.endInclusive
                            Key.PageUp -> value + unit * 10
                            Key.PageDown -> value - unit * 10
                            Key.DirectionRight -> value + delta * reverse
                            Key.DirectionLeft -> value - delta * reverse
                            Key.DirectionUp -> value + delta * (if (state.inverted) -1 else 1)
                            Key.DirectionDown -> value - delta * (if (state.inverted) -1 else 1)
                            else -> return@onPreviewKeyEvent false
                        }
                    slider.commit(slider.updateThumb(index, target))
                    true
                }
                .semantics {
                    progressBarRangeInfo =
                        ProgressBarRangeInfo(
                            slider.state.values[index],
                            slider.state.valueRange,
                            steps = sliderSemanticsSteps(slider.rangeSpan, slider.state.step),
                        )
                    setProgress { target ->
                        if (!slider.state.enabled) {
                            false
                        } else {
                            slider.commit(slider.updateThumb(index, target))
                            true
                        }
                    }
                    if (!slider.state.enabled) disabled()
                }
                .focusable(enabled = slider.state.enabled, interactionSource = interactionSource)
                .then(modifier)
        } else {
            modifier
        }
    val styleState = rememberUpdatedStyleState(interactionSource)
    Box(modifier = resolved) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .styleable(styleState, rememberSliderThumbStyle(), style),
        )
    }
}
