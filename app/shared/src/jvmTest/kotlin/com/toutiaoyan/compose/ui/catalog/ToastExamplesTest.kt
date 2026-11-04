@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.toastDoc
import com.toutiaoyan.compose.ui.components.toast.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class ToastExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private lateinit var state: ToastState

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(
                    1440,
                    false,
                ),
                arrayOf<Any>(
                    1440,
                    true,
                ),
                arrayOf<Any>(
                    390,
                    false,
                ),
                arrayOf<Any>(
                    390,
                    true,
                ),
            )
    }

    private fun advance(millis: Long = 320) {
        rule.waitForIdle()
        rule.mainClock.advanceTimeBy(millis)
        rule.waitForIdle()
    }

    private fun show(title: String) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewport, 800)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    CatalogToastHost(
                        Modifier.width(minOf(viewport, 640).dp).height(640.dp)
                            .background(currentTheme.colors.background).testTag("toast-scene"),
                    ) {
                        val manager = checkNotNull(LocalCatalogToastState.current)
                        SideEffect { state = manager }
                        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            toastDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
        advance()
    }

    private fun toast() = rule.onNodeWithContentDescription("Notifications")

    private fun capture(
        name: String,
        scene: Boolean = false,
    ) {
        val file = File("build/reports/toast-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        val node = if (scene) rule.onNodeWithTag("toast-scene") else toast()
        ImageIO.write(node.captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultUsesSourceMessageAndUndoClosesItsOwnId() {
        show("Default")
        rule.onNodeWithText("Show Toast").performClick()
        advance()
        rule.onNodeWithText("Event created").assertIsDisplayed()
        rule.onNodeWithText("Sunday, December 3 at 9:00 AM").assertIsDisplayed()
        rule.onNodeWithContentDescription("Close toast").assertIsDisplayed()
        capture("default")
        rule.onNodeWithText("Undo").performClick()
        advance()
        assertTrue(state.toasts.isEmpty())
    }

    @Test fun allFiveSourceTypesRenderAndErrorUsesUrgentAnnouncement() {
        show("Types")
        val entries =
            listOf(
                Triple("Default", "Event has been created.", ToastVariant.Default),
                Triple("Success", "Event has been created.", ToastVariant.Success),
                Triple("Info", "Arrive 10 minutes before the event.", ToastVariant.Info),
                Triple("Warning", "The event cannot start before 8:00 AM.", ToastVariant.Warning),
                Triple("Error", "The event could not be created.", ToastVariant.Error),
            )
        entries.forEach { (label, description, variant) ->
            rule.runOnIdle { state.dismiss() }
            advance()
            rule.onNodeWithText(label).performClick()
            advance()
            assertEquals(variant, state.toasts.single().variant)
            assertEquals("", state.toasts.single().title)
            rule.onNodeWithText(description).assertIsDisplayed()
            if (variant == ToastVariant.Error) {
                rule.onNode(
                    SemanticsMatcher.expectValue(
                        SemanticsProperties.LiveRegion,
                        LiveRegionMode.Assertive,
                    ),
                ).assertExists()
            }
            capture("type-${label.lowercase()}")
        }
    }

    @Test fun promiseChangesOneLoadingNotificationToSuccess() {
        show("Promise")
        rule.onNodeWithText("Create Event").performClick()
        advance(32)
        assertEquals(ToastVariant.Loading, state.toasts.single().variant)
        val id = state.toasts.single().id
        rule.onNodeWithText("Creating event…").assertIsDisplayed()
        capture("promise-loading")
        advance(2100)
        assertEquals(id, state.toasts.single().id)
        assertEquals(ToastVariant.Success, state.toasts.single().variant)
        rule.onNodeWithText("Event created.").assertIsDisplayed()
        capture("promise-success")
    }

    @Test fun defaultCloseButtonRemovesTheNotification() {
        show("Default")
        rule.onNodeWithText("Show Toast").performClick()
        advance()
        rule.onNodeWithContentDescription("Close toast").performClick()
        advance()
        assertTrue(state.toasts.isEmpty())
        rule.onNodeWithText("Event created").assertDoesNotExist()
    }

    @Test fun windowHostUsesSixteenPixelInsetsAndResponsiveWidth() {
        show("Default")
        rule.onNodeWithText("Show Toast").performClick()
        advance()
        toast().assertWidthIsEqualTo((if (viewport == 390) 358 else 384).dp)
        val scene = rule.onNodeWithTag("toast-scene").fetchSemanticsNode().boundsInRoot
        val actual = toast().fetchSemanticsNode().boundsInRoot
        assertEquals(scene.right - 16, actual.right, .1f)
        assertEquals(scene.bottom - 16, actual.bottom, .1f)
        capture("viewport", scene = true)
    }

    @Test fun updatingAMessageUpdatesItsActionAndPriorityWithoutChangingId() {
        show("Default")
        rule.onNodeWithText("Show Toast").performClick()
        advance()
        val id = state.toasts.single().id
        var actions = 0
        rule.runOnIdle {
            state.update(id) {
                it.copy(
                    title = "Updated",
                    description = "Updated details",
                    durationMillis = 0,
                    actionLabel = "Run",
                    onAction = { actions++ },
                    priority = ToastPriority.High,
                )
            }
        }
        advance()
        assertEquals(id, state.toasts.single().id)
        rule.onNodeWithText("Updated").assertIsDisplayed()
        rule.onNodeWithText("Run").performClick()
        assertEquals(1, actions)
        assertEquals(1, state.toasts.size)
        capture("updated")
    }
}
