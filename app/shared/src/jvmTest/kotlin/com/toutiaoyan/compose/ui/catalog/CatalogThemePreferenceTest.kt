package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.App
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class CatalogThemePreferenceTest {
    @get:Rule val rule = createComposeRule()

    @Test fun explicitChoicesArePublishedAndRestoredWhenTheApplicationIsRecreated() {
        var saved: Boolean? = true
        val changes = mutableListOf<Boolean>()
        val generation = mutableIntStateOf(0)
        rule.setContent {
            Box(Modifier.requiredSize(1200.dp, 800.dp).testTag("app")) {
                key(generation.intValue) {
                    App(
                        initialDarkModeOverride = saved,
                        onDarkModeOverrideChange = {
                            saved = it
                            changes += it
                        },
                    )
                }
            }
        }

        fun assertTheme(dark: Boolean) {
            val pixel = rule.onNodeWithTag("app").captureToImage().toPixelMap()[2, 2]
            val expected = if (dark) 10f / 255 else 1f
            assertEquals(expected, pixel.red, .01f)
            assertEquals(expected, pixel.green, .01f)
            assertEquals(expected, pixel.blue, .01f)
        }
        assertTheme(true)
        assertTrue(changes.isEmpty(), "Restoring a preference must not publish another manual change")
        rule.onNodeWithContentDescription("Toggle theme").performClick()
        assertTheme(false)
        assertEquals(false, saved)
        rule.runOnIdle { generation.intValue++ }
        assertTheme(false)
        rule.onNodeWithContentDescription("Toggle theme").performClick()
        assertTheme(true)
        assertEquals(true, saved)
        rule.runOnIdle { generation.intValue++ }
        assertTheme(true)
        assertEquals(listOf(false, true), changes)
    }
}
