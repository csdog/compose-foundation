@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.LocalCatalogViewportWidth
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

@Composable
internal fun ButtonRecipe(
    kind: Int,
    language: Int = 0,
) {
    val words =
        when (language) {
            1 -> listOf("زر", "حذف", "إرسال", "جاري التحميل")
            2 -> listOf("כפתור", "מחק", "שלח", "טוען")
            else -> listOf("Button", "Delete", "Submit", "Loading")
        }
    when (kind) {
        1 -> ButtonSizeRecipe()
        12 -> buttonGroupDoc.examples.first().preview()
        else ->
            ButtonRecipeRow(kind) {
                when (kind) {
                    0 -> {
                        Button("Button", variant = ButtonVariant.Outline)
                        Button(
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Icon,
                            label = "Submit",
                        ) { Icon(Lucide.ArrowUp) }
                    }
                    2 -> Button("Button")
                    3 -> Button("Outline", variant = ButtonVariant.Outline)
                    4 -> Button("Secondary", variant = ButtonVariant.Secondary)
                    5 -> Button("Ghost", variant = ButtonVariant.Ghost)
                    6 -> Button("Destructive", variant = ButtonVariant.Destructive)
                    7 -> Button("Link", variant = ButtonVariant.Link)
                    8 ->
                        Button(variant = ButtonVariant.Outline, size = ButtonSize.Icon, label = "Upload") {
                            Icon(Lucide.CircleFadingArrowUp)
                        }
                    9 -> {
                        Button(variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.Start) {
                            Icon(Lucide.GitBranch, modifier = Modifier.testTag("button-leading-icon"))
                            Text(maxLines = 1) { "New Branch" }
                        }
                        Button(variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.End) {
                            Text(maxLines = 1) { "Fork" }
                            Icon(Lucide.GitFork, modifier = Modifier.testTag("button-trailing-icon"))
                        }
                    }
                    10 -> {
                        Button("Get Started", style = Style { shape(CircleShape) })
                        Button(
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Icon,
                            label = "Submit",
                            style =
                                Style {
                                    shape(CircleShape)
                                },
                        ) { Icon(Lucide.ArrowUp) }
                    }
                    11 -> {
                        Button(
                            variant = ButtonVariant.Outline,
                            disabled = true,
                            iconPlacement = ButtonIconPlacement.Start,
                        ) {
                            Spinner(Modifier.testTag("button-generating-spinner"))
                            Text(maxLines = 1) { "Generating" }
                        }
                        // Official source marks this trailing spinner inline-start; preserve that edge declaration.
                        Button(
                            variant = ButtonVariant.Secondary,
                            disabled = true,
                            iconPlacement = ButtonIconPlacement.Start,
                        ) {
                            Text(maxLines = 1) { "Downloading" }
                            Spinner()
                        }
                    }
                    14 -> {
                        Button(words[0], variant = ButtonVariant.Outline)
                        Button(words[1], variant = ButtonVariant.Destructive)
                        Button(variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.End) {
                            Text(maxLines = 1) { words[2] }
                            Icon(
                                Lucide.ArrowRight,
                                modifier = Modifier.graphicsLayer { rotationZ = if (language == 0) 0f else 180f },
                            )
                        }
                        Button(
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Icon,
                            label = "Add",
                        ) { Icon(Lucide.Plus) }
                        Button(
                            variant = ButtonVariant.Secondary,
                            disabled = true,
                            iconPlacement = ButtonIconPlacement.Start,
                        ) {
                            Spinner()
                            Text(maxLines = 1) { words[3] }
                        }
                    }
                }
            }
    }
}

@Composable
private fun ButtonSizeRecipe() {
    val pairs: @Composable () -> Unit = {
        listOf(
            "Extra Small" to ButtonSize.Xs,
            "Small" to ButtonSize.Sm,
            "Default" to ButtonSize.Md,
            "Large" to ButtonSize.Lg,
        ).forEachIndexed {
                index,
                (text, size),
            ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(text, size = size, variant = ButtonVariant.Outline)
                Button(
                    variant = ButtonVariant.Outline,
                    size = listOf(ButtonSize.IconXs, ButtonSize.IconSm, ButtonSize.Icon, ButtonSize.IconLg)[index],
                    label = "Submit",
                ) {
                    Icon(Lucide.ArrowUpRight)
                }
            }
        }
    }
    val width =
        LocalCatalogViewportWidth.current ?: with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp()
        }
    if (width < 640.dp) {
        Column(
            Modifier.testTag("button-sizes"),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            content = { pairs() },
        )
    } else {
        Row(
            Modifier.testTag(
                "button-sizes",
            ),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top,
            content = {
                pairs()
            },
        )
    }
}

@Composable
internal fun ButtonRtlRecipe() {
    var language by remember { mutableIntStateOf(1) }
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it })
        Separator()
        Box(Modifier.fillMaxWidth().height(288.dp).padding(40.dp), contentAlignment = Alignment.Center) {
            val inherited = currentTheme
            val font =
                when (language) {
                    1 -> catalogArabicFontFamily()
                    2 -> catalogHebrewFontFamily()
                    else -> inherited.fontFamily
                }
            CompositionLocalProvider(
                LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
            ) {
                Theme(
                    preset = inherited.activePreset,
                    baseColor = inherited.activeBaseColor,
                    accent = inherited.activeAccent,
                    isDark = inherited.isDark, baseRadius = inherited.baseRadius, ring = inherited.ring,
                    unitSpacing = LocalUnitSpacing.current,
                    minTouchTarget = inherited.minTouchTarget,
                    fontFamily = font,
                ) { ButtonRecipe(14, language) }
            }
        }
    }
}

@Composable
private fun ButtonRecipeRow(
    kind: Int,
    content: @Composable () -> Unit,
) {
    if (kind == 14) {
        ButtonRtlFlow(content)
    } else {
        // CSS flex without flex-wrap keeps the natural row even when it exceeds the inner padding.
        Row(
            Modifier.testTag("button-recipe-$kind").wrapContentWidth(unbounded = true),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) { content() }
    }
}

/** CSS shrink-to-fit flex-wrap keeps the available width once its natural row overflows. */
@Composable
private fun ButtonRtlFlow(content: @Composable () -> Unit) {
    Layout(content = content, modifier = Modifier.testTag("button-recipe-14")) { measurables, constraints ->
        val gap = 8.dp.roundToPx()
        val children =
            measurables.map {
                it.measure(Constraints(maxHeight = constraints.maxHeight))
            }
        val preferredWidth = children.sumOf { it.width } + gap * (children.size - 1).coerceAtLeast(0)
        val width = constraints.constrainWidth(preferredWidth)
        var x = 0
        var y = 0
        var lineHeight = 0
        val positions =
            children.map { child ->
                if (x > 0 && x + child.width > width) {
                    x = 0
                    y += lineHeight + gap
                    lineHeight = 0
                }
                val position = x to y
                x += child.width + gap
                lineHeight = maxOf(lineHeight, child.height)
                position
            }
        layout(width, constraints.constrainHeight(y + lineHeight)) {
            children.forEachIndexed { index, child ->
                val (left, top) = positions[index]
                child.placeRelative(left, top)
            }
        }
    }
}
