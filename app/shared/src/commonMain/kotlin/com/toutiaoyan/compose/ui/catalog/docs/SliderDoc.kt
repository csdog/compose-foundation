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

@Composable
private fun sliderWidth() = Modifier.widthIn(max = 320.dp).fillMaxWidth()

@Composable
private fun SliderDemoPreview() {
    var value by remember { mutableStateOf(75f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 1f,
        modifier = sliderWidth().testTag("slider-default"),
        label = "Volume",
    )
}

@Composable
private fun SliderRangePreview() {
    var range by remember { mutableStateOf(25f..50f) }
    Slider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        step = 5f,
        modifier = sliderWidth().testTag("slider-range"),
        label = "Range",
    )
}

@Composable
private fun SliderMultiplePreview() {
    var values by remember { mutableStateOf(listOf(10f, 20f, 70f)) }
    Slider(
        values = values,
        onValuesChange = { values = it },
        valueRange = 0f..100f,
        step = 10f,
        modifier = sliderWidth().testTag("slider-multiple"),
        label = "Multiple thumbs",
    )
}

@Composable
private fun SliderVerticalPreview() {
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

@Composable
private fun SliderControlledPreview() {
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

@Composable
private fun SliderDisabledPreview() {
    var value by remember { mutableStateOf(50f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 1f,
        disabled = true,
        modifier = sliderWidth().testTag("slider-disabled"),
        label = "Disabled",
    )
}

@Composable
private fun SliderRtlPreview() {
    var language by remember { mutableStateOf(1) }
    RtlPreview(
        language,
        listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
        { language = it },
    ) {
        var value by remember(language) { mutableStateOf(75f) }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
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
}

@Composable
private fun SliderStepExtensionPreview() {
    var value by remember { mutableStateOf(50f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        step = 10f,
        modifier = sliderWidth(),
        label = "Step 10",
    )
}

@Composable
private fun SliderInvertedExtensionPreview() {
    var value by remember { mutableStateOf(50f) }
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        inverted = true,
        modifier = sliderWidth(),
        label = "Inverted",
    )
}

@Composable
private fun SliderCommitExtensionPreview() {
    var value by remember { mutableStateOf(50f) }
    var committed by remember { mutableStateOf<Float?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Slider(
            value = value,
            onValueChange = { value = it },
            valueRange = 0f..100f,
            onValueCommit = { committed = it },
            modifier = sliderWidth(),
            label = "Commit",
        )
        Text(textStyle = TextColors.Muted) {
            "Committed: " + (committed?.roundToInt()?.toString() ?: "not yet")
        }
    }
}

@Composable
private fun SliderMinStepsExtensionPreview() {
    var range by remember { mutableStateOf(25f..75f) }
    Slider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        step = 1f,
        minStepsBetweenThumbs = 10,
        modifier = sliderWidth(),
        label = "Min steps 10",
    )
}

val sliderDoc =
    ComponentDoc(
        id = "slider",
        title = "Slider",
        description = "An input where the user selects a value from within a given range.",
        usageCode =
            """
            var value by remember { mutableStateOf(33f) }
            Slider(
                value = value,
                onValueChange = { value = it },
                valueRange = 0f..100f,
                step = 1f,
            )
            """.trimIndent(),
        usageImportsCode =
            "import androidx.compose.runtime.*\n" +
                "import androidx.compose.ui.Modifier\n" +
                "import androidx.compose.ui.unit.dp\n" +
                "import com.toutiaoyan.compose.ui.components.slider.*",
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    "Slider",
                    "A controlled slider with one thumb, a range or any number of thumbs. See the " +
                        "[Base UI Slider](https://base-ui.com/react/components/slider#api-reference) " +
                        "documentation for the original props.",
                    properties =
                        listOf(
                            ApiProperty("value / values", "Float / range / List<Float>", "required"),
                            ApiProperty("valueRange", "ClosedFloatingPointRange<Float>", "0f..1f"),
                            ApiProperty("step", "Float", "0f (continuous)"),
                            ApiProperty("orientation", "SliderOrientation", "Horizontal"),
                            ApiProperty("inverted", "Boolean", "false"),
                            ApiProperty("minStepsBetweenThumbs", "Int", "0"),
                            ApiProperty("onValueCommit", "callback?", "null"),
                            ApiProperty("disabled", "Boolean", "false"),
                            ApiProperty("label", "String", "\"\""),
                            ApiProperty("getValueLabel", "((Float) -> String)?", "null"),
                        ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    code =
                        """
                        var value by remember { mutableStateOf(75f) }
                        Slider(
                            value = value,
                            onValueChange = { value = it },
                            valueRange = 0f..100f,
                            step = 1f,
                            modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
                        )
                        """.trimIndent(),
                    preview = { SliderDemoPreview() },
                ),
                ComponentExample(
                    title = "Range",
                    descriptionMarkdown = "Use a `ClosedFloatingPointRange` value for a range slider.",
                    code =
                        """
                        var range by remember { mutableStateOf(25f..50f) }
                        Slider(
                            value = range,
                            onValueChange = { range = it },
                            valueRange = 0f..100f,
                            step = 5f,
                        )
                        """.trimIndent(),
                    preview = { SliderRangePreview() },
                ),
                ComponentExample(
                    title = "Multiple Thumbs",
                    descriptionMarkdown = "Use a list of values for multiple thumbs.",
                    code =
                        """
                        var values by remember { mutableStateOf(listOf(10f, 20f, 70f)) }
                        Slider(
                            values = values,
                            onValuesChange = { values = it },
                            valueRange = 0f..100f,
                            step = 10f,
                        )
                        """.trimIndent(),
                    preview = { SliderMultiplePreview() },
                ),
                ComponentExample(
                    title = "Vertical",
                    descriptionMarkdown = "Use `orientation = SliderOrientation.Vertical` for a vertical slider.",
                    code =
                        """
                        Slider(
                            value = value,
                            onValueChange = { value = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Vertical,
                            modifier = Modifier.height(160.dp),
                        )
                        """.trimIndent(),
                    preview = { SliderVerticalPreview() },
                ),
                ComponentExample(
                    title = "Controlled",
                    descriptionMarkdown =
                        "Keep the value in host state and render it beside the slider; a range value is a " +
                            "`ClosedFloatingPointRange<Float>`.",
                    code =
                        """
                        var range by remember { mutableStateOf(0.3f..0.7f) }
                        Label("Temperature")
                        Slider(
                            value = range,
                            onValueChange = { range = it },
                            valueRange = 0f..1f,
                            step = 0.1f,
                        )
                        """.trimIndent(),
                    preview = { SliderControlledPreview() },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown = "Use the `disabled` prop to disable the slider.",
                    code =
                        """
                        Slider(
                            value = value,
                            onValueChange = { value = it },
                            valueRange = 0f..100f,
                            disabled = true,
                        )
                        """.trimIndent(),
                    preview = { SliderDisabledPreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    previewPadding = 0.dp,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code =
                        """
                        var language by remember { mutableStateOf(1) }
                        RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                            Slider(
                                value = value,
                                onValueChange = { value = it },
                                valueRange = 0f..100f,
                            )
                        }
                        """.trimIndent(),
                    preview = { SliderRtlPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Step",
                    descriptionMarkdown =
                        "`step = 10f` snaps keyboard and pointer changes to the step; the official examples use " +
                            "an equivalent `step` prop.",
                    code =
                        """
                        Slider(value = value, onValueChange = { value = it }, valueRange = 0f..100f, step = 10f)
                        """.trimIndent(),
                    preview = { SliderStepExtensionPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Inverted",
                    descriptionMarkdown = "`inverted = true` flips the direction of the filled range.",
                    code =
                        """
                        Slider(value = value, onValueChange = { value = it }, valueRange = 0f..100f, inverted = true)
                        """.trimIndent(),
                    preview = { SliderInvertedExtensionPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Commit",
                    descriptionMarkdown =
                        "`onValueCommit` fires when a gesture or key interaction ends, so the host can persist a " +
                            "single settled value.",
                    code =
                        """
                        Slider(
                            value = value,
                            onValueChange = { value = it },
                            valueRange = 0f..100f,
                            onValueCommit = { committed = it },
                        )
                        """.trimIndent(),
                    preview = { SliderCommitExtensionPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Min steps between thumbs",
                    descriptionMarkdown =
                        "`minStepsBetweenThumbs` keeps this many steps between adjacent thumbs.",
                    code =
                        """
                        Slider(value = range, onValueChange = { range = it }, minStepsBetweenThumbs = 10)
                        """.trimIndent(),
                    preview = { SliderMinStepsExtensionPreview() },
                ),
            ),
    )
