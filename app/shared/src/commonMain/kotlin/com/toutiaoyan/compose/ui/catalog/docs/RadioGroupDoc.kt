@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldContent
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldLegendVariant
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.field.FieldTitle
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupOrientation
import com.toutiaoyan.compose.ui.components.radiobutton.radioGroupState
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupScope
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

private val rtlLabels = listOf("Arabic (العربية)", "English", "Hebrew (עברית)")

private class RadioRtlCopy(
    val defaultValue: String,
    val defaultDescription: String,
    val comfortable: String,
    val comfortableDescription: String,
    val compact: String,
    val compactDescription: String,
)

private val radioRtlCopy =
    listOf(
        RadioRtlCopy(
            defaultValue = "افتراضي",
            defaultDescription = "تباعد قياسي لمعظم حالات الاستخدام.",
            comfortable = "مريح",
            comfortableDescription = "مساحة أكبر بين العناصر.",
            compact = "مضغوط",
            compactDescription = "تباعد أدنى للتخطيطات الكثيفة.",
        ),
        RadioRtlCopy(
            defaultValue = "Default",
            defaultDescription = "Standard spacing for most use cases.",
            comfortable = "Comfortable",
            comfortableDescription = "More space between elements.",
            compact = "Compact",
            compactDescription = "Minimal spacing for dense layouts.",
        ),
        RadioRtlCopy(
            defaultValue = "ברירת מחדל",
            defaultDescription = "ריווח סטנדרטי לרוב מקרי השימוש.",
            comfortable = "נוח",
            comfortableDescription = "יותר מקום בין האלמנטים.",
            compact = "קומפקטי",
            compactDescription = "ריווח מינימלי לפריסות צפופות.",
        ),
    )

/** Label click selects the item, matching the official `htmlFor` association. */
@Composable
private fun SelectableLabel(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Label(
        text = text,
        modifier =
            modifier
                .focusProperties { canFocus = false }
                .clickable(onClick = onClick),
        style = Style {},
    )
}

@Composable
private fun RadioGroupScope.RadioRow(
    value: String,
    text: String,
) {
    val select = radioGroupState().onValueChange
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioGroupItem(value, label = text)
        SelectableLabel(text, onClick = { select(value) })
    }
}

@Composable
private fun RadioGroupDemoPreview() {
    var selected by remember { mutableStateOf("comfortable") }
    RadioGroup(value = selected, onValueChange = { selected = it }, modifier = Modifier.testTag("radio-demo")) {
        RadioRow("default", "Default")
        RadioRow("comfortable", "Comfortable")
        RadioRow("compact", "Compact")
    }
}

private class DescribedOption(val value: String, val label: String, val description: String)

private val describedOptions =
    listOf(
        DescribedOption("default", "Default", "Standard spacing for most use cases."),
        DescribedOption("comfortable", "Comfortable", "More space between elements."),
        DescribedOption("compact", "Compact", "Minimal spacing for dense layouts."),
    )

@Composable
private fun RadioGroupScope.DescribedRow(option: DescribedOption) {
    val select = radioGroupState().onValueChange
    Field(orientation = FieldOrientation.Horizontal) {
        RadioGroupItem(option.value, label = option.label)
        FieldContent {
            FieldLabel(
                option.label,
                modifier = Modifier.focusProperties { canFocus = false }.clickable { select(option.value) },
            )
            FieldDescription(option.description)
        }
    }
}

@Composable
private fun RadioGroupDescriptionPreview() {
    var selected by remember { mutableStateOf("comfortable") }
    RadioGroup(
        value = selected,
        onValueChange = { selected = it },
        modifier = Modifier.width(IntrinsicSize.Max).testTag("radio-description"),
    ) {
        describedOptions.forEach { option ->
            DescribedRow(option)
        }
    }
}

private class PlanOption(val value: String, val title: String, val description: String)

private val planOptions =
    listOf(
        PlanOption("plus", "Plus", "For individuals and small teams."),
        PlanOption("pro", "Pro", "For growing businesses."),
        PlanOption("enterprise", "Enterprise", "For large teams and enterprises."),
    )

@Composable
private fun RadioGroupChoiceCardPreview() {
    var selected by remember { mutableStateOf("plus") }
    RadioGroup(
        value = selected,
        onValueChange = { selected = it },
        modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
    ) {
        val select = radioGroupState().onValueChange
        planOptions.forEach { plan ->
            FieldLabel(
                onClick = { select(plan.value) },
                selected = selected == plan.value,
                modifier = Modifier.testTag("radio-card-${plan.value}").focusProperties { canFocus = false },
            ) {
                Field(orientation = FieldOrientation.Horizontal) {
                    FieldContent {
                        FieldTitle(plan.title)
                        FieldDescription(plan.description)
                    }
                    RadioGroupItem(plan.value, label = plan.title)
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPlanRows() {
    val plans =
        listOf(
            "monthly" to "Monthly ($9.99/month)",
            "yearly" to "Yearly ($99.99/year)",
            "lifetime" to "Lifetime ($299.99)",
        )
    var selected by remember { mutableStateOf("monthly") }
    FieldSet(
        legend = "Subscription Plan",
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
        legendVariant = FieldLegendVariant.Label,
    ) {
        FieldDescription("Yearly and lifetime plans offer significant savings.")
        RadioGroup(value = selected, onValueChange = { selected = it }, modifier = Modifier.testTag("radio-fieldset")) {
            val select = radioGroupState().onValueChange
            plans.forEach { (value, label) ->
                Field(orientation = FieldOrientation.Horizontal) {
                    RadioGroupItem(value, label = label)
                    FieldLabel(
                        label,
                        textStyle = TextStyle(fontWeight = FontWeight.Normal),
                        modifier = Modifier.focusProperties { canFocus = false }.clickable { select(value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RadioGroupFieldsetPreview() {
    SubscriptionPlanRows()
}

@Composable
private fun RadioGroupDisabledPreview() {
    var selected by remember { mutableStateOf("option2") }
    RadioGroup(
        value = selected,
        onValueChange = { selected = it },
        modifier = Modifier.width(IntrinsicSize.Max).testTag("radio-disabled"),
    ) {
        val select = radioGroupState().onValueChange
        Field(orientation = FieldOrientation.Horizontal, disabled = true) {
            RadioGroupItem("option1", disabled = true, label = "Disabled")
            FieldLabel("Disabled", textStyle = TextStyle(fontWeight = FontWeight.Normal))
        }
        Field(orientation = FieldOrientation.Horizontal) {
            RadioGroupItem("option2", label = "Option 2")
            FieldLabel(
                "Option 2",
                textStyle = TextStyle(fontWeight = FontWeight.Normal),
                modifier = Modifier.focusProperties { canFocus = false }.clickable { select("option2") },
            )
        }
        Field(orientation = FieldOrientation.Horizontal) {
            RadioGroupItem("option3", label = "Option 3")
            FieldLabel(
                "Option 3",
                textStyle = TextStyle(fontWeight = FontWeight.Normal),
                modifier = Modifier.focusProperties { canFocus = false }.clickable { select("option3") },
            )
        }
    }
}

@Composable
private fun RadioGroupInvalidPreview() {
    var selected by remember { mutableStateOf("email") }
    FieldSet(
        legend = "Notification Preferences",
        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
        legendVariant = FieldLegendVariant.Label,
    ) {
        FieldDescription("Choose how you want to receive notifications.")
        RadioGroup(value = selected, onValueChange = { selected = it }, modifier = Modifier.testTag("radio-invalid")) {
            val select = radioGroupState().onValueChange
            listOf(
                "email" to "Email only",
                "sms" to "SMS only",
                "both" to "Both Email & SMS",
            ).forEach { (value, label) ->
                Field(orientation = FieldOrientation.Horizontal, invalid = true) {
                    RadioGroupItem(value, invalid = true, label = label)
                    FieldLabel(
                        label,
                        textStyle = TextStyle(fontWeight = FontWeight.Normal),
                        modifier = Modifier.focusProperties { canFocus = false }.clickable { select(value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RadioGroupRtlPreview() {
    var language by remember { mutableStateOf(0) }
    RtlPreview(language, rtlLabels, { language = it }) {
        val inherited = currentTheme
        val fontFamily =
            when (language) {
                2 -> catalogHebrewFontFamily()
                1 -> inherited.fontFamily
                else -> catalogArabicFontFamily()
            }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Theme(
                preset = inherited.activePreset,
                baseColor = inherited.activeBaseColor,
                accent = inherited.activeAccent,
                isDark = inherited.isDark,
                baseRadius = inherited.baseRadius,
                ring = inherited.ring,
                unitSpacing = LocalUnitSpacing.current,
                minTouchTarget = inherited.minTouchTarget,
                fontFamily = fontFamily,
            ) {
                RadioGroupRtlContent(language)
            }
        }
    }
}

@Composable
private fun RadioGroupRtlContent(language: Int = 0) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
    ) {
        val copy = radioRtlCopy[language]
        val options =
            listOf(
                DescribedOption("default", copy.defaultValue, copy.defaultDescription),
                DescribedOption("comfortable", copy.comfortable, copy.comfortableDescription),
                DescribedOption("compact", copy.compact, copy.compactDescription),
            )
        var selected by remember(language) { mutableStateOf("comfortable") }
        Box(Modifier.testTag("radio-rtl")) {
            RadioGroup(
                value = selected,
                onValueChange = { selected = it },
                modifier = Modifier.width(IntrinsicSize.Max).testTag("radio-rtl-group"),
            ) {
                options.forEach { option ->
                    DescribedRow(option)
                }
            }
        }
    }
}

@Composable
private fun RadioGroupHorizontalPreview() {
    var selected by remember { mutableStateOf("a") }
    RadioGroup(
        value = selected,
        onValueChange = { selected = it },
        orientation = RadioGroupOrientation.Horizontal,
        modifier = Modifier.testTag("radio-horizontal"),
    ) {
        val select = radioGroupState().onValueChange
        listOf("a" to "Option A", "b" to "Option B", "c" to "Option C").forEach { (value, label) ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioGroupItem(value, label = label)
                SelectableLabel(label, onClick = { select(value) })
            }
        }
    }
}

@Composable
private fun RadioGroupRequiredPreview() {
    var selected by remember { mutableStateOf("a") }
    RadioGroup(value = selected, onValueChange = {
        selected = it
    }, required = true, modifier = Modifier.testTag("radio-required")) {
        RadioRow("a", "Option A")
        RadioRow("b", "Option B")
    }
}

private val radioGroupKotlinApi =
    listOf(
        ComponentApiSection(
            "RadioGroup",
            "Groups the items with single-selection semantics. Pass `value` + `onValueChange` for a " +
                "controlled group, or `defaultValue` for an uncontrolled one. See the " +
                "[Base UI Radio Group](https://base-ui.com/react/components/radio-group#api-reference) " +
                "documentation for the original props.",
            properties =
                listOf(
                    ApiProperty("value", "String?", "null"),
                    ApiProperty("onValueChange", "((String) -> Unit)?", "null"),
                    ApiProperty("defaultValue", "String", "\"\""),
                    ApiProperty("orientation", "RadioGroupOrientation", "Vertical"),
                    ApiProperty("disabled", "Boolean", "false"),
                    ApiProperty("required", "Boolean", "false"),
                    ApiProperty("contentDescription", "String?", "null"),
                    ApiProperty("modifier", "Modifier", "Modifier"),
                    ApiProperty("style", "Style", "Style"),
                ),
        ),
        ComponentApiSection(
            "RadioGroupItem",
            "One circle bound to the parent group's value. Arrow keys move and select, wrapping at " +
                "the ends and skipping disabled items; Home/End jump to the ends and the group is a " +
                "single Tab stop.",
            properties =
                listOf(
                    ApiProperty("value", "String", "required"),
                    ApiProperty("enabled", "Boolean", "true"),
                    ApiProperty("disabled", "Boolean", "false"),
                    ApiProperty("invalid", "Boolean", "false"),
                    ApiProperty("animation", "ButtonAnimation", "None (Nova), Scale (other presets)"),
                    ApiProperty("label", "String", "\"\""),
                    ApiProperty("modifier", "Modifier", "Modifier"),
                    ApiProperty("style", "Style", "Style"),
                ),
        ),
        ComponentApiSection(
            "RadioGroupItem (with text)",
            "Convenience row that draws the circle, a label and an optional description.",
            properties =
                listOf(
                    ApiProperty("text", "String", "required"),
                    ApiProperty("description", "String?", "null"),
                ),
        ),
    )

val radioGroupDoc =
    ComponentDoc(
        id = "radio-group",
        title = "Radio Group",
        description =
            "A set of checkable buttons—known as radio buttons—where no more than one of the buttons " +
                "can be checked at a time.",
        usageImportsCode =
            "import androidx.compose.foundation.clickable\n" +
                "import androidx.compose.foundation.layout.*\n" +
                "import androidx.compose.runtime.*\n" +
                "import androidx.compose.ui.Alignment\n" +
                "import androidx.compose.ui.focus.focusProperties\n" +
                "import androidx.compose.ui.Modifier\n" +
                "import androidx.compose.ui.unit.dp\n" +
                "import com.toutiaoyan.compose.ui.components.label.Label\n" +
                "import com.toutiaoyan.compose.ui.components.radiobutton.*",
        usageCode =
            """
            val options = listOf("option-one" to "Option One", "option-two" to "Option Two")
            var choice by remember { mutableStateOf("option-one") }
            RadioGroup(value = choice, onValueChange = { choice = it }) {
                val select = radioGroupState().onValueChange
                options.forEach { (value, label) ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioGroupItem(value, label = label)
                        Label(label, Modifier.focusProperties { canFocus = false }.clickable { select(value) })
                    }
                }
            }
            """.trimIndent(),
        beforeCompositionMarkdown = "Use the following composition to build a `RadioGroup`:",
        compositionCode =
            """
            RadioGroup
            ├── RadioGroupItem
            └── RadioGroupItem
            """.trimIndent(),
        examplesHeading = null,
        beforeExampleSections =
            mapOf(
                "Kotlin extension: Horizontal orientation" to
                    listOf(
                        ComponentGuideSection(
                            "API Reference",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "See the [Base UI Radio Group]" +
                                        "(https://base-ui.com/react/components/radio-group#api-reference) " +
                                        "documentation.",
                                ),
                            ),
                        ),
                    ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin extension: API",
                    radioGroupKotlinApi.flatMap { section ->
                        listOf(
                            ComponentGuideBlock.Paragraph("**" + section.title + "** " + section.descriptionMarkdown),
                            ComponentGuideBlock.Table(
                                headers = listOf("Property", "Type", "Default"),
                                rows = section.properties.map { listOf(it.name, it.type, it.defaultValue) },
                            ),
                        )
                    },
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    code = radioGroupDefaultRecipeCode,
                    preview = { RadioGroupDemoPreview() },
                ),
                ComponentExample(
                    title = "Description",
                    descriptionMarkdown = "Radio group items with a description using the `Field` component.",
                    code = radioGroupDescriptionRecipeCode,
                    preview = { RadioGroupDescriptionPreview() },
                ),
                ComponentExample(
                    title = "Choice Card",
                    descriptionMarkdown =
                        "Use `FieldLabel` to wrap the entire `Field` for a clickable card-style selection.",
                    code = radioGroupChoiceCardRecipeCode,
                    preview = { RadioGroupChoiceCardPreview() },
                ),
                ComponentExample(
                    title = "Fieldset",
                    descriptionMarkdown =
                        "Use `FieldSet` and `FieldLegend` to group radio items with a label and description.",
                    code = radioGroupFieldsetRecipeCode,
                    preview = { RadioGroupFieldsetPreview() },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown = "Use the `disabled` prop on `RadioGroup` to disable all items.",
                    code = radioGroupDisabledRecipeCode,
                    preview = { RadioGroupDisabledPreview() },
                ),
                ComponentExample(
                    title = "Invalid",
                    descriptionMarkdown =
                        "Use `invalid` on `RadioGroupItem` and `Field` to show validation errors.",
                    code = radioGroupInvalidRecipeCode,
                    preview = { RadioGroupInvalidPreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    previewPadding = 0.dp,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code = radioGroupRTLRecipeCode,
                    preview = { RadioGroupRtlPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Horizontal orientation",
                    descriptionMarkdown =
                        "`orientation = RadioGroupOrientation.Horizontal` lays the group out in a row " +
                            "(the official page only shows the vertical grid).",
                    code = radioGroupHorizontalRecipeCode,
                    preview = { RadioGroupHorizontalPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Required",
                    descriptionMarkdown =
                        "`required = true` exposes the required state to the field set while keeping the " +
                            "same single-selection behavior.",
                    code = radioGroupRequiredRecipeCode,
                    preview = { RadioGroupRequiredPreview() },
                ),
            ),
    )
