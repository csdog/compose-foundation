@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.slider.Slider
import com.toutiaoyan.compose.ui.components.slider.SliderOrientation

import androidx.compose.foundation.layout.widthIn

val sliderDoc =
    ComponentDoc(
        id = "slider",
        title = "Slider",
        description = "An input where the user selects a value from within a given range.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.slider.Slider

            var volume by remember { mutableStateOf(50f) }
            Slider(value = volume, onValueChange = { volume = it }, valueRange = 0f..100f)
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("value / values", "Float / range / List<Float>", "required"),
                ApiProperty("valueRange", "ClosedFloatingPointRange<Float>", "0f..1f"),
                ApiProperty("step", "Float", "0f (continuous)"),
                ApiProperty("orientation", "SliderOrientation", "Horizontal"),
                ApiProperty("inverted", "Boolean", "false"),
                ApiProperty("minStepsBetweenThumbs", "Int", "0"),
                ApiProperty("onValueCommit", "callback?", "null"),
                ApiProperty("disabled", "Boolean", "false"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var volume by remember { mutableStateOf(75f) }
                        Slider(value = volume, onValueChange = { volume = it }, valueRange = 0f..100f, step = 1f, modifier = Modifier.widthIn(max = 320.dp), label = "Volume")
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(75f) }
                        Slider(
                            value = volume,
                            onValueChange = {
                                volume = it
                            },
                            valueRange = 0f..100f,
                            step = 1f,
                            modifier =
                                Modifier.widthIn(
                                    max = 320.dp,
                                ),
                            label = "Volume",
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """Slider(value = 30f, onValueChange = {}, valueRange = 0f..100f, disabled = true)""",
                    preview = { Slider(value = 30f, onValueChange = {}, valueRange = 0f..100f, disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """Slider(value = 50f, onValueChange = {}, valueRange = 0f..100f, disabled = false)""",
                    preview = { Slider(value = 50f, onValueChange = {}, valueRange = 0f..100f, disabled = false) },
                ),
                ComponentExample(
                    title = "Multiple thumbs",
                    code =
                        """
                        var values by remember { mutableStateOf(listOf(25f, 50f, 75f)) }
                        Slider(values = values, onValuesChange = { values = it }, valueRange = 0f..100f)
                        """.trimIndent(),
                    preview = {
                        var values by remember { mutableStateOf(listOf(25f, 50f, 75f)) }
                        Slider(values = values, onValuesChange = { values = it }, valueRange = 0f..100f)
                    },
                ),
                ComponentExample(
                    title = "Step",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            step = 10f,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            step = 10f,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default step",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            step = 0f,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            step = 0f,
                        )
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Vertical,
                            modifier = Modifier.height(160.dp),
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Vertical,
                            modifier = Modifier.height(160.dp),
                        )
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Horizontal,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Horizontal,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Horizontal,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            orientation = SliderOrientation.Horizontal,
                        )
                    },
                ),
                ComponentExample(
                    title = "Inverted",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            inverted = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            inverted = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default inverted",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            inverted = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            inverted = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Min steps between thumbs",
                    code =
                        """
                        var range by remember { mutableStateOf(25f..75f) }
                        Slider(
                            value = range,
                            onValueChange = { range = it },
                            valueRange = 0f..100f,
                            step = 1f,
                            minStepsBetweenThumbs = 10,
                        )
                        """.trimIndent(),
                    preview = {
                        var range by remember { mutableStateOf(25f..75f) }
                        Slider(
                            value = range,
                            onValueChange = { range = it },
                            valueRange = 0f..100f,
                            step = 1f,
                            minStepsBetweenThumbs = 10,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default min steps",
                    code =
                        """
                        var range by remember { mutableStateOf(25f..75f) }
                        Slider(
                            value = range,
                            onValueChange = { range = it },
                            valueRange = 0f..100f,
                            minStepsBetweenThumbs = 0,
                        )
                        """.trimIndent(),
                    preview = {
                        var range by remember { mutableStateOf(25f..75f) }
                        Slider(
                            value = range,
                            onValueChange = { range = it },
                            valueRange = 0f..100f,
                            minStepsBetweenThumbs = 0,
                        )
                    },
                ),
                ComponentExample(
                    title = "Min and max",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(value = volume, onValueChange = { volume = it }, min = 0f, max = 100f)
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(value = volume, onValueChange = { volume = it }, min = 0f, max = 100f)
                    },
                ),
                ComponentExample(
                    title = "Commit",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            onValueCommit = { /* persist */ },
                            valueRange = 0f..100f,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            onValueCommit = {},
                            valueRange = 0f..100f,
                        )
                    },
                ),
                ComponentExample(
                    title = "Value label",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            getValueLabel = { "${'$'}{it.toInt()} percent" },
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            getValueLabel = { "${it.toInt()} percent" },
                        )
                    },
                ),
                ComponentExample(
                    title = "Default label",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            label = "",
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            label = "",
                        )
                    },
                ),
                ComponentExample(
                    title = "Default min",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            min = null,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            min = null,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default max",
                    code =
                        """
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            max = null,
                        )
                        """.trimIndent(),
                    preview = {
                        var volume by remember { mutableStateOf(50f) }
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            max = null,
                        )
                    },
                ),
            ),
    )
