package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import com.toutiaoyan.compose.ui.components.overlay.overlayTypography

internal val LocalContextMenuNova = staticCompositionLocalOf { false }

// Menubar selection marks occupy the leading slot; DropdownMenu marks are trailing.
internal val LocalContextMenuLeadingSelectionIndicator = staticCompositionLocalOf { false }

internal class ContextMenuNavigation {
    val items = mutableStateListOf<FocusRequester>()
    var focused: FocusRequester? = null
    val labels = mutableMapOf<FocusRequester, String>()
    var search = ""
    var lastType = kotlin.time.TimeSource.Monotonic.markNow()

    private fun typeAhead(event: KeyEvent): Boolean {
        if (event.isCtrlPressed || event.isMetaPressed || event.isAltPressed) return false
        val char = event.utf16CodePoint.toChar()
        if (!char.isLetterOrDigit()) return false
        if (lastType.elapsedNow().inWholeMilliseconds > 500) search = ""
        lastType = kotlin.time.TimeSource.Monotonic.markNow()
        search += char
        val prefix = if (search.all { it.equals(char, ignoreCase = true) }) char.toString() else search
        val start = items.indexOf(focused)
        val candidates = (items.drop(start + 1) + items.take(start + 1))
        candidates.firstOrNull { labels[it]?.startsWith(prefix, ignoreCase = true) == true }?.requestFocus()
        return true
    }

    fun key(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown || items.isEmpty()) return false
        val i = items.indexOf(focused)
        val target =
            when (event.key) {
                Key.DirectionDown -> (i + 1).mod(items.size)
                Key.DirectionUp -> (if (i < 0) items.lastIndex else i - 1).mod(items.size)
                Key.MoveHome -> 0
                Key.MoveEnd -> items.lastIndex
                else -> return typeAhead(event)
            }
        items[target].requestFocus()
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
    DisposableEffect(nav, enabled) {
        if (enabled) nav.items.add(focus)
        nav.labels[focus] = label
        onDispose {
            nav.items.remove(focus)
            nav.labels.remove(focus)
        }
    }
    SideEffect { nav.labels[focus] = label }
    return focusRequester(focus).onFocusChanged { if (it.isFocused) nav.focused = focus }
}

@Composable
internal fun contextMenuTypography(
    fallback: TextStyle = TextStyle.Default,
    label: Boolean = false,
): TextStyle =
    if (LocalContextMenuNova.current) {
        overlayTypography(if (label) 12 else 14, if (label) 16 else 20, medium = label, muted = label)
            .copy(lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None))
            .merge(fallback)
    } else {
        fallback
    }
