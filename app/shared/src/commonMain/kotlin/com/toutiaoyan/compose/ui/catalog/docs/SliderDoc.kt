package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
private fun SliderRtlCatalogPreview() {
    var language by remember { mutableStateOf(1) }
    RtlPreview(
        language,
        listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
        { language = it },
    ) {
        SliderRtlPreview(language = listOf("en", "ar", "he")[language])
    }
}

val sliderDoc =
    ComponentDoc(
        id = "slider",
        title = "Slider",
        description = "An input where the user selects a value from within a given range.",
        usageCode = sliderUsageRecipeCode,
        usageImportsCode = sliderUsageImportCode,
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
                            ApiProperty("onValueCommit / onValuesCommit", "callback?", "null"),
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
                    code = sliderDefaultRecipeCode,
                    preview = { SliderDemoPreview() },
                ),
                ComponentExample(
                    title = "Range",
                    descriptionMarkdown = "Use a `ClosedFloatingPointRange` value for a range slider.",
                    code = sliderRangeRecipeCode,
                    preview = { SliderRangePreview() },
                ),
                ComponentExample(
                    title = "Multiple Thumbs",
                    descriptionMarkdown = "Use a list of values for multiple thumbs.",
                    code = sliderMultipleRecipeCode,
                    preview = { SliderMultiplePreview() },
                ),
                ComponentExample(
                    title = "Vertical",
                    descriptionMarkdown = "Use `orientation = SliderOrientation.Vertical` for a vertical slider.",
                    code = sliderVerticalRecipeCode,
                    preview = { SliderVerticalPreview() },
                ),
                ComponentExample(
                    title = "Controlled",
                    descriptionMarkdown =
                        "Keep the value in host state and render it beside the slider; a range value is a " +
                            "`ClosedFloatingPointRange<Float>`.",
                    code = sliderControlledRecipeCode,
                    preview = { SliderControlledPreview() },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown = "Use the `disabled` prop to disable the slider.",
                    code = sliderDisabledRecipeCode,
                    preview = { SliderDisabledPreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    previewPadding = 0.dp,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code = sliderRtlRecipeCode,
                    preview = { SliderRtlCatalogPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Step",
                    descriptionMarkdown =
                        "`step = 10f` snaps keyboard and pointer changes to the step; the official examples use " +
                            "an equivalent `step` prop.",
                    code = sliderStepRecipeCode,
                    preview = { SliderStepExtensionPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Inverted",
                    descriptionMarkdown = "`inverted = true` flips the direction of the filled range.",
                    code = sliderInvertedRecipeCode,
                    preview = { SliderInvertedExtensionPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Commit",
                    descriptionMarkdown =
                        "`onValueCommit` fires when a gesture or key interaction ends, so the host can persist a " +
                            "single settled value.",
                    code = sliderCommitRecipeCode,
                    preview = { SliderCommitExtensionPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Min steps between thumbs",
                    descriptionMarkdown =
                        "`minStepsBetweenThumbs` keeps this many steps between adjacent thumbs.",
                    code = sliderMinStepsRecipeCode,
                    preview = { SliderMinStepsExtensionPreview() },
                ),
            ),
    )
