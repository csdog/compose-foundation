@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.toutiaoyan.compose.ui.catalog.rememberGuideScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing

val themingPage =
    GuidePage(
        id = "theming",
        title = "Theming",
        content = { ThemingContent() },
    )

@Composable
private fun ThemingContent() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberGuideScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
    ) {
        Text(variant = TextVariant.TitleLarge) { "Theming" }
        GuideParagraph(
            "Every screen must be wrapped in Theme once, at the app root. It provides the " +
                "current Colors, Typography, TwRadiusScale, and TwSpacingScale to the whole tree:",
        )
        CodeBlock(
            code =
                """
                setContent {
                    Theme {
                        CatalogNavHost()
                    }
                }
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
        GuideHeading("Reading tokens in a composable")
        GuideParagraph("Use the currentTheme accessor -- it's a @Composable property, not a static object:")
        CodeBlock(
            code =
                """
                val theme = currentTheme
                Box(Modifier.background(theme.colors.primary).padding(twSpacing.lg))
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
        GuideHeading("Reading tokens inside a Style")
        GuideParagraph(
            "Styles run outside Composition, so currentTheme is not available there. Use the " +
                "StyleScope extensions (colors, typography, twRadius / spacing) instead -- they read " +
                "the CompositionLocal at the moment the style is applied, not at definition time:",
        )
        CodeBlock(
            code =
                """
                val myStyle = Style {
                    background(colors.primary)   // StyleScope.colors, not currentTheme.colors
                    shape(RoundedCornerShape(twRadius.md))
                    contentPadding(spacing.md)
                }
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
        GuideHeading("Owning your own palette")
        GuideParagraph(
            "Colors, Typography, TwRadiusScale (via twRadius), and TwSpacingScale (via twSpacing) " +
                "are plain data classes. Override them through Theme(preset = …) / baseColor / accent, " +
                "or provide LocalTwRadius / LocalTwSpacing yourself.",
        )
        GuideHeading("Three independent theming axes")
        GuideParagraph(
            "Mirrors shadcn/ui's real project creator (ui.shadcn.com/create), which has three " +
                "separate pickers rather than one combined \"theme\": StylePreset (Vega, Nova, " +
                "Maia, Lyra, Mira, Luma, Sera, Rhea -- shape/spacing/typography), BaseColor " +
                "(Neutral, Stone, Zinc, Mauve, Olive, Mist, Taupe -- the whole neutral palette), and " +
                "Accent (Blue, Rose, Violet, and 14 more -- primary/primaryForeground only). Background, " +
                "border, muted, and the focus ring color come from Base color alone and never change " +
                "with the accent, matching real shadcn. Try the rows in the top bar -- every component " +
                "on every page re-themes instantly, in both light and dark mode.",
        )
        CodeBlock(
            code =
                """
                var style by remember { mutableStateOf(StylePreset.Vega) }
                var base by remember { mutableStateOf(BaseColor.Zinc) }
                var accent by remember { mutableStateOf(Accent.Violet) }

                Theme(
                    preset = style,
                    baseColor = base,
                    accent = accent,
                    isDark = isDark,
                ) { /* ... */ }
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
