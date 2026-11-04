@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.focusable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius
import com.toutiaoyan.compose.tailwind.core.TwRadiusScale
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

// region shared
@Composable
private fun ScrollFadeSourceTheme(
    preset: StylePreset = StylePreset.Rhea,
    content: @Composable () -> Unit,
) {
    val inherited = currentTheme
    Theme(
        preset = preset,
        baseColor = inherited.activeBaseColor,
        accent = inherited.activeAccent,
        isDark = inherited.isDark,
        baseRadius = inherited.baseRadius,
        fontFamily = inherited.fontFamily,
        minTouchTarget = null,
    ) {
        // These are plain Tailwind divs: globals.css radius variables, not a .cn-* component personality.
        CompositionLocalProvider(LocalTwRadius provides TwRadiusScale.fromBaseRadius(inherited.baseRadius.value)) {
            content()
        }
    }
}

private val scrollFadeTags =
    listOf(
        "Design", "Engineering", "Marketing", "Product", "Research", "Sales", "Support",
        "Operations", "Finance", "Legal", "People", "Security",
    )

@Composable
private fun ScrollFadeTile(
    label: String,
    horizontal: Boolean,
) {
    val colors = currentTheme.colors
    Box(
        (if (horizontal) Modifier else Modifier.fillMaxWidth())
            .clip(
                RoundedCornerShape(twRadius.lg),
            ).background(colors.muted).padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            textStyle =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                ),
        ) { label }
    }
}

@Composable
private fun ScrollFadeFrame(
    items: List<String>,
    tag: String,
    modifier: Modifier = Modifier,
    height: Dp? = 288.dp,
    horizontal: Boolean = false,
    edge: ScrollFadeEdge = ScrollFadeEdge.Both,
    size: ScrollFadeSize = ScrollFadeSize.Default,
    enabled: Boolean = true,
) {
    val state = rememberScrollState()
    val colors = currentTheme.colors
    val shape = RoundedCornerShape(twRadius.xl2)
    Box(
        modifier.widthIn(max = 320.dp).fillMaxWidth()
            .border(1.dp, colors.border, shape).clip(shape).padding(1.dp),
    ) {
        val viewport =
            Modifier.fillMaxWidth().testTag("scroll-fade-$tag-viewport")
                .then(
                    if (horizontal) {
                        Modifier.height(52.dp)
                    } else if (height != null) {
                        Modifier.height(height)
                    } else {
                        Modifier
                    },
                )
                .scrollFade(
                    state,
                    edge = edge,
                    size = size,
                    enabled = enabled,
                    orientation = if (horizontal) ScrollFadeOrientation.Horizontal else ScrollFadeOrientation.Vertical,
                )
        if (horizontal) {
            Row(
                viewport.horizontalScroll(state).focusable().padding(6.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp),
            ) {
                items.forEach { ScrollFadeTile(it, true) }
            }
        } else {
            Column(
                viewport.verticalScroll(state).focusable().padding(6.dp),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp),
            ) {
                items.forEach { ScrollFadeTile(it, false) }
            }
        }
    }
}

@Composable
private fun ScrollFadeCaption(
    label: String,
    mono: FontFamily,
) {
    Text(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
        textStyle =
            TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = mono,
                color = currentTheme.colors.mutedForeground,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
            ),
    ) { label }
}
// endregion shared

// region default
@Composable
internal fun ScrollFadeDefaultRecipe() =
    ScrollFadeSourceTheme {
        ScrollFadeFrame(List(12) { "Item ${it + 1}" }, "default", Modifier.testTag("scroll-fade-example"))
    }
// endregion default

// region overflow
@Composable
internal fun ScrollFadeOverflowRecipe() =
    ScrollFadeSourceTheme {
        ScrollFadeFrame(
            List(3) { "Item ${it + 1}" },
            "overflow",
            Modifier.testTag("scroll-fade-example"),
            height = null,
        )
    }
// endregion overflow

// region horizontal
@Composable
internal fun ScrollFadeHorizontalRecipe() =
    ScrollFadeSourceTheme {
        ScrollFadeFrame(scrollFadeTags, "horizontal", Modifier.testTag("scroll-fade-example"), horizontal = true)
    }
// endregion horizontal

// region edge
@Composable
internal fun ScrollFadeEdgeRecipe(mono: FontFamily = FontFamily.Monospace) =
    ScrollFadeSourceTheme {
        val items =
            listOf("Inbox triage", "Design review", "API contract", "QA pass", "Launch notes", "Metrics follow-up")
        Column(
            Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("scroll-fade-example"),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            listOf(
                ScrollFadeEdge.Top to "t",
                ScrollFadeEdge.Bottom to "b",
                ScrollFadeEdge.Start to "s",
                ScrollFadeEdge.End to "e",
            ).forEach { (edge, suffix) ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val horizontal = edge == ScrollFadeEdge.Start || edge == ScrollFadeEdge.End
                    ScrollFadeFrame(
                        if (horizontal) scrollFadeTags.take(8) else items,
                        suffix,
                        height = 144.dp,
                        horizontal = horizontal,
                        edge = edge,
                    )
                    ScrollFadeCaption("scroll-fade-$suffix", mono)
                }
            }
        }
    }
// endregion edge

// region size
@Composable
internal fun ScrollFadeSizeRecipe(mono: FontFamily = FontFamily.Monospace) =
    ScrollFadeSourceTheme {
        Column(
            Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("scroll-fade-example"),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            listOf(4, 24).forEach { units ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ScrollFadeFrame(
                        List(8) { "Item ${it + 1}" },
                        "size-$units",
                        height = 192.dp,
                        size = ScrollFadeSize.Length((units * 4).dp),
                    )
                    ScrollFadeCaption("scroll-fade-$units", mono)
                }
            }
        }
    }
// endregion size

// region none
@Composable
internal fun ScrollFadeNoneRecipe(mono: FontFamily = FontFamily.Monospace) =
    ScrollFadeSourceTheme {
        Column(
            Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("scroll-fade-example"),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            listOf(true, false).forEach { enabled ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ScrollFadeFrame(
                        List(8) { "Item ${it + 1}" },
                        if (enabled) {
                            "on"
                        } else {
                            "off"
                        },
                        height =
                            192.dp,
                        enabled =
                        enabled,
                    )
                    ScrollFadeCaption(if (enabled) "scroll-fade" else "scroll-fade scroll-fade-none", mono)
                }
            }
        }
    }
// endregion none

// region rtl
@Composable
internal fun ScrollFadeRtlRecipe(language: Int = 1) =
    ScrollFadeSourceTheme(StylePreset.Nova) {
        val tags =
            listOf(
                scrollFadeTags.take(10),
                listOf("تصميم", "هندسة", "تسويق", "منتج", "أبحاث", "مبيعات", "دعم", "عمليات", "مالية", "قانوني"),
                listOf("עיצוב", "הנדסה", "שיווק", "מוצר", "מחקר", "מכירות", "תמיכה", "תפעול", "כספים", "משפטי"),
            )
        CompositionLocalProvider(
            LocalLayoutDirection provides
                if (language == 0) {
                    LayoutDirection.Ltr
                } else {
                    LayoutDirection.Rtl
                },
        ) {
            ScrollFadeFrame(tags[language], "rtl", Modifier.testTag("scroll-fade-example"), horizontal = true)
        }
    }
// endregion rtl

internal val scrollFadeOfficialExamples =
    listOf(
        ComponentExample("Default", code = scrollFadeDefaultRecipeCode, preview = { ScrollFadeDefaultRecipe() }),
        ComponentExample(
            "No Overflow, No Fade",
            code =
            scrollFadeOverflowRecipeCode,
            preview =
                { ScrollFadeOverflowRecipe() },
        ),
        ComponentExample(
            "Horizontal Scrolling",
            code =
            scrollFadeHorizontalRecipeCode,
            preview =
                { ScrollFadeHorizontalRecipe() },
        ),
        ComponentExample("Edge Fades", code = scrollFadeEdgeRecipeCode, preview = {
            ScrollFadeEdgeRecipe(com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily())
        }),
        ComponentExample("Fade Size", code = scrollFadeSizeRecipeCode, preview = {
            ScrollFadeSizeRecipe(com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily())
        }),
        ComponentExample("Disabling the Fade", code = scrollFadeNoneRecipeCode, preview = {
            ScrollFadeNoneRecipe(com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily())
        }),
        ComponentExample("RTL", code = scrollFadeRtlRecipeCode, previewPadding = 0.dp, preview = {
            var language by remember { mutableIntStateOf(1) }
            RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                ScrollFadeRtlRecipe(language)
            }
        }),
    )
