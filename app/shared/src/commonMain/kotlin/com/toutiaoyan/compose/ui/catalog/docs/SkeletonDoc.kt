@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor

val skeletonDoc =
    ComponentDoc(
        id = "skeleton",
        title = "Skeleton",
        description = "Use to show a placeholder while content is loading.",
        examplesHeading = null,
        usageCode =
            """
            import androidx.compose.foundation.layout.size
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp
            import androidx.compose.foundation.shape.CircleShape
            import androidx.compose.foundation.style.Style
            import androidx.compose.foundation.style.then
            import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
            import com.toutiaoyan.compose.ui.components.skeleton.rememberSkeletonStyle

            Skeleton(
                modifier = Modifier.size(width = 100.dp, height = 20.dp),
                style = rememberSkeletonStyle() then Style { shape(CircleShape) },
            )
            """.trimIndent(),
        examples =
            skeletonOfficialExamples +
                listOf(
                    ComponentExample(
                        title = "Kotlin extension: Shimmer",
                        code = skeletonShimmerRecipeCode,
                        preview = { SkeletonShimmerRecipe() },
                    ),
                ),
    )

@Preview(showBackground = true, name = "Skeleton")
@Composable
private fun SkeletonPreview() {
    Theme(baseColor = BaseColor.Neutral) { SkeletonDefaultRecipe() }
}

@Preview(showBackground = true, name = "Skeleton Shimmer")
@Composable
private fun SkeletonShimmerPreview() {
    Theme(baseColor = BaseColor.Neutral) { SkeletonShimmerRecipe() }
}
