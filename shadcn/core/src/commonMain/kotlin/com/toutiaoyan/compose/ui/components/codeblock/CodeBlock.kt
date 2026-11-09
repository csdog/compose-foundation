@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.codeblock

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.composables.interactioncapabilities.currentInteractionCapabilities
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import androidx.compose.ui.Alignment
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme

sealed interface CodeBlockVariant {
    data object Default : CodeBlockVariant

    data object Destructive : CodeBlockVariant
}

sealed interface CodeBlockSize {
    data object Default : CodeBlockSize

    data object Sm : CodeBlockSize
}

internal enum class CodeSlot { Header, Footer, Content, Actions }

private data class CodeSlotData(val slot: CodeSlot) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = slot
}

internal fun Modifier.codeSlot(slot: CodeSlot): Modifier = then(CodeSlotData(slot))

internal class CodeBlockContext(
    val variant: CodeBlockVariant,
    val size: CodeBlockSize,
    val showActions: Boolean,
    val onContentFocus: (Boolean) -> Unit,
) {
    val inset get() = if (size == CodeBlockSize.Sm) 10.dp else 12.dp
}

open class CodeBlockPartsScope internal constructor(
    internal val context: CodeBlockContext,
    internal val inHeader: Boolean =
        false,
)

class CodeBlockScope internal constructor(context: CodeBlockContext) : CodeBlockPartsScope(context)

class CodeBlockHeaderScope internal constructor(context: CodeBlockContext) : CodeBlockPartsScope(
    context,
    inHeader = true,
)

/** Base UI CodeBlock composition. Slots inherit explicit size/variant scope, not global state. */
@Composable
fun CodeBlock(
    modifier: Modifier = Modifier,
    variant: CodeBlockVariant = CodeBlockVariant.Default,
    size: CodeBlockSize = CodeBlockSize.Default,
    style: Style = Style,
    content: @Composable CodeBlockScope.() -> Unit,
) {
    val theme = currentTheme
    val radius = twRadius.lg
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    var focusWithin by remember { mutableStateOf(false) }
    var contentFocused by remember { mutableStateOf(false) }
    val context =
        CodeBlockContext(variant, size, hovered || focusWithin || !currentInteractionCapabilities().hasPointer, {
            contentFocused = it
        })
    val foreground = if (variant == CodeBlockVariant.Destructive) theme.colors.destructive else theme.colors.foreground
    val state = remember { MutableStyleState(source) }
    val chrome =
        Style {
            shape(RoundedCornerShape(radius))
            border(
                if (variant == CodeBlockVariant.Destructive) {
                    theme.colors.destructive.copy(
                        alpha = if (theme.isDark) 0.4f else 0.3f,
                    )
                } else {
                    theme.colors.input
                },
            )
            // Native dropShadow also paints under translucent content. Cover its interior so
            // keyboard focus changes only the outer ring, like CSS box-shadow.
            background(
                if (variant == CodeBlockVariant.Default && theme.isDark) {
                    theme.colors.input.copy(
                        alpha = theme.colors.input.alpha * 0.3f,
                    ).compositeOver(theme.colors.background)
                } else {
                    theme.colors.background
                },
            )
            contentPadding(0.dp)
            if (contentFocused) {
                borderColor(theme.colors.ring)
                dropShadow(theme.focusRingShadow())
            }
        }
    CompositionLocalProvider(
        LocalContentColor provides foreground,
        LocalDefaultTextStyle provides
            theme.typography.bodyMedium.copy(
                lineHeight = if (size == CodeBlockSize.Sm) 16.sp else 20.sp,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                fontSize =
                    if (size == CodeBlockSize.Sm) {
                        12.sp
                    } else {
                        14.sp
                    },
            ),
    ) {
        Layout(
            modifier =
                modifier.fillMaxWidth().onFocusChanged { focusWithin = it.hasFocus }.focusGroup()
                    .hoverable(source).styleable(state, chrome, style),
            content = { CodeBlockScope(context).content() },
        ) { measurables, constraints ->
            val width =
                constraints.constrainWidth(
                    if (constraints.hasBoundedWidth) constraints.maxWidth else 320.dp.roundToPx(),
                )
            val inset = context.inset.roundToPx()
            val hasHeader = measurables.any { it.parentData == CodeSlot.Header }
            val hasFooter = measurables.any { it.parentData == CodeSlot.Footer }
            val flows =
                measurables.filter { it.parentData != CodeSlot.Actions }.map { measurable ->
                    val role = measurable.parentData
                    val horizontal = if (role == CodeSlot.Header || role == CodeSlot.Footer) inset else 0
                    val top =
                        when (role) {
                            CodeSlot.Header -> inset
                            CodeSlot.Content ->
                                if (hasHeader) {
                                    if (size == CodeBlockSize.Sm) {
                                        6.dp.roundToPx()
                                    } else {
                                        8.dp.roundToPx()
                                    }
                                } else {
                                    inset
                                }
                            else -> 0
                        }
                    val bottom =
                        when (role) {
                            CodeSlot.Footer -> inset
                            CodeSlot.Content -> if (hasFooter) 8.dp.roundToPx() else inset
                            else -> 0
                        }
                    val child =
                        measurable.measure(
                            androidx.compose.ui.unit.Constraints(maxWidth = (width - 2 * horizontal).coerceAtLeast(0)),
                        )
                    Triple(child, horizontal, top to bottom)
                }
            val actions =
                measurables.filter { it.parentData == CodeSlot.Actions }.map {
                    it.measure(androidx.compose.ui.unit.Constraints(maxWidth = width))
                }
            val height = constraints.constrainHeight(flows.sumOf { it.first.height + it.third.first + it.third.second })
            layout(width, height) {
                var y = 0
                flows.forEach { (child, x, padding) ->
                    y += padding.first
                    child.placeRelative(x, y)
                    y += child.height + padding.second
                }
                val offset = if (size == CodeBlockSize.Sm) 6.dp.roundToPx() else 8.dp.roundToPx()
                actions.forEach { it.placeRelative((width - it.width - offset).coerceAtLeast(0), offset) }
            }
        }
    }
}

@Composable
fun CodeBlockPartsScope.CodeBlockHeader(
    modifier: Modifier = Modifier,
    content: @Composable CodeBlockHeaderScope.() -> Unit,
) {
    Layout(modifier = modifier.fillMaxWidth().codeSlot(CodeSlot.Header), content = {
        CodeBlockHeaderScope(context).content()
    }) { children, constraints ->
        val actions =
            children.filter { it.parentData == CodeSlot.Actions }.map {
                it.measure(
                    constraints.copy(minWidth = 0),
                )
            }
        val gap = 8.dp.roundToPx()
        // Nova's -my-1/-me-1.5 reduce the action margin box, not the button itself.
        val endOverhang = if (actions.isNotEmpty()) 6.dp.roundToPx() else 0
        val reserved =
            (actions.sumOf { it.width } - endOverhang).coerceAtLeast(0) +
                if (actions.isNotEmpty()) gap else 0
        var available = (constraints.maxWidth - reserved).coerceAtLeast(0)
        val leading =
            children.filter { it.parentData != CodeSlot.Actions }.map {
                it.measure(constraints.copy(minWidth = 0, maxWidth = available)).also {
                        p ->
                    available = (available - p.width - gap).coerceAtLeast(0)
                }
            }
        val height =
            maxOf(
                leading.maxOfOrNull { it.height } ?: 0,
                ((actions.maxOfOrNull { it.height } ?: 0) - 8.dp.roundToPx()).coerceAtLeast(0),
            )
        layout(constraints.maxWidth, height) {
            var x = 0
            leading.forEach {
                it.placeRelative(x, (height - it.height) / 2)
                x += it.width + gap
            }
            x = constraints.maxWidth - actions.sumOf { it.width } + endOverhang
            actions.forEach {
                it.placeRelative(x, (height - it.height) / 2)
                x += it.width
            }
        }
    }
}

@Composable
fun CodeBlockPartsScope.CodeBlockTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    CodeBlockTitle(modifier) { Text(maxLines = 1, overflow = TextOverflow.Ellipsis) { text } }
}

@Composable
fun CodeBlockPartsScope.CodeBlockTitle(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val theme = currentTheme
    CompositionLocalProvider(
        LocalContentColor provides
            if (context.variant == CodeBlockVariant.Destructive) {
                theme.colors.destructive
            } else {
                theme.colors.foreground
            },
        LocalDefaultTextStyle provides
            theme.typography.bodyMedium.copy(
                lineHeight = if (context.size == CodeBlockSize.Sm) 16.sp else 20.sp,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                fontFamily =
                    theme.fontFamily,
                fontSize =
                    if (context.size == CodeBlockSize.Sm) {
                        12.sp
                    } else {
                        14.sp
                    },
                fontWeight =
                    FontWeight.Medium,
            ),
        LocalInlineIconSize provides 14.dp,
    ) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
fun CodeBlockPartsScope.CodeBlockActions(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    CompositionLocalProvider(LocalInlineIconSize provides 12.dp) {
        Row(
            modifier = modifier.codeSlot(CodeSlot.Actions).alpha(if (inHeader || context.showActions) 1f else 0f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

@Composable
fun CodeBlockPartsScope.CodeBlockFooter(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides currentTheme.colors.mutedForeground) {
        Row(
            modifier = modifier.fillMaxWidth().codeSlot(CodeSlot.Footer),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}
