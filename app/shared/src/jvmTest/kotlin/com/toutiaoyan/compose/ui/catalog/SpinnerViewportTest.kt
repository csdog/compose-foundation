package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.spinnerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class SpinnerViewportTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule =
        createComposeRule(
            effectContext =
                object : MotionDurationScale {
                    override val scaleFactor = 0f
                },
        )

    private fun show(name: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                OverlayHost {
                    Box(Modifier.width((if (width < 640) 325 else 638).dp)) {
                        Box(Modifier.padding(if (name == "RTL") 0.dp else 40.dp)) {
                            spinnerDoc.examples.single { it.title == name }.RenderPreview()
                        }
                    }
                }
            }
        }
    }

    @Test fun badgeKeepsThreeTwelvePixelSpinnersOnOneLine() {
        show("Badge")
        val nodes = rule.onAllNodesWithContentDescription("Loading", useUnmergedTree = true).fetchSemanticsNodes()
        assertEquals(3, nodes.size)
        assertEquals(listOf(12f, 12f, 12f), nodes.map { it.boundsInRoot.width })
        assertEquals(1, nodes.map { it.boundsInRoot.top }.distinct().size)
        listOf("Syncing", "Updating", "Processing").forEach { rule.onNodeWithText(it).assertExists() }
    }

    @Test fun languageChangesMirrorMediaAndPreserveLoading() {
        show("RTL")
        val loading = rule.onNodeWithContentDescription("Loading", useUnmergedTree = true)
        for ((language, title) in listOf(
            "English" to "Processing payment...",
            "Hebrew (עברית)" to "מעבד תשלום...",
            "Arabic (العربية)" to "جاري معالجة الدفع...",
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onNodeWithText(language).performClick()
            rule.onNodeWithText(title).assertIsDisplayed()
            val iconBounds = loading.fetchSemanticsNode().boundsInRoot
            val titleBounds = rule.onNodeWithText(title).fetchSemanticsNode().boundsInRoot
            if (language == "English") {
                assertTrue(iconBounds.right <= titleBounds.left)
            } else {
                assertTrue(iconBounds.left >= titleBounds.right)
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
