package com.toutiaoyan.compose.ui.components.codeblock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.utils.clipboard.plainTextClipEntry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val COPY_CONFIRMATION_MILLIS = 2_000L

/** Copies exactly [value], including its original whitespace; confirms only a successful write. */
@Composable
fun CodeBlockCopyButton(
    value: String,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: ButtonSize = ButtonSize.IconXs,
    copyLabel: String = "Copy",
    copiedLabel: String = "Copied",
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var successfulCopies by remember(value) { mutableIntStateOf(0) }
    val confirmation = successfulCopies
    val copied = confirmation > 0
    LaunchedEffect(confirmation, value) {
        if (copied) {
            delay(COPY_CONFIRMATION_MILLIS)
            if (successfulCopies == confirmation) successfulCopies = 0
        }
    }
    Button(
        onClick = {
            scope.launch {
                try {
                    clipboard.setClipEntry(plainTextClipEntry(value))
                    successfulCopies++
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Clipboard permission can be denied by the platform/browser.
                }
            }
        },
        modifier =
            modifier.semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = if (copied) copiedLabel else ""
            },
        variant = variant,
        size = size,
        label = if (copied) copiedLabel else copyLabel,
    ) { Icon(if (copied) Check else CopyIcon) }
}

private val CopyIcon by lazy {
    ImageVector.Builder("Copy", 24.dp, 24.dp, 24f, 24f).apply {
        path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f) {
            moveTo(9f, 9f)
            lineTo(21f, 9f)
            lineTo(21f, 21f)
            lineTo(9f, 21f)
            close()
            moveTo(5f, 15f)
            lineTo(3f, 15f)
            lineTo(3f, 3f)
            lineTo(15f, 3f)
            lineTo(15f, 5f)
        }
    }.build()
}
