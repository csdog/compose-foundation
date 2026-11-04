@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.scroll_area_art_0
import compose_foundation.app.shared.generated.resources.scroll_area_art_1
import compose_foundation.app.shared.generated.resources.scroll_area_art_2
import org.jetbrains.compose.resources.painterResource
import com.toutiaoyan.compose.ui.components.scrollarea.*
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.theme.currentTheme

// region shared
@Composable
private fun scrollAreaRecipeFrame(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            border(colors.border)
        }
    }

@Composable
private fun scrollAreaRecipeViewport(): Style =
    rememberTheme {
        Style { shape(RoundedCornerShape(radius.md)) }
    }

@Composable
private fun ScrollAreaTags(
    title: String,
    dir: ScrollDir? = null,
) {
    ScrollArea(
        Modifier.width(192.dp).height(288.dp).testTag("scroll-area-example"),
        style = scrollAreaRecipeFrame(),
        viewportStyle = scrollAreaRecipeViewport(),
        dir = dir,
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                modifier =
                    Modifier.padding(bottom = 16.dp).semantics { heading() }.layout { measurable, constraints ->
                        val glyph = measurable.measure(constraints.copy(minHeight = 0))
                        val line = constraints.constrainHeight(14.sp.roundToPx())
                        layout(glyph.width, line) { glyph.placeRelative(0, (line - glyph.height) / 2) }
                    },
                textStyle =
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    ),
            ) { title }
            repeat(50) { index ->
                Text(
                    textStyle =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            lineHeightStyle =
                                LineHeightStyle(
                                    LineHeightStyle.Alignment.Center,
                                    LineHeightStyle.Trim.None,
                                ),
                        ),
                ) { "v1.2.0-beta.${50 - index}" }
                Separator(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
// endregion shared

// region default
@Composable
internal fun ScrollAreaDefaultRecipe() {
    ScrollAreaTags("Tags")
}
// endregion default

// region horizontal
@Composable
internal fun ScrollAreaHorizontalRecipe() {
    // Copy the three scroll_area_art_*.jpg drawables from docs/web-parity/scroll-area-r1/artwork-sources.json.

    val colors = currentTheme.colors
    val radius = com.toutiaoyan.compose.tailwind.core.twRadius.md
    val artwork =
        listOf(
            "Ornella Binni" to Res.drawable.scroll_area_art_0,
            "Tom Byrom" to Res.drawable.scroll_area_art_1,
            "Vladimir Malyavko" to Res.drawable.scroll_area_art_2,
        )
    ScrollArea(
        Modifier.width(384.dp).height(458.dp).testTag("scroll-area-example"),
        style = scrollAreaRecipeFrame(),
        viewportStyle = scrollAreaRecipeViewport(),
        orientation = ScrollAreaOrientation.Horizontal,
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            artwork.forEach { (artist, resource) ->
                Column(Modifier.width(300.dp)) {
                    Image(
                        painter = painterResource(resource),
                        contentDescription = "Photo by $artist",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(300.dp, 400.dp).clip(RoundedCornerShape(radius)),
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        textStyle =
                            TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = colors.mutedForeground,
                                lineHeightStyle =
                                    LineHeightStyle(
                                        LineHeightStyle.Alignment.Center,
                                        LineHeightStyle.Trim.None,
                                    ),
                            ),
                    ) {
                        buildAnnotatedString {
                            append("Photo by ")
                            withStyle(SpanStyle(color = colors.foreground, fontWeight = FontWeight.SemiBold)) {
                                append(artist)
                            }
                        }
                    }
                }
            }
        }
    }
}
// endregion horizontal

// region rtl
@Composable
internal fun ScrollAreaRtlRecipe(language: Int = 1) {
    ScrollAreaTags(
        listOf("Tags", "العلامات", "תגיות")[language],
        if (language == 0) ScrollDir.Ltr else ScrollDir.Rtl,
    )
}
// endregion rtl

internal val scrollAreaOfficialExamples =
    listOf(
        ComponentExample("Default", code = scrollAreaDefaultRecipeCode, preview = { ScrollAreaDefaultRecipe() }),
        ComponentExample(
            "Horizontal",
            code = scrollAreaHorizontalRecipeCode,
            preview = { ScrollAreaHorizontalRecipe() },
        ),
        ComponentExample("RTL", code = scrollAreaRtlRecipeCode, previewPadding = 0.dp, preview = {
            var language by remember { mutableIntStateOf(1) }
            RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                ScrollAreaRtlRecipe(language)
            }
        }),
    )
