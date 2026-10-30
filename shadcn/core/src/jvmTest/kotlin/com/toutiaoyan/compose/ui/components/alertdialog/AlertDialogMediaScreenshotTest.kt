package com.toutiaoyan.compose.ui.components.alertdialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class AlertDialogMediaScreenshotTest : ScreenshotTest() {
    private fun media(
        small: Boolean,
        dark: Boolean,
    ) {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            AlertDialog(defaultOpen = true) {
                AlertDialogContent(size = if (small) DialogSize.Sm else DialogSize.Default) {
                    AlertDialogHeader {
                        AlertDialogMedia { Box(Modifier.size(24.dp)) }
                        AlertDialogTitle(if (small) "Allow accessory to connect?" else "Share this project?")
                        AlertDialogDescription(
                            if (small) {
                                "Do you want to allow the USB accessory to connect to this device?"
                            } else {
                                "Anyone with the link will be able to view and edit this project."
                            },
                        )
                    }
                    AlertDialogFooter {
                        AlertDialogCancel(if (small) "Don't allow" else "Cancel")
                        AlertDialogAction(if (small) "Allow" else "Share")
                    }
                }
            }
        }
        captureNamed(if (small) "alert_dialog_nova_small_media" else "alert_dialog_nova_media", dark)
    }

    @Test fun mediaLight() = media(false, false)

    @Test fun mediaDark() = media(false, true)

    @Test fun smallMediaLight() = media(true, false)

    @Test fun smallMediaDark() = media(true, true)
}
