@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing

val darkModePage =
    GuidePage(
        id = "dark-mode",
        title = "Dark Mode",
        content = { DarkModeContent() },
    )

@Composable
private fun DarkModeContent() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
    ) {
        Text(variant = TextVariant.TitleLarge) { "Dark Mode" }
        GuideParagraph(
            "Theme follows the system's dark mode setting by default on every platform " +
                "(isSystemInDarkTheme() on Android/iOS/Desktop/Web) -- no configuration required.",
        )
        GuideHeading("Overriding the system setting")
        GuideParagraph(
            "To let users pick light/dark independently of the OS (a settings screen toggle), " +
                "provide LocalDarkTheme above Theme:",
        )
        CodeBlock(
            code =
                """
                val userPrefersDark: Boolean? by viewModel.darkModePreference.collectAsState()

                CompositionLocalProvider(LocalDarkTheme provides userPrefersDark) {
                    Theme {
                        CatalogNavHost()
                    }
                }
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
        GuideParagraph(
            "null follows the system, true forces dark, false forces light. Every component picks " +
                "up the change automatically since colors are read live from the Style API, not cached.",
        )
    }
}
