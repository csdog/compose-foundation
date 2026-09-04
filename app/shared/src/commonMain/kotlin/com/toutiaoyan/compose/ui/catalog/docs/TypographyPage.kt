@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing

val typographyPage =
    GuidePage(
        id = "typography",
        title = "Typography",
        content = { TypographyContent() },
    )

@Composable
private fun TypographyContent() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
    ) {
        Text(variant = TextVariant.TitleLarge) { "Typography" }
        GuideParagraph(
            "Text renders the TextTypography type scale. Every style below maps to a " +
                "Typography token, so changing the token updates every screen at once.",
        )
        Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Text(variant = TextVariant.DisplayLarge) { "Display Large" }
            Text(variant = TextVariant.DisplayMedium) { "Display Medium" }
            Text(variant = TextVariant.TitleLarge) { "Title Large" }
            Text(variant = TextVariant.TitleMedium) { "Title Medium" }
            Text(variant = TextVariant.TitleSmall) { "Title Small" }
            Text(variant = TextVariant.BodyLarge) { "Body Large" }
            Text(variant = TextVariant.BodyMedium) { "Body Medium" }
            Text(variant = TextVariant.BodySmall) { "Body Small" }
            Text(variant = TextVariant.LabelLarge) { "Label Large" }
            Text(variant = TextVariant.LabelSmall) { "Label Small" }
        }
        GuideHeading("Muted text")
        GuideParagraph(
            "Use textStyle = TextStyles.Muted for secondary/supporting text -- it reads " +
                "mutedForeground instead of foreground:",
        )
        Text(variant = TextVariant.BodyMedium, textStyle = TextColors.Muted) { "This is muted body text" }
    }
}
