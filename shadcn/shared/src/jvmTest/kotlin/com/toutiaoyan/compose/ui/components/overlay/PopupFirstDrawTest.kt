package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class PopupFirstDrawTest {
    @get:Rule val rule = createComposeRule()

    @Test fun finalCollisionMetadataExistsBeforeColdAndReopenedContentDraws() {
        var open by mutableStateOf(false)
        var positioned: PopupPositionResult? = null
        val firstDraws = mutableListOf<PopupPositionResult?>()
        rule.setContent {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.size(16.dp)) {
                    PositionedAnchoredPopup(
                        open, {}, PopupPlacement.Bottom, 4.dp, PopupAlign.Start, 0.dp,
                        false, false, EnterTransition.None, ExitTransition.None,
                        onPositioned = { positioned = it },
                    ) {
                        val drawn = remember { booleanArrayOf(false) }
                        Box(
                            Modifier.size(100.dp, 60.dp).testTag("panel").drawWithContent {
                                if (!drawn[0]) {
                                    firstDraws += positioned
                                    drawn[0] = true
                                }
                                drawContent()
                            },
                        )
                    }
                }
            }
        }
        repeat(2) {
            rule.runOnIdle {
                positioned = null
                open = true
            }
            rule.onNodeWithTag("panel").captureToImage()
            rule.runOnIdle {
                val result = assertNotNull(firstDraws.last())
                assertEquals(PopupPlacement.Top, result.physicalSide)
                assertEquals(100, result.bounds.width)
                assertEquals(60, result.bounds.height)
                open = false
            }
            rule.onNodeWithTag("panel").assertDoesNotExist()
        }
        assertEquals(2, firstDraws.size)
    }
}
