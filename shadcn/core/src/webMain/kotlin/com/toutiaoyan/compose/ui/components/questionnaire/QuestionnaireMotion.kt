package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
internal actual fun questionnaireAllowsMotion(): Boolean {
    val preference = remember { window.matchMedia("(prefers-reduced-motion: reduce)") }
    var reduced by remember(preference) { mutableStateOf(preference.matches) }
    DisposableEffect(preference) {
        val changed: (Event) -> Unit = { reduced = preference.matches }
        preference.addEventListener("change", changed)
        onDispose { preference.removeEventListener("change", changed) }
    }
    return !reduced
}
