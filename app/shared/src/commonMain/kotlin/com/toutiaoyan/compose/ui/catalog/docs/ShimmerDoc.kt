@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing

val shimmerDoc =
    ComponentDoc(
        id = "shimmer",
        title = "Shimmer",
        description =
            "A sweeping highlight for \"generating response\"/\"thinking\" text (and solid " +
                    "blocks) — Modifier.shimmer (SrcAtop overlay) or rememberShimmerStyle " +
                    "(contentBrush / background-clip:text), matching ui.shadcn.com/docs/utils/shimmer.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
            import com.toutiaoyan.compose.ui.styles.shimmer

            Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer()) { "Generating response…" }
            Text(textStyle = TextStyles.Muted, style = rememberShimmerStyle()) { "Generating response…" }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default (Modifier)",
                    code = """Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer()) { "Generating response…" }""",
                    preview = {
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) {
                            "Generating response…"
                        }
                    },
                ),
                ComponentExample(
                    title = "Style (contentBrush)",
                    code = """Text(textStyle = TextStyles.Muted, style = rememberShimmerStyle()) { "Generating response…" }""",
                    preview = {
                        Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) {
                            "Generating response…"
                        }
                    },
                ),
                ComponentExample(
                    title = "Duration / angle / spread",
                    code =
                        """
                        Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer(durationMillis = 1000)) { "…" }
                        Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer(angleDegrees = 45f)) { "…" }
                        Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer(spread = 24.spacing.dp)) { "…" }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                textStyle = TextColors.Muted,
                                modifier = Modifier.shimmer(durationMillis = 1000),
                            ) { "duration 1000" }
                            Text(
                                textStyle = TextColors.Muted,
                                modifier = Modifier.shimmer(angleDegrees = 45f),
                            ) { "angle 45°" }
                            Text(
                                textStyle = TextColors.Muted,
                                modifier = Modifier.shimmer(spread = 24.spacing.dp),
                            ) { "spread-24" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Reverse / once",
                    code =
                        """
                        Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer(reverse = true)) { "…" }
                        Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer(once = true, durationMillis = 1100)) { "…" }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                textStyle = TextColors.Muted,
                                modifier = Modifier.shimmer(reverse = true),
                            ) { "reverse" }
                            Text(
                                textStyle = TextColors.Muted,
                                modifier = Modifier.shimmer(once = true, durationMillis = 1100),
                            ) { "once" }
                        }
                    },
                ),
            ),
    )
