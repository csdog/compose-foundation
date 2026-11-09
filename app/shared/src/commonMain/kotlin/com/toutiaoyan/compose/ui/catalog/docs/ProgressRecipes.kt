@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.progress.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.slider.Slider
import kotlinx.coroutines.delay

// region shared
// Progress keeps the Kotlin fraction API; these source recipes explicitly use max100.
// endregion shared

// region default
@Composable
internal fun ProgressDefaultRecipe() {
    var progress by remember { mutableFloatStateOf(13f) }
    LaunchedEffect(Unit) {
        delay(500)
        progress = 66f
    }
    Progress(progress, max = 100f, modifier = Modifier.fillMaxWidth(0.6f).testTag("progress-recipe"))
}
// endregion default

// region label
@Composable
internal fun ProgressLabelRecipe() {
    Progress(56f, max = 100f, modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("progress-recipe")) {
        ProgressLabel("Upload progress")
        ProgressValue()
    }
}
// endregion label

// region controlled
@Composable
internal fun ProgressControlledRecipe() {
    var value by remember { mutableFloatStateOf(50f) }
    Column(
        Modifier.widthIn(
            max =
                384.dp,
        ).fillMaxWidth().testTag("progress-controlled"),
        verticalArrangement =
            Arrangement.spacedBy(16.dp),
    ) {
        Progress(value, max = 100f, modifier = Modifier.fillMaxWidth().testTag("progress-recipe"))
        Slider(
            value,
            onValueChange = { value = it },
            min = 0f,
            max = 100f,
            step = 1f,
            modifier = Modifier.fillMaxWidth().testTag("progress-slider"),
        )
    }
}
// endregion controlled

// region rtl
@Composable
internal fun ProgressRtlRecipe(language: Int = 1) {
    val labels = listOf("Upload progress", "تقدم الرفع", "התקדמות העלאה")
    val inherited = currentTheme
    val font =
        when (language) {
            1 -> catalogArabicFontFamily()
            2 -> catalogHebrewFontFamily()
            else -> inherited.fontFamily
        }
    CompositionLocalProvider(
        LocalLayoutDirection provides
            if (language == 0) {
                LayoutDirection.Ltr
            } else {
                LayoutDirection.Rtl
            },
    ) {
        Theme(
            preset = inherited.activePreset, baseColor = inherited.activeBaseColor,
            accent = inherited.activeAccent, isDark = inherited.isDark, baseRadius = inherited.baseRadius,
            ring = inherited.ring, unitSpacing = LocalUnitSpacing.current,
            minTouchTarget = inherited.minTouchTarget, fontFamily = font,
        ) {
            Progress(
                56f,
                max = 100f,
                modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("progress-recipe"),
            ) {
                ProgressLabel(labels[language])
                ProgressValue(if (language == 1) "٥٦%" else "56%")
            }
        }
    }
}
// endregion rtl

internal val progressOfficialExamples =
    listOf(
        ComponentExample("Default", code = progressDefaultRecipeCode, preview = { ProgressDefaultRecipe() }),
        ComponentExample(
            "Label",
            code = progressLabelRecipeCode,
            descriptionMarkdown = "Use `ProgressLabel` and `ProgressValue` to add a label and value display.",
            preview = { ProgressLabelRecipe() },
        ),
        ComponentExample(
            "Controlled",
            code = progressControlledRecipeCode,
            descriptionMarkdown = "A progress bar that can be controlled by a slider.",
            preview = { ProgressControlledRecipe() },
        ),
        ComponentExample(
            "RTL",
            code = progressRtlRecipeCode,
            previewPadding = 0.dp,
            descriptionMarkdown =
                "To enable RTL support in shadcn/ui, see the " +
                    "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
            preview = {
                var language by remember { mutableIntStateOf(1) }
                Column(Modifier.fillMaxWidth()) {
                    RtlPreviewToolbar(
                        language,
                        listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
                        { language = it },
                    )
                    Box(
                        Modifier.testTag("progress-rtl-body").fillMaxWidth().heightIn(min = 288.dp).padding(40.dp),
                        contentAlignment = Alignment.Center,
                    ) { ProgressRtlRecipe(language) }
                }
            },
        ),
    )
