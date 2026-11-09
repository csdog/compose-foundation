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
        title = "shimmer",
        description = "Utilities for adding a shimmer effect to text elements.",
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin extension: CSS class mapping",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The CSS utilities map to the Compose Style API and modifier options:",
                        ),
                        ComponentGuideBlock.Table(
                            headers = listOf("Class", "CSS behavior", "Kotlin equivalent"),
                            rows =
                                listOf(
                                    listOf(
                                        "`shimmer`",
                                        "`background-clip: text; linear infinite sweep (2s)`",
                                        "`style = rememberShimmerStyle()`",
                                    ),
                                    listOf(
                                        "`shimmer-once`",
                                        "`animation-iteration-count: 1`",
                                        "`once = true`",
                                    ),
                                    listOf(
                                        "`shimmer-reverse`",
                                        "`animation-direction: reverse`",
                                        "`reverse = true`",
                                    ),
                                    listOf(
                                        "`shimmer-none`",
                                        "`No gradient; currentColor text fill`",
                                        "`enabled = false`",
                                    ),
                                    listOf(
                                        "`shimmer-color-<color>`",
                                        "`--shimmer-color: <color>`",
                                        "`color = TwColors.blue500`",
                                    ),
                                    listOf(
                                        "`shimmer-color-[<value>]`",
                                        "`Arbitrary highlight color`",
                                        "`color = Color(0xFF378ADD)`",
                                    ),
                                    listOf(
                                        "`shimmer-color-<color>/<pct>`",
                                        "`OKLCH color mix with transparent`",
                                        "`color = TwColors.blue500.copy(alpha = .6f)`",
                                    ),
                                    listOf(
                                        "`shimmer-duration-<number>`",
                                        "`--shimmer-duration: <number> milliseconds`",
                                        "`durationMillis = 1000`",
                                    ),
                                    listOf(
                                        "`shimmer-spread-<number>`",
                                        "`--shimmer-spread: spacing × <number>`",
                                        "`spread = 24.spacing.dp`",
                                    ),
                                    listOf(
                                        "`shimmer-spread-[<value>]`",
                                        "`Arbitrary length or percentage`",
                                        "`spread = 80.dp (length only)`",
                                    ),
                                    listOf(
                                        "`shimmer-angle-<number>`",
                                        "`--shimmer-angle: <number> degrees`",
                                        "`angleDegrees = 45f`",
                                    ),
                                ),
                        ),
                        ComponentGuideBlock.Paragraph(
                            "The Style content brush follows the CSS text-fill behavior. " +
                                "`Modifier.shimmer()` is a Kotlin overlay extension for blocks and groups. " +
                                "The default spread is a fixed 64.dp approximation of `calc(3ch + 40px)`; " +
                                "font-relative lengths and percentage spreads are not supported by the Dp API.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "The shimmer is built on `currentColor`, so it adapts to the " +
                                "element: the highlight derives from the text color, it works on " +
                                "any color from `text-muted-foreground` to brand colors, and in " +
                                "dark mode the highlight automatically brightens to stay visible. " +
                                "The effect paints the text with a content brush and sweeps the " +
                                "highlight in a seamless loop.",
                        ),
                    ),
                ),
            ),
        beforeExampleSections =
            mapOf(
                "RTL" to
                    listOf(
                        ComponentGuideSection(
                            "Fallback",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "The shimmer is built on modern color features, relative color syntax " +
                                        "and `color-mix()`. In older browsers without support the highlight " +
                                        "gradient is dropped and the text can render transparent; apply " +
                                        "`shimmer` conditionally with a `supports-*` variant there. The " +
                                        "Compose renderer resolves its gradient in sRGB/Oklch directly, so " +
                                        "there is no browser fallback path.",
                                ),
                            ),
                        ),
                        ComponentGuideSection(
                            "Reduced Motion",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "When the user prefers reduced motion, the CSS animation is disabled " +
                                        "automatically. Compose animations honor a host-provided " +
                                        "`MotionDurationScale` of zero, finishing the sweep and restoring " +
                                        "normal text. This utility does not itself query browser " +
                                        "`prefers-reduced-motion`; callers can pass `enabled = false` " +
                                        "when a platform preference is available.",
                                ),
                            ),
                        ),
                    ),
            ),
        afterExampleSections =
            mapOf(
                "Spread" to
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "For a one-off length, use `spread = 80.dp`, corresponding to " +
                                "`shimmer-spread-[5rem]` at a 16px root font. Percentage and " +
                                "font-relative spreads require an explicit Dp conversion in Compose.",
                        ),
                        ComponentGuideBlock.Code(
                            "Text(style = rememberShimmerStyle(spread = 80.dp)) { \"Generating response…\" }",
                        ),
                    ),
            ),
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.components.text.TextColors
            import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle

            Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) { "Generating response…" }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Kotlin extension: Overlay modifier",
                    code =
                        """
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) { "Generating response…" }
                        """.trimIndent(),
                    preview = {
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) {
                            "Generating response…"
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Style content brush",
                    code =
                        """
                        Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) { "Generating response…" }
                        """.trimIndent(),
                    preview = {
                        Text(textStyle = TextColors.Muted, style = rememberShimmerStyle()) {
                            "Generating response…"
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Duration, angle, spread",
                    code =
                        """
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer(durationMillis = 1000)) { "…" }
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer(angleDegrees = 45f)) { "…" }
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer(spread = 24.spacing.dp)) { "…" }
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
                    title = "Kotlin extension: Reverse and once",
                    code =
                        """
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer(reverse = true)) { "…" }
                        Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer(once = true, durationMillis = 1100)) { "…" }
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
        .let { native -> native.copy(examples = shimmerOfficialExamples + native.examples) }
