package com.toutiaoyan.compose.ui

import org.junit.Test
import java.util.UUID
import java.util.prefs.Preferences
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopSettingsTest {
    @Test
    fun trayPreferencesSurviveRestartAndKeepTheTwoOptionsIndependent() {
        val store = Preferences.userRoot().node("compose-foundation-test/" + UUID.randomUUID())
        try {
            val settings = DesktopSettings(store)
            assertTrue(settings.showTray)
            assertTrue(settings.closeToTray)
            settings.updateShowTray(false)
            val restarted = DesktopSettings(store)
            assertFalse(restarted.showTray)
            assertTrue(restarted.closeToTray)
            restarted.updateCloseToTray(false)
            restarted.updateShowTray(true)
            val last = DesktopSettings(store)
            assertTrue(last.showTray)
            assertFalse(last.closeToTray)
        } finally {
            store.removeNode()
        }
    }
}
