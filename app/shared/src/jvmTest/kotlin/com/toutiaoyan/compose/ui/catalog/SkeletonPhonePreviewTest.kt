@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.skeletonDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Real preview chrome, including page inset, border and each recipe's padding. */
@RunWith(Parameterized::class)
class SkeletonPhonePreviewTest(
    private val clientWidth: Int,
    private val dark: Boolean,
    private val title: String,
) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "client={0},dark={1},recipe={2}")
        fun modes() =
            listOf(390, 375).flatMap { width ->
                listOf(false, true).flatMap { dark ->
                    listOf("Default", "RTL").map { title -> arrayOf<Any>(width, dark, title) }
                }
            }
    }

    @Test fun phonePreviewPreservesSourceProfileGeometryInsideRealChrome() {
        val example = skeletonDoc.examples.single { it.title == title }
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(390, 844)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    // 375 is the 390px browser's client area with its 15px scrollbar.
                    // ComponentDetailScreen uses 16dp horizontal page padding on phones.
                    Box(Modifier.size(clientWidth.dp, 844.dp).padding(horizontal = 16.dp)) {
                        PreviewCodeSection(
                            code = example.code,
                            modifier = Modifier.fillMaxWidth().testTag("phone-preview-frame"),
                            minPreviewHeight = example.minPreviewHeight,
                            mobileMinPreviewHeight = example.mobileMinPreviewHeight,
                            previewPadding = example.previewPadding,
                            preview = example.preview,
                        )
                    }
                }
            }
        }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()

        fun bounds(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()

        val frame = bounds("phone-preview-frame")
        val avatar = bounds("skeleton-avatar")
        val first = bounds("skeleton-line-0")
        val second = bounds("skeleton-line-1")
        val profile = bounds("skeleton-example")
        val report = File("build/reports/skeleton-phone-preview/$clientWidth-$dark-$title.txt")
        report.parentFile.mkdirs()
        val clipped =
            listOf(
                "phone-preview-frame",
                "skeleton-example",
                "skeleton-avatar",
                "skeleton-line-0",
                "skeleton-line-1",
            ).associateWith {
                rule.onNodeWithTag(it, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            }
        val availableProfileWidth = (frame.right - frame.left) - 2.dp - if (title == "RTL") 48.dp else 80.dp
        report.writeText(
            "availableProfileWidth=$availableProfileWidth\nframe=$frame\nprofile=$profile\n" +
                "avatar=$avatar\nfirst=$first\nsecond=$second\nclipped=$clipped\n",
        )
        assertEquals((clientWidth - 32).dp, frame.right - frame.left, "Real page inset must constrain the preview")
        assertEquals(48.dp, avatar.right - avatar.left)
        assertEquals(48.dp, avatar.bottom - avatar.top)
        assertEquals(
            250.dp,
            first.right - first.left,
            "Source first line must survive actual phone preview constraints",
        )
        assertEquals(
            200.dp,
            second.right - second.left,
            "Source second line must survive actual phone preview constraints",
        )
        assertEquals(16.dp, first.bottom - first.top)
        assertEquals(16.dp, second.bottom - second.top)
        assertEquals(8.dp, second.top - first.bottom)
        assertEquals(314.dp, profile.right - profile.left)
        // Centering an even-width profile in an odd-width client rounds to a pixel.
        assertEquals(
            (frame.left + (frame.right - frame.left) / 2).value,
            (profile.left + (profile.right - profile.left) / 2).value,
            .5f,
        )
        // Overflow may enter the preview padding, but all three shapes still fit
        // inside the actual preview border at both 390px client widths.
        listOf("skeleton-avatar", "skeleton-line-0", "skeleton-line-1").forEach { tag ->
            val actual = bounds(tag)
            assertTrue(actual.left >= frame.left + 1.dp)
            assertTrue(actual.right <= frame.right - 1.dp)
            assertEquals((actual.right - actual.left).value, clipped.getValue(tag).width)
        }
        if (title == "RTL") {
            assertEquals(16.dp, avatar.left - first.right)
            assertEquals(first.right, second.right)
        } else {
            assertEquals(16.dp, first.left - avatar.right)
            assertEquals(first.left, second.left)
        }
    }
}
