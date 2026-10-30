package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant

/** Eagerly composed examples must not steal focus or scroll the document on load. */
@Composable
internal fun AutofocusPreview(content: @Composable () -> Unit) {
    var attempt by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button("Try autofocus", onClick = { attempt++ }, variant = ButtonVariant.Outline)
        if (attempt > 0) key(attempt) { content() }
    }
}
