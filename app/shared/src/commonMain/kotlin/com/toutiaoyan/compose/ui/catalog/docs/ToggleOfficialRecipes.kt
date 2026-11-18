@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bold
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Italic
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.toggle.Toggle
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.toggle.toggleState
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

// region usage
@Composable
internal fun ToggleUsagePreview() {
    Toggle("Toggle")
}
// endregion usage

// region default
@Composable
internal fun ToggleDemoPreview() {
    BookmarkToggle(text = "Bookmark")
}
// endregion default

// region outline
@Composable
internal fun ToggleOutlinePreview() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Toggle(label = "Toggle italic", variant = ToggleVariant.Outline) {
            Icon(Lucide.Italic)
            Text { "Italic" }
        }
        Toggle(label = "Toggle bold", variant = ToggleVariant.Outline) {
            Icon(Lucide.Bold)
            Text { "Bold" }
        }
    }
}
// endregion outline

// region text
@Composable
internal fun ToggleWithTextPreview() {
    Toggle(label = "Toggle italic") {
        Icon(Lucide.Italic)
        Text { "Italic" }
    }
}
// endregion text

// region size
@Composable
internal fun ToggleSizePreview() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Toggle("Small", variant = ToggleVariant.Outline, size = ToggleSize.Sm, label = "Toggle small")
        Toggle("Default", variant = ToggleVariant.Outline, label = "Toggle default")
        Toggle("Large", variant = ToggleVariant.Outline, size = ToggleSize.Lg, label = "Toggle large")
    }
}
// endregion size

// region disabled
@Composable
internal fun ToggleDisabledPreview() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Toggle("Disabled", disabled = true, label = "Toggle disabled")
        Toggle("Disabled", disabled = true, variant = ToggleVariant.Outline, label = "Toggle disabled outline")
    }
}
// endregion disabled

// region rtl
@Composable
internal fun ToggleRtlPreview(language: String = "ar") {
    val text =
        when (language) {
            "ar" -> "إشارة مرجعية"
            "he" -> "סימנייה"
            else -> "Bookmark"
        }
    val direction = if (language == "ar" || language == "he") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        BookmarkToggle(text)
    }
}
// endregion rtl

// region bookmark
@Composable
private fun BookmarkToggle(text: String) {
    Toggle(label = "Toggle bookmark", variant = ToggleVariant.Outline, size = ToggleSize.Sm) {
        Icon(if (toggleState().pressed) FilledBookmark else Lucide.Bookmark)
        Text { text }
    }
}

private val FilledBookmark by lazy {
    ImageVector.Builder("FilledBookmark", 24.dp, 24.dp, 24f, 24f).apply {
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(19f, 21f)
            lineTo(12f, 17f)
            lineTo(5f, 21f)
            lineTo(5f, 5f)
            curveTo(5f, 3.9f, 5.9f, 3f, 7f, 3f)
            lineTo(17f, 3f)
            curveTo(18.1f, 3f, 19f, 3.9f, 19f, 5f)
            close()
        }
    }.build()
}
// endregion bookmark

@Composable
internal fun ToggleRtlCatalogPreview() {
    var language by remember { mutableIntStateOf(1) }
    val inherited = currentTheme
    RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
        val font =
            when (language) {
                1 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        Theme(
            preset = inherited.activePreset,
            baseColor = inherited.activeBaseColor,
            accent = inherited.activeAccent,
            isDark = inherited.isDark,
            baseRadius = inherited.baseRadius,
            ring = inherited.ring,
            unitSpacing = LocalUnitSpacing.current,
            minTouchTarget = inherited.minTouchTarget,
            fontFamily = font,
        ) {
            ToggleRtlPreview(language = listOf("en", "ar", "he")[language])
        }
    }
}

// region defaultVariant
@Composable
internal fun ToggleDefaultVariantPreview() {
    var bold by remember { mutableStateOf(false) }
    Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, variant = ToggleVariant.Default)
}
// endregion defaultVariant

// region defaultSize
@Composable
internal fun ToggleDefaultSizePreview() {
    var bold by remember { mutableStateOf(false) }
    Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, size = ToggleSize.Md)
}
// endregion defaultSize

// region defaultDisabled
@Composable
internal fun ToggleDefaultDisabledPreview() {
    var bold by remember { mutableStateOf(false) }
    Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, disabled = false)
}
// endregion defaultDisabled

// region uncontrolled
@Composable
internal fun ToggleUncontrolledPreview() {
    Toggle("Bold", defaultPressed = true)
}
// endregion uncontrolled

// region defaultPressed
@Composable
internal fun ToggleDefaultPressedPreview() {
    Toggle("Bold", defaultPressed = false)
}
// endregion defaultPressed

// region accessibleLabel
@Composable
internal fun ToggleAccessibleLabelPreview() {
    var bold by remember { mutableStateOf(false) }
    Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, label = "Bold")
}
// endregion accessibleLabel

// region defaultLabel
@Composable
internal fun ToggleDefaultLabelPreview() {
    Toggle("Bold", defaultPressed = false, label = "Bold")
}
// endregion defaultLabel

// region invalid
@Composable
internal fun ToggleInvalidPreview() {
    var confirmed by remember { mutableStateOf(false) }
    Toggle(
        pressed = confirmed,
        onPressedChange = { confirmed = it },
        label = "Confirm selection",
        variant = ToggleVariant.Outline,
        invalid = !confirmed,
    ) {
        Text { "Confirm selection" }
    }
}
// endregion invalid
