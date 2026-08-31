@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant

@Composable
fun GuideHeading(text: String) {
    Text(variant = TextVariant.TitleMedium) { text }
}

@Composable
fun GuideParagraph(text: String) {
    Text(variant = TextVariant.BodyLarge) { text }
}
