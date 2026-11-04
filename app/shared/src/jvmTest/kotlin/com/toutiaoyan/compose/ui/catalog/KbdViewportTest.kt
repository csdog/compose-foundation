package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.kbdDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class KbdViewportTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun rtlBodyHasOne288PixelFrameAndLanguagePickerMirrorsKeys() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                OverlayHost {
                    Box(Modifier.width((if (width < 640) 325 else 638).dp)) {
                        kbdDoc.examples.single { it.title == "RTL" }.preview()
                    }
                }
            }
        }
        rule.onNodeWithTag("kbd-rtl-body").assertHeightIsEqualTo(288.dp)

        fun left(key: String) = rule.onNodeWithText(key).fetchSemanticsNode().boundsInRoot.left
        for (language in listOf("English", "Hebrew (עברית)", "Arabic (العربية)")) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onNodeWithText(language).performClick()
            if (language == "English") {
                assertTrue(left("⌘") < left("⌃"))
            } else {
                assertTrue(left("⌘") > left("⌃"))
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}-{1}")
        fun parameters() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }
}
