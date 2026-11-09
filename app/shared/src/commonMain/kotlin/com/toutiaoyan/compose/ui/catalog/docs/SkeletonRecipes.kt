@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.skeleton.rememberSkeletonStyle
import com.toutiaoyan.compose.ui.components.skeleton.rememberSkeletonChrome
import com.toutiaoyan.compose.ui.styles.shimmer

// region shared
@Composable
private fun SkeletonProfile(
    avatar: Dp,
    first: Dp,
    second: Dp,
) {
    Row(
        // The source flex row keeps its fixed-width children at their natural width,
        // even when the centered preview's padded content area is narrower.
        Modifier.requiredWidth(avatar + 16.dp + maxOf(first, second)).testTag("skeleton-example"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Skeleton(
            Modifier.size(avatar).testTag("skeleton-avatar"),
            style = rememberSkeletonStyle() then Style { shape(CircleShape) },
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Skeleton(Modifier.size(first, 16.dp).testTag("skeleton-line-0"))
            Skeleton(Modifier.size(second, 16.dp).testTag("skeleton-line-1"))
        }
    }
}
// endregion shared

// region default
@Composable
internal fun SkeletonDefaultRecipe() {
    SkeletonProfile(48.dp, 250.dp, 200.dp)
}
// endregion default

// region avatar
@Composable
internal fun SkeletonAvatarRecipe() {
    SkeletonProfile(40.dp, 150.dp, 100.dp)
}
// endregion avatar

// region card
@Composable
internal fun SkeletonCardRecipe() {
    Card(Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("skeleton-example")) {
        CardHeader {
            Skeleton(Modifier.fillMaxWidth(2f / 3f).height(16.dp).testTag("skeleton-line-0"))
            Skeleton(Modifier.fillMaxWidth(.5f).height(16.dp).testTag("skeleton-line-1"))
        }
        CardContent {
            Skeleton(Modifier.fillMaxWidth().aspectRatio(16f / 9f).testTag("skeleton-image"))
        }
    }
}
// endregion card

// region text
@Composable
internal fun SkeletonTextRecipe() {
    Column(
        Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("skeleton-example"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Skeleton(Modifier.fillMaxWidth().height(16.dp).testTag("skeleton-line-0"))
        Skeleton(Modifier.fillMaxWidth().height(16.dp).testTag("skeleton-line-1"))
        Skeleton(Modifier.fillMaxWidth(.75f).height(16.dp).testTag("skeleton-line-2"))
    }
}
// endregion text

// region form
@Composable
internal fun SkeletonFormRecipe() {
    Column(
        Modifier.widthIn(max = 320.dp).fillMaxWidth().testTag("skeleton-example"),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Skeleton(Modifier.size(80.dp, 16.dp).testTag("skeleton-label-0"))
            Skeleton(Modifier.fillMaxWidth().height(32.dp).testTag("skeleton-input-0"))
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Skeleton(Modifier.size(96.dp, 16.dp).testTag("skeleton-label-1"))
            Skeleton(Modifier.fillMaxWidth().height(32.dp).testTag("skeleton-input-1"))
        }
        Skeleton(Modifier.size(96.dp, 32.dp).testTag("skeleton-button"))
    }
}
// endregion form

// region table
@Composable
internal fun SkeletonTableRecipe() {
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("skeleton-example"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(5) { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Skeleton(Modifier.weight(1f).height(16.dp).testTag("skeleton-cell-$row-0"))
                Skeleton(Modifier.size(96.dp, 16.dp).testTag("skeleton-cell-$row-1"))
                Skeleton(Modifier.size(80.dp, 16.dp).testTag("skeleton-cell-$row-2"))
            }
        }
    }
}
// endregion table

// region rtl
@Composable
internal fun SkeletonRtlRecipe(language: Int = 1) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
    ) {
        SkeletonProfile(48.dp, 250.dp, 200.dp)
    }
}
// endregion rtl

// region shimmer

/** Project extension, separate from the seven upstream recipes. */
@Composable
internal fun SkeletonShimmerRecipe() {
    Column(
        Modifier.fillMaxWidth().testTag("skeleton-example"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(3) { index ->
            Skeleton(
                style = rememberSkeletonChrome(),
                modifier = Modifier.fillMaxWidth().height(16.dp).shimmer().testTag("skeleton-line-$index"),
            )
        }
    }
}
// endregion shimmer

internal val skeletonOfficialExamples =
    listOf(
        ComponentExample("Default", code = skeletonDefaultRecipeCode, preview = { SkeletonDefaultRecipe() }),
        ComponentExample("Avatar", code = skeletonAvatarRecipeCode, preview = { SkeletonAvatarRecipe() }),
        ComponentExample("Card", code = skeletonCardRecipeCode, preview = { SkeletonCardRecipe() }),
        ComponentExample("Text", code = skeletonTextRecipeCode, preview = { SkeletonTextRecipe() }),
        ComponentExample("Form", code = skeletonFormRecipeCode, preview = { SkeletonFormRecipe() }),
        ComponentExample("Table", code = skeletonTableRecipeCode, preview = { SkeletonTableRecipe() }),
        ComponentExample(
            "RTL",
            code = skeletonRtlRecipeCode,
            previewPadding = 0.dp,
            descriptionMarkdown =
                "To enable RTL support in shadcn/ui, see the " +
                    "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
            preview = {
                var language by remember { mutableIntStateOf(1) }
                RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                    SkeletonRtlRecipe(language)
                }
            },
        ),
    )
