package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import com.toutiaoyan.compose.ui.components.overlay.overlayTypography

internal val LocalContextMenuNova = staticCompositionLocalOf { false }

// Menubar selection marks occupy the leading slot; DropdownMenu marks are trailing.
internal val LocalContextMenuLeadingSelectionIndicator = staticCompositionLocalOf { false }

private const val MENU_TYPEAHEAD_RESET_MILLIS = 500L
private const val MENU_ROW_FONT_SIZE_SP = 14
private const val MENU_ROW_LINE_HEIGHT_SP = 20
private const val MENU_LABEL_FONT_SIZE_SP = 12
private const val MENU_LABEL_LINE_HEIGHT_SP = 16

internal class ContextMenuNavigation {
    private val registeredItems = mutableStateListOf<FocusRequester>()
    private val itemPositions = mutableMapOf<FocusRequester, Float>()
    val items: List<FocusRequester>
        get() =
            if (registeredItems.all { it in itemPositions }) {
                registeredItems.sortedBy(itemPositions::getValue)
            } else {
                registeredItems
            }

    var focused: FocusRequester? = null
    val labels = mutableMapOf<FocusRequester, String>()
    var search = ""
    var lastType = kotlin.time.TimeSource.Monotonic.markNow()

    fun setEnabled(
        focus: FocusRequester,
        enabled: Boolean,
    ) {
        if (enabled) {
            if (focus !in registeredItems) registeredItems.add(focus)
        } else {
            registeredItems.remove(focus)
            if (focused === focus) focused = null
        }
    }

    fun place(
        focus: FocusRequester,
        y: Float,
    ) {
        itemPositions[focus] = y
    }

    fun unregister(focus: FocusRequester) {
        registeredItems.remove(focus)
        labels.remove(focus)
        itemPositions.remove(focus)
        if (focused === focus) focused = null
    }

    private fun typeAhead(
        event: KeyEvent,
        items: List<FocusRequester>,
    ): Boolean {
        val hasModifier = event.isCtrlPressed || event.isMetaPressed || event.isAltPressed
        val char = event.utf16CodePoint.toChar()
        if (hasModifier || !char.isLetterOrDigit()) return false
        if (lastType.elapsedNow().inWholeMilliseconds > MENU_TYPEAHEAD_RESET_MILLIS) search = ""
        lastType = kotlin.time.TimeSource.Monotonic.markNow()
        search += char
        val prefix = if (search.all { it.equals(char, ignoreCase = true) }) char.toString() else search
        val start = items.indexOf(focused)
        val candidates = (items.drop(start + 1) + items.take(start + 1))
        candidates.firstOrNull { labels[it]?.startsWith(prefix, ignoreCase = true) == true }?.requestFocus()
        return true
    }

    fun key(event: KeyEvent): Boolean {
        val order = if (event.type == KeyEventType.KeyDown) items else emptyList()
        if (order.isEmpty()) return false
        val i = order.indexOf(focused)
        val target =
            when (event.key) {
                Key.DirectionDown -> (i + 1).mod(order.size)
                Key.DirectionUp -> (if (i < 0) order.lastIndex else i - 1).mod(order.size)
                Key.MoveHome -> 0
                Key.MoveEnd -> order.lastIndex
                else -> return typeAhead(event, order)
            }
        order[target].requestFocus()
        return true
    }
}

internal val LocalContextMenuNavigation = staticCompositionLocalOf<ContextMenuNavigation?> { null }

@Composable
internal fun Modifier.contextMenuNavigation(
    enabled: Boolean,
    label: String,
): Modifier {
    val nav = LocalContextMenuNavigation.current ?: return this
    val focus = remember { FocusRequester() }
    DisposableEffect(nav, focus) {
        onDispose { nav.unregister(focus) }
    }
    SideEffect {
        nav.setEnabled(focus, enabled)
        nav.labels[focus] = label
    }
    return focusRequester(focus)
        .onGloballyPositioned { nav.place(focus, it.positionInRoot().y) }
        .onFocusChanged { if (it.isFocused) nav.focused = focus }
}

@Composable
internal fun contextMenuTypography(
    fallback: TextStyle = TextStyle.Default,
    label: Boolean = false,
): TextStyle =
    if (LocalContextMenuNova.current) {
        overlayTypography(
            if (label) MENU_LABEL_FONT_SIZE_SP else MENU_ROW_FONT_SIZE_SP,
            if (label) MENU_LABEL_LINE_HEIGHT_SP else MENU_ROW_LINE_HEIGHT_SP,
            medium = label,
            muted = label,
        )
            .copy(lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None))
            .merge(fallback)
    } else {
        fallback
    }
