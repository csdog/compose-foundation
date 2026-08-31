@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.CodeXml
import com.composables.icons.lucide.Eye
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * A "Preview | Code" toggle used for every live example in the catalog, mirroring
 * the shadcn/ui docs pattern: a single icon button in the top-right corner swaps
 * between the live preview and its source, rather than two separate labeled controls.
 *
 * [title], when given, shares the same header row as the toggle button instead of being
 * rendered as a separate line above it -- one header, not two stacked rows.
 */
@Composable
fun PreviewCodeSection(
    code: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    preview: @Composable () -> Unit,
) {
    var showCode by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (title != null) {
                Text(variant = TextVariant.TitleSmall) { title }
            } else {
                Spacer(Modifier)
            }
            Button(
                onClick = { showCode = !showCode },
                variant = if (showCode) ButtonVariant.Secondary else ButtonVariant.Ghost,
                size = ButtonSize.Icon,
            ) {
                DocIcon(if (showCode) Lucide.Eye else Lucide.CodeXml)
            }
        }
        Spacer(Modifier.height(twSpacing.sm))
        if (showCode) {
            CodeBlock(code = code, modifier = Modifier.fillMaxWidth())
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, currentTheme.colors.border, RoundedCornerShape(twRadius.md))
                        .background(currentTheme.colors.background, RoundedCornerShape(twRadius.md))
                        .padding(twSpacing.xl2),
                // Not Alignment.Center: centering horizontally re-centers the whole preview
                // block whenever its intrinsic width changes (e.g. a Collapsible expanding to
                // reveal wider text, or switching Tabs to a longer content string) -- since
                // that width change is driven by content BELOW/AROUND a Start-anchored trigger,
                // the trigger visibly shifts sideways as the block recenters even though its
                // own position within its own layout never changed. TopStart keeps every demo
                // anchored to a fixed point regardless of how its content resizes.
                contentAlignment = Alignment.TopStart,
            ) {
                preview()
            }
        }
    }
}
