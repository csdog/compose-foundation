@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.style.fontSizeBase
import com.toutiaoyan.compose.tailwind.style.fontSizeLg
import com.toutiaoyan.compose.tailwind.style.fontSizeSm
import com.toutiaoyan.compose.tailwind.style.fontSizeXl
import com.toutiaoyan.compose.tailwind.style.fontSizeXl2
import com.toutiaoyan.compose.tailwind.style.fontSizeXs
import com.toutiaoyan.compose.tailwind.style.fontWeightBold
import com.toutiaoyan.compose.tailwind.style.textLg
import com.toutiaoyan.compose.tailwind.style.textSm
import com.toutiaoyan.compose.tailwind.style.textXl2
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor

/**
 * Kotlin extension page. shadcn/ui ships no `Text` component -- styling is expressed with
 * Tailwind classes -- so there is no official Base UI page to mirror. This page documents the
 * actual Kotlin API and is labeled as this catalog's own extension.
 */
val textDoc =
    ComponentDoc(
        id = "text",
        isKotlinExtension = true,
        title = "Text",
        description =
            "Renders text using the design system's type scale, with the optional TextColors.Muted preset.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.text.*",
        usageCode =
            """
            Text(variant = TextVariant.TitleLarge) { "Title" }
            Text(textStyle = TextColors.Muted) { "Subtitle" }
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    title = "Kotlin extension",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                (
                                    "Text is a Kotlin extension of this catalog, not an official " +
                                        "shadcn/ui component. shadcn styles text with Tailwind utility " +
                                        "classes instead of a standalone component, so there is no Base UI " +
                                        "reference page or upstream recipe list. The API below is the " +
                                        "current Kotlin source of truth: a thin wrapper around BasicText " +
                                        "that maps the theme's type scale and muted color preset."
                                ),
                            ),
                        ),
                ),
            ),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Type scale",
                    showHeading = false,
                    code =
                        """
                        Text(variant = TextVariant.TitleLarge) { "Title Large" }
                        Text(variant = TextVariant.BodyMedium) { "Body Medium" }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(variant = TextVariant.TitleLarge) { "Title Large" }
                            Text(variant = TextVariant.BodyMedium) { "Body Medium" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Variants",
                    descriptionMarkdown =
                        "Every `TextVariant` maps to one entry of the theme type scale; " +
                            "`BodyMedium` is the default.",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextVariant.entries.forEach { variant ->
                                Text(variant = variant) { variant.name }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextVariant.entries.forEach { variant ->
                                Text(variant = variant) { variant.name }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Muted",
                    descriptionMarkdown = "Pass `TextColors.Muted` through `textStyle` for secondary copy.",
                    code =
                        """
                        Text(variant = TextVariant.BodyMedium, textStyle = TextColors.Muted) { "Muted body" }
                        """.trimIndent(),
                    preview = {
                        Text(variant = TextVariant.BodyMedium, textStyle = TextColors.Muted) { "Muted body" }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Style font-size",
                    descriptionMarkdown =
                        "The Style API can set a font size on the wrapper without changing the " +
                            "`TextVariant`; `fontSizeSm()` / `textSm()` are project utilities.",
                    code =
                        """
                        Text(style = Style { fontSizeSm() }) { "Style { fontSizeSm() }" }
                        Text(style = Style { textLg() }) { "Style { textLg() } (size + leading)" }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(style = Style { fontSizeSm() }) { "Style { fontSizeSm() }" }
                            Text(style = Style { textLg() }) { "Style { textLg() } (size + leading)" }
                        }
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Text",
                    "Wraps BasicText with the design-system type scale. `textStyle` can carry a " +
                        "preset such as `TextColors.Muted` or a caller TextStyle; `text` is a lambda so " +
                        "the string is read at composition time.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("variant", "TextVariant", "BodyMedium"),
                        ApiProperty("maxLines", "Int", "Int.MAX_VALUE"),
                        ApiProperty("overflow", "TextOverflow", "Clip"),
                        ApiProperty("textStyle", "TextStyle", "TextStyle.Default"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("text", "() -> String", "Required"),
                    ),
                ),
            ),
    )

private const val PREVIEW_BG = 0xFFFFFFFF

@Preview(
    showBackground = true,
    backgroundColor = PREVIEW_BG,
    widthDp = 360,
    heightDp = 120,
    name = "Smoke / BasicText",
)
@Composable
private fun TextSmokePreview() {
    BasicText(
        text = "Preview OK",
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
        style = TextStyle(color = Color.Red, fontSize = 20.sp),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = PREVIEW_BG,
    widthDp = 360,
    heightDp = 420,
    name = "Text / variants",
)
@Composable
private fun TextVariantsPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = false, minTouchTarget = null) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextVariant.entries.forEach { variant ->
                Text(variant = variant) { variant.name }
            }
            Text(textStyle = TextColors.Muted) { "Muted body text" }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = PREVIEW_BG,
    widthDp = 360,
    heightDp = 320,
    name = "Text / Style { fontSize }",
)
@Composable
private fun TextFontSizeStylePreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = false, minTouchTarget = null) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(style = Style { fontSizeXs() }) { "Style { fontSizeXs() }" }
            Text(style = Style { fontSizeSm() }) { "Style { fontSizeSm() }" }
            Text(style = Style { fontSizeBase() }) { "Style { fontSizeBase() }" }
            Text(style = Style { fontSizeLg() }) { "Style { fontSizeLg() }" }
            Text(style = Style { fontSizeXl() }) { "Style { fontSizeXl() }" }
            Text(style = Style { fontSizeXl2() }) { "Style { fontSizeXl2() }" }
            Text(style = Style { fontSizeSm() }, textStyle = TextColors.Muted) {
                "fontSizeSm + TextColors.Muted"
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = PREVIEW_BG,
    widthDp = 360,
    heightDp = 200,
    name = "Text / Style.text*",
)
@Composable
private fun TextTextStylePreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = false, minTouchTarget = null) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(style = Style { textSm() }) { "Style { textSm() } (size + leading)" }
            Text(style = Style { textLg() }) { "Style { textLg() } (size + leading)" }
            Text(
                style =
                    Style {
                        textXl2()
                        fontWeightBold()
                    },
                textStyle = TextColors.Muted,
            ) {
                "textXl2 + bold + muted"
            }
        }
    }
}
