package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import com.toutiaoyan.compose.ui.catalog.docs.emptyLinkSpaceModifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class EmptyLinkSpaceTest {
    @get:Rule val rule = createComposeRule()

    @Test fun captureFollowsInlineLinkFocusHostReplacementAndDisposal() {
        val first = RecordingHost()
        val second = RecordingHost()
        val link = FocusRequester()
        val other = FocusRequester()
        var host: DocumentScrollHost by mutableStateOf(first)
        var label by mutableStateOf("Link")
        var visible by mutableStateOf(true)
        rule.setContent {
            CompositionLocalProvider(LocalDocumentScrollHost provides host) {
                Theme {
                    Column {
                        if (visible) {
                            Box(Modifier.focusRequester(link).then(emptyLinkSpaceModifier())) {
                                Text {
                                    buildAnnotatedString {
                                        withLink(LinkAnnotation.Url("#")) { append(label) }
                                    }
                                }
                            }
                        }
                        Box(Modifier.focusRequester(other).focusable())
                    }
                }
            }
        }
        rule.runOnIdle { link.requestFocus() }
        rule.runOnIdle {
            assertEquals(1, first.active)
            label = "Updated link"
        }
        rule.runOnIdle {
            assertEquals(1, first.starts, "Recomposition keeps the existing capture")
            other.requestFocus()
        }
        rule.runOnIdle {
            assertEquals(0, first.active, "Another Compose control releases the capture")
            link.requestFocus()
        }
        rule.runOnIdle {
            assertEquals(1, first.active)
            host = second
        }
        rule.runOnIdle {
            assertEquals(0, first.active)
            assertEquals(1, second.active, "The new host inherits the current descendant focus")
            visible = false
        }
        rule.runOnIdle {
            assertEquals(0, second.active, "Removing the link releases its capture")
            assertEquals(first.starts, first.stops)
            assertEquals(second.starts, second.stops)
        }
    }

    private class RecordingHost : DocumentScrollHost {
        var starts = 0
        var stops = 0
        val active get() = starts - stops

        override fun captureNativeLinkSpace(): () -> Unit {
            starts++
            return { stops++ }
        }

        override suspend fun bind(
            state: ScrollState?,
            pixelsPerCssPixel: Float,
        ) = Unit
    }
}
