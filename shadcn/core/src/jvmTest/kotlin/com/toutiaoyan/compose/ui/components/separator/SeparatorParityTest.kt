@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.separator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import kotlin.test.*

class SeparatorParityTest : ScreenshotTest() {
    private fun line() = composeRule.onNodeWithTag("line", useUnmergedTree = true)

    @Test fun baseUiDefaultExposesDescriptionAndOrientationWithoutAnInteractiveRole() {
        setThemedContent(stylePreset = StylePreset.Nova) { Separator(Modifier.width(120.dp).testTag("line")) }
        line().assertContentDescriptionEquals("Separator").assertHeightIsEqualTo(1.dp).assertWidthIsEqualTo(120.dp)
        assertEquals("Horizontal", line().fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        assertFalse(line().fetchSemanticsNode().config.contains(SemanticsProperties.Role))
        assertFalse(line().fetchSemanticsNode().config.contains(SemanticsActions.OnClick))
    }

    @Test fun decorativeExtensionOmitsDefaultAnnouncements() {
        setThemedContent { Separator(Modifier.width(120.dp).testTag("line"), decorative = true) }
        val config = line().fetchSemanticsNode().config
        assertFalse(config.contains(SemanticsProperties.ContentDescription))
        assertFalse(config.contains(SemanticsProperties.StateDescription))
    }

    @Test fun verticalMatchesABoundedParentHeight() {
        setThemedContent {
            Row(Modifier.height(20.dp)) {
                Separator(
                    Modifier.testTag("line"),
                    SeparatorOrientation.Vertical,
                )
            }
        }
        line().assertWidthIsEqualTo(1.dp).assertHeightIsEqualTo(20.dp)
    }

    @Test fun intrinsicRowStretchesSeparatorToItsTallestContent() {
        setThemedContent {
            Row(Modifier.height(IntrinsicSize.Min)) {
                Box(
                    Modifier.size(
                        60.dp,
                        40.dp,
                    ),
                )
                Separator(
                    Modifier.testTag("line"),
                    SeparatorOrientation.Vertical,
                )
                Box(
                    Modifier.size(
                        40.dp,
                        20.dp,
                    ),
                )
            }
        }
        line().assertHeightIsEqualTo(40.dp).assertWidthIsEqualTo(1.dp)
    }

    @Test fun explicitStyleOverridesBothSizeAndColor() {
        setThemedContent {
            Separator(
                Modifier.testTag("line"),
                style =
                    Style {
                        width(80.dp)
                        height(3.dp)
                        background(Color.Red)
                    },
            )
        }
        line().assertWidthIsEqualTo(80.dp).assertHeightIsEqualTo(3.dp)
        assertTrue(line().captureToImage().toPixelMap()[40, 1].red > .99f)
    }

    @Test fun orientationChangesUpdateLayoutAndAnnouncement() {
        var orientation by mutableStateOf(SeparatorOrientation.Horizontal)
        setThemedContent { Box(Modifier.size(120.dp, 80.dp)) { Separator(Modifier.testTag("line"), orientation) } }
        line().assertWidthIsEqualTo(120.dp).assertHeightIsEqualTo(1.dp)
        composeRule.runOnIdle { orientation = SeparatorOrientation.Vertical }
        line().assertWidthIsEqualTo(1.dp).assertHeightIsEqualTo(80.dp)
        assertEquals("Vertical", line().fetchSemanticsNode().config[SemanticsProperties.StateDescription])
    }

    @Test fun callerCanProvideLocalizedAnnouncements() {
        setThemedContent {
            Separator(
                Modifier.testTag("line").semantics {
                    contentDescription = "فاصل"
                    stateDescription = "أفقي"
                },
            )
        }
        line().assertContentDescriptionEquals("فاصل")
        assertEquals("أفقي", line().fetchSemanticsNode().config[SemanticsProperties.StateDescription])
    }

    @Test fun separatorAddsNoImplicitSurroundingSpacing() {
        setThemedContent {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier.size(
                        120.dp,
                        20.dp,
                    ).testTag("before"),
                )
                Separator(Modifier.testTag("line"))
                Box(
                    Modifier.size(
                        120.dp,
                        20.dp,
                    ).testTag("after"),
                )
            }
        }
        val before = composeRule.onNodeWithTag("before").getUnclippedBoundsInRoot()
        val divider = line().getUnclippedBoundsInRoot()
        val after = composeRule.onNodeWithTag("after").getUnclippedBoundsInRoot()
        assertEquals(8.dp, divider.top - before.bottom)
        assertEquals(8.dp, after.top - divider.bottom)
    }
}
