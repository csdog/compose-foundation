@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.slider.Slider
import com.toutiaoyan.compose.ui.components.slider.SliderOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import kotlin.math.roundToInt

// region usage
@Composable
internal fun SliderUsageExample() {
    var value by remember { mutableStateOf(33f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 1f,
    )
}
// endregion usage

// region default
@Composable
internal fun SliderDemoPreview() {
    var value by remember { mutableStateOf(75f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 1f,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("slider-default"),
        label = "Volume",
    )
}
// endregion default

// region range
@Composable
internal fun SliderRangePreview() {
    var range by remember { mutableStateOf(25f..50f) }
    Slider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        step = 5f,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("slider-range"),
        label = "Range",
    )
}
// endregion range

// region multiple
@Composable
internal fun SliderMultiplePreview() {
    var values by remember { mutableStateOf(listOf(10f, 20f, 70f)) }
    Slider(
        values = values,
        onValuesChange = { values = it },
        valueRange = 0f..100f,
        step = 10f,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("slider-multiple"),
        label = "Multiple thumbs",
    )
}
// endregion multiple

// region vertical
@Composable
internal fun SliderVerticalPreview() {
    var first by remember { mutableStateOf(50f) }
    var second by remember { mutableStateOf(25f) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            value = first,
            onValueChange = { first = it },
            valueRange = 0f..100f,
            step = 1f,
            orientation = SliderOrientation.Vertical,
            modifier = Modifier.height(160.dp).testTag("slider-vertical-a"),
            label = "Vertical 50",
        )
        Slider(
            value = second,
            onValueChange = { second = it },
            valueRange = 0f..100f,
            step = 1f,
            orientation = SliderOrientation.Vertical,
            modifier = Modifier.height(160.dp).testTag("slider-vertical-b"),
            label = "Vertical 25",
        )
    }
}
// endregion vertical

// region controlled
@Composable
internal fun SliderControlledPreview() {
    var range by remember { mutableStateOf(0.3f..0.7f) }
    val values = listOf(range.start, range.endInclusive)
    Column(
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("slider-controlled"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Label("Temperature")
            Text(textStyle = TextColors.Muted) {
                values.joinToString(", ") { ((it * 10).roundToInt() / 10f).toString() }
            }
        }
        Slider(
            value = range,
            onValueChange = { range = it },
            valueRange = 0f..1f,
            step = 0.1f,
            modifier = Modifier.fillMaxWidth(),
            label = "Temperature",
        )
    }
}
// endregion controlled

// region disabled
@Composable
internal fun SliderDisabledPreview() {
    var value by remember { mutableStateOf(50f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 1f,
        disabled = true,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("slider-disabled"),
        label = "Disabled",
    )
}
// endregion disabled

// region rtl
@Composable
internal fun SliderRtlPreview(language: String = "ar") {
    var value by remember(language) { mutableStateOf(75f) }
    val direction = if (language == "ar" || language == "he") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Box(
            Modifier.widthIn(max = 320.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Slider(
                value = value,
                onValueChange = { value = it },
                valueRange = 0f..100f,
                step = 1f,
                modifier = Modifier.fillMaxWidth().testTag("slider-rtl"),
                label = "RTL slider",
            )
        }
    }
}
// endregion rtl

// region step
@Composable
internal fun SliderStepExtensionPreview() {
    var value by remember { mutableStateOf(50f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 10f,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
        label = "Step 10",
    )
}
// endregion step

// region inverted
@Composable
internal fun SliderInvertedExtensionPreview() {
    var value by remember { mutableStateOf(50f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        inverted = true,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
        label = "Inverted",
    )
}
// endregion inverted

// region commit
@Composable
internal fun SliderCommitExtensionPreview() {
    var value by remember { mutableStateOf(50f) }
    var committed by remember { mutableStateOf<Float?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Slider(
            value = value,
            onValueChange = { value = it },
            valueRange = 0f..100f,
            onValueCommit = { committed = it },
            modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
            label = "Commit",
        )
        Text(textStyle = TextColors.Muted) {
            "Committed: " + (committed?.roundToInt()?.toString() ?: "not yet")
        }
    }
}
// endregion commit

// region minSteps
@Composable
internal fun SliderMinStepsExtensionPreview() {
    var range by remember { mutableStateOf(25f..75f) }
    Slider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        step = 1f,
        minStepsBetweenThumbs = 10,
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
        label = "Min steps 10",
    )
}
// endregion minSteps
