package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toPixelMap
import kotlin.math.abs
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.emptyDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class EmptyExamplesTest {
    @get:Rule val rule = createComposeRule()
    private var opened = ""

    private fun show(title: String) {
        rule.setContent {
            CompositionLocalProvider(
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            opened = uri
                        }
                    },
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    OverlayHost {
                        Box(Modifier.width(560.dp).height(600.dp)) {
                            emptyDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
    }

    @Test fun allSevenOfficialRecipesAppearInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Outline",
                "Background",
                "Avatar",
                "Avatar Group",
                "InputGroup",
                "RTL",
            ),
            emptyDoc.examples.map { it.title },
        )
        assertTrue(emptyDoc.examples.single { it.title == "InputGroup" }.code.contains("withLink(LinkAnnotation.Url"))
        assertFalse(emptyDoc.examples.single { it.title == "RTL" }.code.contains("RtlPreview("))
    }

    @Test fun defaultLearnMoreFollowsTheLink() {
        show("Default")
        rule.onNodeWithText("Learn More").performClick()
        assertEquals("#", opened)
    }

    @Test fun avatarUsesTheBundledFortyEightPixelPhoto() {
        show("Avatar")
        rule.onNodeWithContentDescription(
            "@shadcn",
        ).assertIsDisplayed().assertWidthIsEqualTo(48.dp).assertHeightIsEqualTo(48.dp)
        val pixels = rule.onNodeWithContentDescription("@shadcn").captureToImage().toPixelMap()
        for (y in 8 until 40 step 8) {
            for (x in 8 until 40 step 8) {
                val color = pixels[x, y]
                assertTrue(abs(color.red - color.green) < 0.015f && abs(color.green - color.blue) < 0.015f)
            }
        }
        rule.onNodeWithText("User Offline").assertIsDisplayed()
        rule.onNodeWithText("Leave Message").assertIsEnabled()
    }

    @Test fun avatarGroupOverlapsThreeRealPhotosByEightPixels() {
        show("Avatar Group")
        val photos =
            listOf("@shadcn", "@maxleiter", "@evilrabbit").map {
                rule.onNodeWithContentDescription(
                    it,
                ).assertIsDisplayed().assertWidthIsEqualTo(48.dp).fetchSemanticsNode().boundsInRoot
            }
        assertEquals(40f, photos[1].left - photos[0].left, 0.1f)
        assertEquals(40f, photos[2].left - photos[1].left, 0.1f)
        rule.onNodeWithText("Invite Members").assertIsEnabled()
    }

    @Test fun searchInputKeepsEditsAndTheSupportTextIsAnActualLink() {
        show("InputGroup")
        rule.onNodeWithText("Try searching for pages...").performTextInput("Buttons")
        rule.onNodeWithText("Buttons").assertIsDisplayed().performTextClearance()
        rule.onNodeWithText("Try searching for pages...").assertIsDisplayed()
        val text = "Need help? Contact support"
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        val layout = results.single()
        val link = layout.layoutInput.text.getLinkAnnotations(0, text.length).single()
        assertEquals("#", (link.item as LinkAnnotation.Url).url)
        val bounds = layout.getBoundingBox(link.start + 2)
        rule.onNodeWithText(text).performTouchInput { click(Offset(bounds.center.x, bounds.center.y)) }
        assertEquals("#", opened)
    }

    @Test fun rtlPickerChangesTheActualCopyAndButtonOrder() {
        show("RTL")
        rule.onNodeWithText("لا توجد مشاريع بعد").assertIsDisplayed()

        fun left(text: String) = rule.onNodeWithText(text).fetchSemanticsNode().boundsInRoot.left
        assertTrue(left("إنشاء مشروع") > left("استيراد مشروع"))
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("No Projects Yet").assertIsDisplayed()
        assertTrue(left("Create Project") < left("Import Project"))
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("עברית").performClick()
        rule.onNodeWithText("אין פרויקטים עדיין").assertIsDisplayed()
        assertTrue(left("צור פרויקט") > left("ייבא פרויקט"))
    }
}
