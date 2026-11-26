package com.toutiaoyan.compose.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

/**
 * The content color a [com.toutiaoyan.compose.ui.components.icon.Icon] should
 * inherit from its nearest ancestor, mirroring the ambient `contentColor` that the Compose
 * Foundation Style API already gives [com.toutiaoyan.compose.ui.components.text.Text]
 * -- that mechanism has no public reader (`StyleScope.contentColor` is write-only, resolved
 * internally by the Style API's own text-rendering pipeline), so non-text content can't hook
 * into it directly. Components that already call `contentColor(...)` in their `Style { }` block
 * (`Button`, etc.) additionally provide this CompositionLocal with the same resolved color
 * so [Icon] can match without every call site passing an explicit `tint`.
 *
 * Defaults to [Color.Unspecified] -- [Icon] falls back to `colors.foreground` when nothing
 * provides this, the same default [com.toutiaoyan.compose.ui.components.text.Text]
 * uses.
 */
val LocalContentColor = compositionLocalOf { Color.Unspecified }

/**
 * Ambient [TextDecoration] for [com.toutiaoyan.compose.ui.components.text.Text].
 *
 * Style `textDecoration(...)` is supposed to inherit into child text, but that path is skipped
 * inside [androidx.compose.foundation.text.selection.SelectionContainer] (same limitation as
 * Style `textAlign`). Parents that need a reliable underline — e.g.
 * [com.toutiaoyan.compose.ui.components.button.ButtonVariant.Link] on hover — set this instead;
 * [com.toutiaoyan.compose.ui.components.text.Text] merges it when its own
 * [androidx.compose.ui.text.TextStyle] leaves `textDecoration` unspecified.
 *
 * `null` means “no ambient decoration”.
 */
val LocalTextDecoration = compositionLocalOf<TextDecoration?> { null }
