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

val textDoc =
    ComponentDoc(
        id = "text",
        title = "Text",
        description = "Renders text using the design system's type scale, with optional TextStyles.Muted preset.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.components.text.TextStyles
            import com.toutiaoyan.compose.ui.components.text.TextTypography

            Text(typography = TextTypography.TitleLarge) { "Title" }
            Text(textStyle = TextStyles.Muted) { "Subtitle" }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Type scale",
                    code =
                        """
                        Text(typography = TextTypography.TitleLarge) { "Title Large" }
                        Text(typography = TextTypography.BodyMedium) { "Body Medium" }
                        """.trimIndent(),
                    preview = {
                        Column {
                            Text(variant = TextVariant.TitleLarge) { "Title Large" }
                            Text(variant = TextVariant.BodyMedium) { "Body Medium" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code = """Text(variant = TextVariant.BodyMedium) { "Body Medium" }""",
                    preview = { Text(variant = TextVariant.BodyMedium) { "Body Medium" } },
                ),
                ComponentExample(
                    title = "Muted",
                    code = """Text(typography = TextTypography.BodyMedium, textStyle = TextStyles.Muted) { "Muted body" }""",
                    preview = { Text(variant = TextVariant.BodyMedium, textStyle = TextColors.Muted) { "Muted body" } },
                ),
            ),
    );

private const val PreviewBg = 0xFFFFFFFF

@Preview(
    showBackground = true,
    backgroundColor = PreviewBg,
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
    backgroundColor = PreviewBg,
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
    backgroundColor = PreviewBg,
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
                "fontSizeSm + TextStyles.Muted"
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = PreviewBg,
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
            Text(style = Style { textXl2(); fontWeightBold() }, textStyle = TextColors.Muted) {
                "textXl2 + bold + muted"
            }
        }
    }
}
