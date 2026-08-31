@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor

internal object TextColorTags {
    const val TextStyle = "text_color_1_textStyle"
    const val OwnStyle = "text_color_2_ownStyle"
    const val Ancestor = "text_color_3_ancestor"
    const val AncestorBeatsLocal = "text_color_3_ancestor_beats_local"
    const val LocalContent = "text_color_4_localContentColor"
    const val Fallback = "text_color_5_fallback"
    const val CardBody = "text_color_card_body"
    const val CardMuted = "text_color_card_muted"
    const val CardTextStyle = "text_color_card_textStyle"
    const val CardOwnStyle = "text_color_card_ownStyle"
    const val ButtonBody = "text_color_button_body"
    const val ButtonTextStyle = "text_color_button_textStyle"
    const val SelectionTextStyle = "text_color_sel_textStyle"
    const val SelectionOwnStyle = "text_color_sel_ownStyle"
    const val SelectionAncestor = "text_color_sel_ancestor"
    const val SelectionLocalBeatsAncestor = "text_color_sel_local_beats_ancestor"
    const val SelectionLocal = "text_color_sel_local"
    const val SelectionCardBody = "text_color_sel_card_body"
    const val SelectionCardMuted = "text_color_sel_card_muted"
    const val SelectionCardOwnStyle = "text_color_sel_card_ownStyle"
}

internal val TextStyleColor = Color(0xFFE11D48)
internal val OwnStyleColor = Color(0xFF0891B2)
internal val AncestorStyleColor = Color(0xFFD97706)
internal val LocalContentColorValue = Color(0xFF2563EB)

@Composable
private fun TextStylesContent() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextVariant.entries.forEach { typography ->
            Text(
                modifier = Modifier.testTag("text_style_${typography.name}"),
                variant = typography,
            ) { typography.name }
        }
        Text(
            modifier = Modifier.testTag("text_style_Muted"),
            textStyle = TextColors.Muted,
        ) { "Muted body text" }
    }
}

/**
 * Color precedence, highest first:
 * 1. textStyle color
 * 2. this Text's `style { contentColor }`
 * 3. ancestor Style `contentColor` (Card / Button / [WithAncestorContentColor])
 * 4. [LocalContentColor]
 * 5. `theme.colors.foreground`
 *
 * Inside [androidx.compose.foundation.text.selection.SelectionContainer], ancestor / own
 * Style `contentColor` is skipped; [textStyle] and [LocalContentColor] still apply.
 */
@Composable
internal fun TextColorInheritanceContent() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WithAncestorContentColor(AncestorStyleColor) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    modifier = Modifier.testTag(TextColorTags.TextStyle),
                    textStyle = TextStyle(color = TextStyleColor),
                    style = Style { contentColor(OwnStyleColor) },
                ) { "1 textStyle beats own style" }
                Text(
                    modifier = Modifier.testTag(TextColorTags.OwnStyle),
                    style = Style { contentColor(OwnStyleColor) },
                ) { "2 own style beats ancestor" }
                Text(modifier = Modifier.testTag(TextColorTags.Ancestor)) {
                    "3 ancestor Style contentColor"
                }
                CompositionLocalProvider(LocalContentColor provides LocalContentColorValue) {
                    Text(modifier = Modifier.testTag(TextColorTags.AncestorBeatsLocal)) {
                        "3 ancestor Style beats LocalContentColor"
                    }
                }
            }
        }
        CompositionLocalProvider(LocalContentColor provides LocalContentColorValue) {
            Text(modifier = Modifier.testTag(TextColorTags.LocalContent)) {
                "4 LocalContentColor (no ancestor Style)"
            }
        }
        Text(modifier = Modifier.testTag(TextColorTags.Fallback)) {
            "5 fallback = theme.foreground"
        }
        Card {
            Text(modifier = Modifier.testTag(TextColorTags.CardBody)) {
                "Card body = ancestor Style (cardForeground)"
            }
            Text(
                modifier = Modifier.testTag(TextColorTags.CardMuted),
                textStyle = TextColors.Muted,
            ) { "Muted textStyle beats Card" }
            Text(
                modifier = Modifier.testTag(TextColorTags.CardTextStyle),
                textStyle = TextStyle(color = TextStyleColor),
            ) { "textStyle beats Card" }
            Text(
                modifier = Modifier.testTag(TextColorTags.CardOwnStyle),
                style = Style { contentColor(OwnStyleColor) },
            ) { "own style beats Card" }
        }
        Button {
            Text(modifier = Modifier.testTag(TextColorTags.ButtonBody)) {
                "Button = ancestor Style (primaryForeground)"
            }
        }
        Button {
            Text(
                modifier = Modifier.testTag(TextColorTags.ButtonTextStyle),
                textStyle = TextStyle(color = TextStyleColor),
            ) { "textStyle beats Button" }
        }
        SelectionContainer {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WithAncestorContentColor(AncestorStyleColor) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            modifier = Modifier.testTag(TextColorTags.SelectionTextStyle),
                            textStyle = TextStyle(color = TextStyleColor),
                            style = Style { contentColor(OwnStyleColor) },
                        ) { "SC textStyle still applies" }
                        Text(
                            modifier = Modifier.testTag(TextColorTags.SelectionOwnStyle),
                            style = Style { contentColor(OwnStyleColor) },
                        ) { "SC skips own Style contentColor" }
                        Text(modifier = Modifier.testTag(TextColorTags.SelectionAncestor)) {
                            "SC skips ancestor Style contentColor"
                        }
                    }
                }
                CompositionLocalProvider(LocalContentColor provides LocalContentColorValue) {
                    WithAncestorContentColor(AncestorStyleColor) {
                        Text(modifier = Modifier.testTag(TextColorTags.SelectionLocalBeatsAncestor)) {
                            "SC LocalContentColor beats ancestor Style"
                        }
                    }
                    Text(modifier = Modifier.testTag(TextColorTags.SelectionLocal)) {
                        "SC LocalContentColor"
                    }
                }
                Card {
                    Text(modifier = Modifier.testTag(TextColorTags.SelectionCardBody)) {
                        "SC Card body via LocalContentColor"
                    }
                    Text(
                        modifier = Modifier.testTag(TextColorTags.SelectionCardMuted),
                        textStyle = TextColors.Muted,
                    ) { "SC Muted textStyle beats Card" }
                    Text(
                        modifier = Modifier.testTag(TextColorTags.SelectionCardOwnStyle),
                        style = Style { contentColor(OwnStyleColor) },
                    ) { "SC skips own Style, keeps Card LocalContentColor" }
                }
            }
        }
    }
}

@Composable
private fun WithAncestorContentColor(
    color: Color,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(interactionSource = null) }
    Box(Modifier.styleable(styleState, Style { contentColor(color) })) {
        content()
    }
}

@Preview(showBackground = true, name = "Text styles")
@Composable
private fun TextStylesPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = false) {
        TextStylesContent()
    }
}

@Preview(showBackground = true, name = "Text styles / dark")
@Composable
private fun TextStylesDarkPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = true) {
        TextStylesContent()
    }
}

@Preview(showBackground = true, name = "Text color inheritance", widthDp = 360)
@Composable
private fun TextColorInheritancePreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = false) {
        TextColorInheritanceContent()
    }
}

@Preview(showBackground = true, name = "Text color inheritance / dark", widthDp = 360)
@Composable
private fun TextColorInheritanceDarkPreview() {
    Theme(baseColor = BaseColor.Neutral, isDark = true) {
        TextColorInheritanceContent()
    }
}
