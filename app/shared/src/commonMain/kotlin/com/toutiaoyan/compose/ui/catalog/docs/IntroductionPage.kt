@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.toutiaoyan.compose.ui.catalog.rememberGuideScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing

val introductionPage =
    GuidePage(
        id = "introduction",
        title = "Introduction",
        content = { IntroductionContent() },
    )

@Composable
private fun IntroductionContent() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberGuideScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
    ) {
        Text(variant = TextVariant.TitleLarge) { "shadcn-compose" }
        GuideParagraph(
            "shadcn-compose is a shadcn/ui-inspired component library for Compose Multiplatform. " +
                "It ships token-based theming and sealed variant systems (ButtonVariant, CardVariant, " +
                "BadgeVariant, ...) built on the Compose Styles API -- there is no Material dependency " +
                "anywhere in the library.",
        )
        GuideHeading("Why not Material?")
        GuideParagraph(
            "Material3 bundles its own opinionated tokens, shapes, and component behavior. " +
                "shadcn-compose instead gives you full ownership of the token layer -- colors, " +
                "typography, shape, and spacing are plain data classes you can override completely, " +
                "the same way shadcn/ui hands you owned component source instead of a black-box npm package.",
        )
        GuideHeading("Platforms")
        GuideParagraph(
            "Every component targets Android, iOS (arm64 + simulator), Desktop (JVM), and Web " +
                "(JS + WasmJS) from a single commonMain source set.",
        )
        GuideHeading("Where to go next")
        GuideParagraph(
            "See Installation for the Gradle dependency, Theming for how Theme and design " +
                "tokens work, and Dark Mode for the light/dark switching model. Then browse the " +
                "component list in the sidebar below.",
        )
    }
}
