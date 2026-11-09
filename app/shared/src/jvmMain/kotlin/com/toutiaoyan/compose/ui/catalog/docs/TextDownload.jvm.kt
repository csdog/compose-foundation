package com.toutiaoyan.compose.ui.catalog.docs

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import javax.swing.JFileChooser

internal actual fun platformTextDownloader(): TextDownloader? =
    { value, filename ->
        val chooser = JFileChooser().apply { selectedFile = java.io.File(filename) }
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            Files.writeString(chooser.selectedFile.toPath(), value, StandardCharsets.UTF_8)
        }
    }
