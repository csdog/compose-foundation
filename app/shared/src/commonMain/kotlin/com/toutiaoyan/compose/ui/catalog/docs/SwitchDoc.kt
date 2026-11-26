@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldContent
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.field.FieldTitle
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.switch.SwitchSize
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

val switchDoc =
    ComponentDoc(
        id = "switch",
        title = "Switch",
        description = "A control that allows the user to toggle between checked and not checked.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.switch.Switch",
        usageCode =
            """
            var checked by remember { mutableStateOf(false) }
            Switch(checked = checked, onCheckedChange = { checked = it })
            """.trimIndent(),
        examplesHeading = null,
        apiProperties =
            listOf(
                ApiProperty("checked", "Boolean?", "null"),
                ApiProperty("defaultChecked", "Boolean", "false"),
                ApiProperty("onCheckedChange", "((Boolean) -> Unit)?", "null"),
                ApiProperty("size", "SwitchSize", "Default"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("required", "Boolean", "false"),
                ApiProperty("enabled / disabled", "Boolean", "true / false"),
                ApiProperty("animation", "ButtonAnimation", "Scale"),
                ApiProperty("style", "Style", "Style"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                modifier = Modifier.focusRequester(target),
                                label = "Airplane Mode",
                            )
                            FieldLabel(
                                "Airplane Mode",
                                modifier =
                                    Modifier.focusProperties { canFocus = false }.clickable {
                                        checked = !checked
                                        target.requestFocus()
                                    },
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                modifier = Modifier.focusRequester(target),
                                label = "Airplane Mode",
                            )
                            FieldLabel(
                                "Airplane Mode",
                                modifier =
                                    Modifier.focusProperties { canFocus = false }.clickable {
                                        checked = !checked
                                        target.requestFocus()
                                    },
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    descriptionMarkdown =
                        "Use `FieldContent` with `FieldLabel` and `FieldDescription`, and place the " +
                            "switch at the logical end.",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        Field(Modifier.widthIn(max = 384.dp), orientation = FieldOrientation.Horizontal) {
                            FieldContent {
                                FieldLabel(
                                    "Share across devices",
                                    modifier =
                                        Modifier.focusProperties { canFocus = false }.clickable {
                                            checked = !checked
                                            target.requestFocus()
                                        },
                                )
                                FieldDescription("Focus is shared across devices, and turns off when you leave the app.")
                            }
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                modifier = Modifier.focusRequester(target),
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        Field(Modifier.widthIn(max = 384.dp), orientation = FieldOrientation.Horizontal) {
                            FieldContent {
                                FieldLabel(
                                    "Share across devices",
                                    modifier =
                                        Modifier.focusProperties { canFocus = false }.clickable {
                                            checked = !checked
                                            target.requestFocus()
                                        },
                                )
                                FieldDescription(
                                    "Focus is shared across devices, and turns off when you leave the app.",
                                )
                            }
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                modifier = Modifier.focusRequester(target),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Choice Card",
                    descriptionMarkdown =
                        "Card-style selection where the whole row is the label for the switch.",
                    code =
                        """
                        var share by remember { mutableStateOf(false) }
                        var notifications by remember { mutableStateOf(true) }
                        FieldGroup(Modifier.widthIn(max = 384.dp), spacing = 12.dp) {
                            SwitchChoiceCard(
                                title = "Share across devices",
                                description = "Focus is shared across devices, and turns off when you leave the app.",
                                checked = share,
                                onCheckedChange = { share = it },
                            )
                            SwitchChoiceCard(
                                title = "Enable notifications",
                                description = "Receive notifications when focus mode is enabled or disabled.",
                                checked = notifications,
                                onCheckedChange = { notifications = it },
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var share by remember { mutableStateOf(false) }
                        var notifications by remember { mutableStateOf(true) }
                        FieldGroup(Modifier.widthIn(max = 384.dp), spacing = 12.dp) {
                            SwitchChoiceCard(
                                title = "Share across devices",
                                description = "Focus is shared across devices, and turns off when you leave the app.",
                                checked = share,
                                onCheckedChange = { share = it },
                            )
                            SwitchChoiceCard(
                                title = "Enable notifications",
                                description = "Receive notifications when focus mode is enabled or disabled.",
                                checked = notifications,
                                onCheckedChange = { notifications = it },
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown =
                        "`disabled = true` on the switch; pass `disabled = true` to the label or `Field` too.",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = false, onCheckedChange = {}, disabled = true)
                            FieldLabel("Disabled", disabled = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Switch(checked = false, onCheckedChange = {}, disabled = true)
                            FieldLabel("Disabled", disabled = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    descriptionMarkdown =
                        "`invalid = true` adds the destructive ring; the wrapping `Field` can mark the copy too.",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        Field(Modifier.widthIn(max = 384.dp), orientation = FieldOrientation.Horizontal, invalid = true) {
                            FieldContent {
                                FieldLabel(
                                    "Accept terms and conditions",
                                    modifier =
                                        Modifier.focusProperties { canFocus = false }.clickable {
                                            checked = !checked
                                            target.requestFocus()
                                        },
                                )
                                FieldDescription("You must accept the terms and conditions to continue.")
                            }
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                modifier = Modifier.focusRequester(target),
                                invalid = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        Field(
                            Modifier.widthIn(max = 384.dp),
                            orientation = FieldOrientation.Horizontal,
                            invalid = true,
                        ) {
                            FieldContent {
                                FieldLabel(
                                    "Accept terms and conditions",
                                    modifier =
                                        Modifier.focusProperties { canFocus = false }.clickable {
                                            checked = !checked
                                            target.requestFocus()
                                        },
                                )
                                FieldDescription("You must accept the terms and conditions to continue.")
                            }
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                modifier = Modifier.focusRequester(target),
                                invalid = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Size",
                    descriptionMarkdown = "Use `size = SwitchSize.Sm` for compact layouts.",
                    code =
                        """
                        FieldGroup(Modifier.widthIn(max = 160.dp), spacing = 12.dp) {
                            Field(orientation = FieldOrientation.Horizontal) {
                                Switch("Small", size = SwitchSize.Sm)
                            }
                            Field(orientation = FieldOrientation.Horizontal) {
                                Switch("Default")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldGroup(Modifier.widthIn(max = 160.dp), spacing = 12.dp) {
                            Field(orientation = FieldOrientation.Horizontal) {
                                Switch("Small", size = SwitchSize.Sm)
                            }
                            Field(orientation = FieldOrientation.Horizontal) {
                                Switch("Default")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    code =
                        """
                        var language by remember { mutableStateOf(0) }
                        var checked by remember { mutableStateOf(false) }
                        val target = remember { FocusRequester() }
                        RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
                            CompositionLocalProvider(
                                LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
                            ) {
                                Field(Modifier.widthIn(max = 384.dp), orientation = FieldOrientation.Horizontal) {
                                    FieldContent {
                                        FieldLabel(
                                            label,
                                            modifier =
                                                Modifier.focusProperties { canFocus = false }.clickable {
                                                    checked = !checked
                                                    target.requestFocus()
                                                },
                                        )
                                        FieldDescription(description)
                                    }
                                    Switch(
                                        checked = checked,
                                        onCheckedChange = { checked = it },
                                        modifier = Modifier.focusRequester(target),
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { SwitchRtlPreview() },
                ),
            ),
    )

@Composable
private fun SwitchChoiceCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = currentTheme.colors
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier =
            Modifier.fillMaxWidth().clip(shape)
                .background(if (checked) colors.muted else colors.background)
                .border(1.dp, colors.border, shape)
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            FieldTitle(title)
            FieldDescription(description)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private val switchRtlLabels = listOf("Arabic (العربية)", "English", "Hebrew (עברית)")

private class SwitchRtlCopy(
    val label: String,
    val description: String,
)

private val switchRtlCopy =
    listOf(
        SwitchRtlCopy(
            label = "المشاركة عبر الأجهزة",
            description = "يتم مشاركة التركيز عبر الأجهزة، ويتم إيقاف تشغيله عند مغادرة التطبيق.",
        ),
        SwitchRtlCopy(
            label = "Share across devices",
            description = "Focus is shared across devices, and turns off when you leave the app.",
        ),
        SwitchRtlCopy(
            label = "שיתוף בין מכשירים",
            description = "המיקוד משותף בין מכשירים, וכבה כשאתה עוזב את האפליקציה.",
        ),
    )

@Composable
private fun SwitchRtlPreview() {
    var language by remember { mutableStateOf(0) }
    RtlPreview(language, switchRtlLabels, { language = it }) {
        val inherited = currentTheme
        val fontFamily =
            when (language) {
                0 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
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
                var checked by remember { mutableStateOf(false) }
                val target = remember { FocusRequester() }
                val copy = switchRtlCopy[language]
                Field(
                    Modifier.widthIn(max = 384.dp),
                    orientation = FieldOrientation.Horizontal,
                ) {
                    FieldContent {
                        FieldLabel(
                            copy.label,
                            modifier =
                                Modifier.focusProperties { canFocus = false }.clickable {
                                    checked = !checked
                                    target.requestFocus()
                                },
                        )
                        FieldDescription(copy.description)
                    }
                    Switch(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        modifier = Modifier.focusRequester(target),
                    )
                }
            }
        }
    }
}
