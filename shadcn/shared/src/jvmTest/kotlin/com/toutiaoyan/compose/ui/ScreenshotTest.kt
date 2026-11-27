@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasRequestFocusAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule

/**
 * Base class for Roborazzi screenshot tests. Runs Robolectric-less on the JVM/Desktop
 * target via `roborazzi-compose-desktop` -- there is no Android/Robolectric dependency
 * anywhere in this module, matching :library's plain jvm() target.
 *
 * Every capture goes through [snapshot] or [snapshotFocused] so every golden is themed
 * consistently and named `<name>_<light|dark>.png`, per the project convention that a
 * screenshot test with only a light-mode capture is incomplete.
 */
abstract class ScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // The Gradle plugin's `roborazzi { outputDir = ... }` targets the classic
    // Robolectric path; the Robolectric-less desktop capture used here just resolves
    // the filename passed to `captureRoboImage` against the module's working
    // directory, so the snapshots/ prefix is applied by hand here instead.
    private val snapshotDir = System.getenv("COMPONENT_PARITY_SCREENSHOT_DIR") ?: "src/jvmTest/snapshots"

    /** Renders [content] inside a themed, padded background and captures it as `<name>_<light|dark>.png`. */
    fun snapshot(
        name: String,
        darkTheme: Boolean = false,
        stylePreset: StylePreset = StylePreset.Vega,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            SnapshotSurface(darkTheme, stylePreset, content)
        }
        // Lets finite transitions (AnimatedVisibility, animateFloatAsState, ...) settle
        // before capture -- Compose UI Test explicitly excludes InfiniteTransition-driven
        // animations (Skeleton's pulse, Spinner's rotation) from idle-detection, so this
        // never blocks on those.
        composeRule.waitForIdle()
        captureCompositeRoot().captureRoboImage("$snapshotDir/$name${themeSuffix(darkTheme)}.png")
    }

    /**
     * Like [snapshot], but enters keyboard mode and requests real system focus on the node tagged [focusTag] before
     * capturing -- this exercises the same focus/interaction pipeline a real user would
     * drive, rather than special-casing a "forced focused" prop on the component under test.
     *
     * [focusTag] doesn't have to sit on the exact focusable node: some components (e.g.
     * `Input`, `Textarea`, `Slider`) apply `modifier` to an outer
     * layout wrapper around the real focus target (a `BasicTextField`, a drag handle), so
     * this searches [focusTag]'s own subtree for whichever descendant actually exposes the
     * `RequestFocus` semantics action.
     */
    fun snapshotFocused(
        name: String,
        focusTag: String,
        darkTheme: Boolean = false,
        stylePreset: StylePreset = StylePreset.Vega,
        content: @Composable () -> Unit,
    ) {
        lateinit var inputMode: InputModeManager
        composeRule.setContent {
            inputMode = LocalInputModeManager.current
            SnapshotSurface(darkTheme, stylePreset, content)
        }
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        val focusable = hasRequestFocusAction() and (hasTestTag(focusTag) or hasAnyAncestor(hasTestTag(focusTag)))
        composeRule.onNode(focusable, useUnmergedTree = true).requestFocus()
        composeRule.waitForIdle()
        captureCompositeRoot().captureRoboImage("$snapshotDir/$name${themeSuffix(darkTheme)}.png")
    }

    /**
     * Like [snapshot], but doesn't capture immediately -- for tests that need to drive
     * some interaction (e.g. `performMouseInput { moveTo(...) }` to trigger a hover
     * state) between composing and capturing. Call [captureNamed] once the interaction
     * has settled.
     */
    fun setThemedContent(
        darkTheme: Boolean = false,
        stylePreset: StylePreset = StylePreset.Vega,
        useMinimumTouchTarget: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            SnapshotSurface(darkTheme, stylePreset) {
                if (useMinimumTouchTarget) {
                    content()
                } else {
                    CompositionLocalProvider(
                        Theme.LocalTheme provides currentTheme.copy(minTouchTarget = null),
                        content = content,
                    )
                }
            }
        }
    }

    /** Captures the *already-composed* current tree -- for tests that drive multiple sequential captures off one `setContent` call. */
    fun captureNamed(
        name: String,
        darkTheme: Boolean,
    ) {
        captureCompositeRoot().captureRoboImage("$snapshotDir/$name${themeSuffix(darkTheme)}.png")
    }

    /**
     * A `Popup` (used by every overlay component -- see the `overlay` package) opens
     * its own semantics root on top of the base content's root, so `onRoot()` throws
     * ("expected exactly 1 root, found 2+") once one is showing. Verified empirically
     * (see the deleted `PopupCaptureSpikeTest` spike): the *last* root reported by
     * `onAllNodes(isRoot())` is always the one representing the fully composited
     * window -- base content plus every open overlay layered on top, exactly what a
     * real screenshot should show -- so always capturing the last root is correct
     * whether zero, one, or several Popups are open.
     *
     * Known cosmetic side effect (test-harness only, not a product bug): that
     * composite root reports the *real* platform window's full bounds (e.g.
     * 1024x768), not [SnapshotSurface]'s own wrap-content size -- so any capture
     * involving a Popup shows extra unthemed canvas margin around the actual UI,
     * invisible in light mode (the unfilled margin happens to be white already) but
     * visible as a white border in dark-mode popup goldens. The themed UI elements
     * themselves render with fully correct colors in every case -- confirmed by
     * inspecting the actual pixels, not assumed. Not worth fixing by making
     * [SnapshotSurface] `fillMaxSize()`: that would blow up every one of the
     * 100+ already-tight, already-reviewed non-popup goldens into mostly-blank
     * full-window images for a purely cosmetic gain on the handful of popup tests.
     */
    private fun captureCompositeRoot(): SemanticsNodeInteraction {
        val roots = composeRule.onAllNodes(isRoot())
        val lastIndex = roots.fetchSemanticsNodes().size - 1
        return roots[lastIndex]
    }

    private fun themeSuffix(darkTheme: Boolean) = if (darkTheme) "_dark" else "_light"
}

@Composable
private fun SnapshotSurface(
    darkTheme: Boolean,
    stylePreset: StylePreset,
    content: @Composable () -> Unit,
) {
    Theme(preset = stylePreset, isDark = darkTheme) {
        Box(modifier = Modifier.background(currentTheme.colors.background).padding(24.dp)) {
            content()
        }
    }
}
