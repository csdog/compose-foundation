package com.toutiaoyan.compose.ui.components.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.ThemeData

/**
 * Preset [TextStyle] values for [Text]'s [Text.textStyle] parameter.
 *
 * Prefer these over Style `contentColor` / `textStyle` when the intent is a design-system
 * tone (e.g. muted secondary copy). Passing color via [TextStyle] keeps it working inside
 * [androidx.compose.foundation.text.selection.SelectionContainer], where Style text
 * inheritance is skipped.
 */
object TextColors {
    /** Secondary / de-emphasized copy — `mutedForeground`. */
    val Muted: TextStyle
        @Composable get() = TextStyle(color = Theme.colors.mutedForeground)
}

enum class TextVariant {
    DisplayLarge,
    DisplayMedium,
    TitleLarge,
    TitleMedium,
    TitleSmall,
    BodyLarge,
    BodyMedium,
    BodySmall,
    LabelLarge,
    LabelSmall,
}


/**
 * Resolves the same [TextVariant] -> [TextStyle] mapping [Text] uses, factored out so
 * [com.toutiaoyan.compose.ui.components.emojitext.EmojiText] can match it exactly
 * without the two composables' typography rules drifting apart over time.
 */
internal fun resolveTypography(
    theme: ThemeData,
    typography: TextVariant,
): TextStyle =
    when (typography) {
        TextVariant.DisplayLarge -> theme.typography.displayLarge
        TextVariant.DisplayMedium -> theme.typography.displayMedium
        TextVariant.TitleLarge -> theme.typography.titleLarge
        TextVariant.TitleMedium -> theme.typography.titleMedium
        TextVariant.TitleSmall -> theme.typography.titleSmall
        TextVariant.BodyLarge -> theme.typography.bodyLarge
        TextVariant.BodyMedium -> theme.typography.bodyMedium
        TextVariant.BodySmall -> theme.typography.bodySmall
        TextVariant.LabelLarge -> theme.typography.labelLarge
        TextVariant.LabelSmall -> theme.typography.labelSmall
    }
