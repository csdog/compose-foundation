@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import androidx.compose.ui.text.TextStyle

/** A simple "Section / Page" trail shown at the top of every detail screen. */
@Composable
fun Breadcrumb(
    vararg segments: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        segments.forEachIndexed { index, segment ->
            val isLast = index == segments.lastIndex
            Text(
                variant = TextVariant.LabelSmall,
                textStyle = if (!isLast) TextColors.Muted else TextStyle.Default
            ) { segment }
            if (!isLast) {
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "/" }
            }
        }
    }
}
