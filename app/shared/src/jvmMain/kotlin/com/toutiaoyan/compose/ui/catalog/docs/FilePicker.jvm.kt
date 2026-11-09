package com.toutiaoyan.compose.ui.catalog.docs

import javax.swing.JFileChooser
import javax.swing.SwingUtilities

internal actual fun platformFilePicker(): FilePicker? =
    { onResult ->
        SwingUtilities.invokeLater {
            val chooser = JFileChooser()
            val filename =
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    chooser.selectedFile.name
                } else {
                    null
                }
            onResult(filename)
        }
    }
