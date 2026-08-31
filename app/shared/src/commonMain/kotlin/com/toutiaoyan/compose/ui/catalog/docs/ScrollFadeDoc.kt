@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.scrollFade

val scrollFadeDoc =
    ComponentDoc(
        id = "scroll-fade",
        title = "Scroll Fade",
        description =
            "A scroll-position-aware fade at the edges of a scrollable container, hinting overflow " +
                "without a hard cut -- a modifier, not a standalone component.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.styles.shadcnScrollFade

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.height(160.dp).verticalScroll(scrollState).shadcnScrollFade(scrollState),
            ) {
                repeat(20) { Text { "Row ${'$'}it" } }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier.width(240.dp).height(160.dp)
                                .verticalScroll(scrollState).shadcnScrollFade(scrollState),
                        ) {
                            repeat(20) { index -> Text(modifier = Modifier.padding(vertical = 4.dp)) { "Row ${'$'}index" } }
                        }
                        """.trimIndent(),
                    preview = {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier =
                                Modifier
                                    .width(240.dp)
                                    .height(160.dp)
                                    .verticalScroll(scrollState)
                                    .scrollFade(scrollState),
                        ) {
                            repeat(20) { index ->
                                Text(modifier = Modifier.padding(vertical = 4.dp)) { "Row $index" }
                            }
                        }
                    },
                ),
            ),
    )
