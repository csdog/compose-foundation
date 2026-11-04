package com.toutiaoyan.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.prefs.Preferences

internal class DesktopSettings(
    private val preferences: Preferences = Preferences.userRoot().node("com/toutiaoyan/compose/foundation/desktop"),
) {
    var showTray by mutableStateOf(preferences.getBoolean("showTray", true))
        private set
    var closeToTray by mutableStateOf(preferences.getBoolean("closeToTray", true))
        private set

    fun updateShowTray(value: Boolean) {
        preferences.putBoolean("showTray", value)
        showTray = value
    }

    fun updateCloseToTray(value: Boolean) {
        preferences.putBoolean("closeToTray", value)
        closeToTray = value
    }
}
