package com.toutiaoyan.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.skeleton.rememberSkeletonChrome
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor

@Composable
private fun SkeletonRows(shimmering: Boolean) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(currentTheme.colors.background)
                .padding(4.spacing.dp),
        verticalArrangement = Arrangement.spacedBy(2.spacing.dp),
    ) {
        repeat(4) {
            Skeleton(
                style = if (shimmering) rememberSkeletonChrome() else null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.spacing.dp)
                        .then(if (shimmering) Modifier.shimmer() else Modifier),
            )
        }
    }
}

@Preview(showBackground = true, name = "Skeleton", widthDp = 360, heightDp = 140)
@Composable
private fun SkeletonPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = false) {
        SkeletonRows(shimmering = false)
    }
}

// `bg-muted` on `background` is near-invisible in light mode; dark shows the fill.
@Preview(showBackground = true, name = "Skeleton / dark", widthDp = 360, heightDp = 140)
@Composable
private fun SkeletonDarkPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = true) {
        SkeletonRows(shimmering = false)
    }
}

@Preview(showBackground = true, name = "Skeleton Shimmer", widthDp = 360, heightDp = 140)
@Composable
private fun SkeletonShimmerPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = true) {
        SkeletonRows(shimmering = true)
    }
}