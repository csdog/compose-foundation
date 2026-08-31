package com.toutiaoyan.compose.ui.utils.clipboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * [copy] copies [text] to the system clipboard; [justCopied] is briefly true afterward so
 * callers can swap their button label to a "Copied!" confirmation.
 */
class CopyToClipboardState(val copy: () -> Unit, val justCopied: Boolean)

@Composable
fun rememberCopyToClipboard(text: String): CopyToClipboardState {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var justCopied by remember(text) { mutableStateOf(false) }
    val copy =
        remember(clipboard, text, scope) {
            fun() {
                scope.launch {
                    clipboard.setClipEntry(plainTextClipEntry(text))
                    justCopied = true
                }
            }
        }
    LaunchedEffect(justCopied) {
        if (justCopied) {
            delay(1500)
            justCopied = false
        }
    }
    return CopyToClipboardState(copy, justCopied)
}
