package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.messageScrollerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class MessageScrollerExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(600.dp).height(800.dp), contentAlignment = Alignment.TopCenter) {
                        messageScrollerDoc.examples.single { it.title == "Provider controls (Kotlin)" }.preview()
                    }
                }
            }
        }
    }

    @Test fun usageExplainsCanonicalCompositionAndExposesRealStateApi() {
        assertEquals(7, messageScrollerDoc.apiSections.size)
        assertTrue(messageScrollerDoc.usageCode.contains("messageId"))
        assertTrue(messageScrollerDoc.usageCode.contains("MessageScrollerButton()"))
        assertFalse(messageScrollerDoc.usageCode.contains("DocIcon"))
        assertFalse(messageScrollerDoc.usageCode.contains("components.*"))
    }

    @Test fun jumpAndLoadingEarlierKeepTheSameReadingPosition() {
        show()
        rule.onNodeWithText("Jump to message 6").performClick()
        val top = rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top
        rule.onNodeWithText("Load earlier").performClick()
        rule.onNodeWithText("Load earlier").assertIsNotEnabled()
        assertEquals(top, rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top, 0.1f)
    }

    @Test fun releasedReaderStaysPutAndExplicitButtonResumesFollowing() {
        show()
        rule.onNodeWithText("Jump to message 6").performClick()
        val top = rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top
        rule.onNodeWithText("Append reply").performClick()
        assertEquals(top, rule.onNodeWithText("Message 6").fetchSemanticsNode().boundsInRoot.top, 0.1f)
        rule.onNodeWithContentDescription("Scroll to end").performClick()
        rule.onNodeWithText("Message 11").assertIsDisplayed()
        rule.onNodeWithText("Append reply").performClick()
        rule.onNodeWithText("Message 12").assertIsDisplayed()
        rule.onNodeWithContentDescription("Scroll to end").assertDoesNotExist()
    }
}
