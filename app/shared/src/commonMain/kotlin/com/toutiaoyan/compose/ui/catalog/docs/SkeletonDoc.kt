@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.skeleton.rememberSkeletonChrome
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor

val skeletonDoc =
    ComponentDoc(
        id = "skeleton",
        title = "Skeleton",
        description = "A placeholder block shown while content is loading (pulse by default; shimmer via Modifier).",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.skeleton.Skeleton

            Skeleton(modifier = Modifier.size(width = 200.dp, height = 20.dp))
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Skeleton(modifier = Modifier.size(width = 250.dp, height = 20.dp))
                            Skeleton(modifier = Modifier.size(width = 200.dp, height = 20.dp))
                        }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Skeleton(modifier = Modifier.size(width = 250.dp, height = 20.dp))
                            Skeleton(modifier = Modifier.size(width = 200.dp, height = 20.dp))
                        }
                    },
                ),
                ComponentExample(
                    title = "Avatar placeholder",
                    code = "Skeleton(modifier = Modifier.size(40.dp).clip(CircleShape))",
                    preview = {
                        Skeleton(modifier = Modifier.size(40.dp))
                    },
                ),
                ComponentExample(
                    title = "Shimmer",
                    code =
                        """
                        Skeleton(
                            style = rememberSkeletonChrome(),
                            modifier = Modifier.fillMaxWidth().height(16.dp).shimmer(),
                        )
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) {
                                Skeleton(
                                    style = rememberSkeletonChrome(),
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(16.dp)
                                            .shimmer(),
                                )
                            }
                        }
                    },
                ),
            ),
    )

@Preview(showBackground = true, name = "Skeleton")
@Composable
private fun SkeletonPreview() {
    Theme(baseColor = BaseColor.Neutral) {
        Column(
            modifier = Modifier.background(currentTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(2.spacing.dp),
        ) {
            repeat(4) {
                Skeleton(modifier = Modifier.fillMaxWidth().height(4.spacing.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Skeleton Shimmer")
@Composable
private fun SkeletonShimmerPreview() {
    Theme(baseColor = BaseColor.Neutral) {
        Column(
            modifier = Modifier.background(currentTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(2.spacing.dp),
        ) {
            repeat(4) {
                Skeleton(
                    style = rememberSkeletonChrome(),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(4.spacing.dp)
                            .shimmer(),
                )
            }
        }
    }
}