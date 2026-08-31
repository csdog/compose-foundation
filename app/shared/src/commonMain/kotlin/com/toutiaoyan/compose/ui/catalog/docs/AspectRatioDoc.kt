@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.aspectratio.AspectRatio
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.currentTheme

val aspectRatioDoc =
    ComponentDoc(
        id = "aspect-ratio",
        title = "Aspect Ratio",
        description = "Constrains content to a fixed width/height ratio.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.aspectratio.AspectRatio

            AspectRatio(ratio = 16f / 9f) {
                // your image / content
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "16:9",
                    code =
                        """
                        AspectRatio(ratio = 16f / 9f, modifier = Modifier.width(240.dp)) {
                            Box(modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted))
                        }
                        """.trimIndent(),
                    preview = {
                        AspectRatio(ratio = 16f / 9f, modifier = Modifier.width(240.dp)) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "16:9" }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "1:1",
                    code =
                        """
                        AspectRatio(modifier = Modifier.width(160.dp)) {
                            Box(modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted))
                        }
                        """.trimIndent(),
                    preview = {
                        AspectRatio(modifier = Modifier.width(160.dp)) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "1:1" }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default ratio",
                    code =
                        """
                        AspectRatio(ratio = 1f, modifier = Modifier.width(160.dp)) {
                            Box(modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted))
                        }
                        """.trimIndent(),
                    preview = {
                        AspectRatio(ratio = 1f, modifier = Modifier.width(160.dp)) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "1:1" }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "4:3",
                    code =
                        """
                        AspectRatio(ratio = 4f / 3f, modifier = Modifier.width(200.dp)) {
                            Box(modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted))
                        }
                        """.trimIndent(),
                    preview = {
                        AspectRatio(ratio = 4f / 3f, modifier = Modifier.width(200.dp)) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "4:3" }
                            }
                        }
                    },
                ),
            ),
    )
