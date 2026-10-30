package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.channels.Channel

internal data class DocSection(
    val id: String,
    val title: String,
    val nested: Boolean = false,
    val fragment: String =
        docFragment(
            title,
        ),
)

/** Layout coordinates are read for scrolling, never written back into composition state. */
internal class DocAnchors {
    private val updates = Channel<Unit>(Channel.CONFLATED)
    var content: LayoutCoordinates? = null
        set(value) {
            field = value
            updates.trySend(Unit)
        }
    private val headings = mutableMapOf<String, LayoutCoordinates>()

    fun register(
        id: String,
        coordinates: LayoutCoordinates,
    ) {
        headings[id] = coordinates
        updates.trySend(Unit)
    }

    suspend fun awaitOffsetOf(id: String): Int {
        while (true) {
            offsetOf(id)?.let { return it }
            updates.receive()
        }
    }

    fun offsetOf(id: String): Int? {
        val parent = content?.takeIf { it.isAttached } ?: return null
        val heading = headings[id]?.takeIf { it.isAttached } ?: return null
        return parent.localPositionOf(heading, Offset.Zero).y.roundToInt().coerceAtLeast(0)
    }
}

internal fun Modifier.docAnchor(
    anchors: DocAnchors,
    id: String,
): Modifier = onGloballyPositioned { anchors.register(id, it) }.testTag("heading-$id")

@Composable
internal fun DocTableOfContents(
    sections: List<DocSection>,
    activeSection: String,
    onSectionClick: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier.width(
                224.dp,
            ).fillMaxHeight().padding(top = 48.dp, end = 24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(variant = TextVariant.BodyMedium, textStyle = currentTheme.typography.labelLarge) { "On This Page" }
        sections.forEach { section ->
            Button(
                onClick = { onSectionClick(section.id) },
                modifier = Modifier.padding(start = if (section.nested) 12.dp else 0.dp).testTag("toc-${section.id}"),
                variant = ButtonVariant.Ghost,
                size = ButtonSize.Sm,
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textStyle =
                        currentTheme.typography.bodyMedium.copy(
                            color =
                                if (section.id == activeSection) {
                                    currentTheme.colors.foreground
                                } else {
                                    currentTheme.colors.mutedForeground
                                },
                            fontWeight = if (section.id == activeSection) FontWeight.Medium else FontWeight.Normal,
                        ),
                ) { section.title }
            }
        }
    }
}
