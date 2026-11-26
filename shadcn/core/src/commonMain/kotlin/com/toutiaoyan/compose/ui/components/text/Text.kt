@file:OptIn(
    androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class,
    kotlin.experimental.ExperimentalTypeInference::class,
)
@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.toutiaoyan.compose.ui.components.text

import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleOuterNode
import androidx.compose.foundation.style.TextDrawFlag
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.LocalSelectionRegistrar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.traverseAncestors
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.LocalTextDecoration
import com.toutiaoyan.compose.ui.theme.Theme
import kotlin.jvm.JvmName

internal val LocalDefaultTextStyle = staticCompositionLocalOf { TextStyle.Default }

// Preserve ancestor Style color when a component opts into selection for its content.
internal val LocalInheritSelectableTextColor = staticCompositionLocalOf { false }

// Empty opts in here so paragraph measurement sees the final Style shell and BasicText typography.
internal val LocalTextParagraphModifier = staticCompositionLocalOf<(@Composable (TextStyle) -> Modifier)?> { null }

/**
 * Usage:
 * ```
 * Text { "Hello world" }
 * Text(variant = TextVariant.BodySmall, textStyle = TextStyles.Muted) { "Subtitle" }
 * Text(style = Style { textXl2(); fontWeightBold() }) { "Large bold" }
 * Text(variant = TextVariant.BodySmall, textStyle = TextStyles.Muted) {
 *     buildAnnotatedString {
 *         append("Agree to our ")
 *         withLink(LinkAnnotation.Url("https://example.com")) { append("Terms") }
 *     }
 * }
 * ```
 *
 * Both [String] and [AnnotatedString] use trailing-lambda form. Resolution relies on
 * [OverloadResolutionByLambdaReturnType] (+ [JvmName] for JVM erasure of `Function0`).
 *
 * [textStyle] merges into [BasicText]'s layout style (alignment, weight, color, etc.). Prefer it
 * for tones like [TextColors.Muted] and for `textAlign` — Style `textAlign` / `textStyle` /
 * `contentColor` inheritance is skipped when the text is inside
 * [androidx.compose.foundation.text.selection.SelectionContainer].
 *
 * Style typography (`fontSize` / `lineHeight` / `fontWeight` / `textSm()` …) is inherited only on
 * the [String] overload (`BasicText(String)` → `TextStringSimpleNode`). The [AnnotatedString]
 * overload uses `TextAnnotatedStringNode`, which does **not** apply inherited Style typography in
 * Compose Foundation 1.11 — pass [textStyle] / [TwTextStyle][com.toutiaoyan.compose.tailwind.core.TwTextSize]
 * for rich text sizing instead.
 *
 * [inlineContent] maps ids from [androidx.compose.foundation.text.appendInlineContent] to
 * composables rendered inline (icons, images, etc.).
 *
 * Empty [textStyle] leaves ambient `LocalContentColor` from parents like Button/Badge intact;
 * set [textStyle] (e.g. [TextColors.Muted]) to override color. Use [style] for shell properties
 * (background, padding, shape).
 */
@Composable
@JvmName("TextString")
@OverloadResolutionByLambdaReturnType
fun Text(
    modifier: Modifier = Modifier,
    variant: TextVariant = TextVariant.BodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textStyle: TextStyle = TextStyle.Default,
    style: Style = Style,
    text: () -> String,
) {
    TextImpl(
        plainText = text(),
        annotatedText = null,
        modifier = modifier,
        variant = variant,
        maxLines = maxLines,
        overflow = overflow,
        textStyle = textStyle,
        style = style,
        inlineContent = emptyMap(),
    )
}

/**
 * Same typography / Style shell as the [String] overload, for rich text
 * (`withLink`, span styles, [inlineContent], etc.).
 *
 * Style `fontSize` / `textSm()` etc. do **not** inherit here — use [textStyle].
 */
@Composable
@JvmName("TextAnnotatedString")
@OverloadResolutionByLambdaReturnType
fun Text(
    modifier: Modifier = Modifier,
    variant: TextVariant = TextVariant.BodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textStyle: TextStyle = TextStyle.Default,
    style: Style = Style,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    text: () -> AnnotatedString,
) {
    TextImpl(
        plainText = null,
        annotatedText = text(),
        modifier = modifier,
        variant = variant,
        maxLines = maxLines,
        overflow = overflow,
        textStyle = textStyle,
        style = style,
        inlineContent = inlineContent,
    )
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun textParagraphModifier(
    isPlainText: Boolean,
    style: TextStyle,
): Modifier {
    val paragraphModifier = LocalTextParagraphModifier.current ?: return Modifier
    // Inherited String typography is resolved by the engine after composition.
    return if (isPlainText && androidx.compose.foundation.ComposeFoundationFlags.isInheritedTextStyleEnabled) {
        Modifier
    } else {
        paragraphModifier(style)
    }
}

@Composable
private fun TextImpl(
    plainText: String?,
    annotatedText: AnnotatedString?,
    modifier: Modifier,
    variant: TextVariant,
    maxLines: Int,
    overflow: TextOverflow,
    textStyle: TextStyle,
    style: Style,
    inlineContent: Map<String, InlineTextContent>,
) {
    val theme = Theme.LocalTheme.current
    // Compose 1.12 leaves Style text inheritance off
    // (`ComposeFoundationFlags.isInheritedTextStyleEnabled`) and, when it is on, a specified
    // TextStyle color overrides Style `contentColor`. Baking LocalContentColor into TextStyle
    // therefore paints over ancestor Style (Card / Button / WithAncestorContentColor).
    // Resolve the inherited content color from the Style node during BasicText drawing.
    // Inside SelectionContainer the string node skips Style inheritance, so LocalContentColor
    // (else theme.foreground) stays on TextStyle there.
    // A specified [textStyle] color is also pushed as a nearer Style `contentColor`.
    // LocalTextDecoration covers Link-button hover underline where Style inheritance is skipped.
    val merged =
        resolveTypography(theme, variant)
            .merge(if (variant == TextVariant.BodyMedium) LocalDefaultTextStyle.current else TextStyle.Default)
            .merge(textStyle)
    val specifiedColor = merged.color.takeIf { it.isSpecified }
    val ambientColor = LocalContentColor.current
    val fallbackColor = if (ambientColor.isSpecified) ambientColor else theme.colors.foreground
    val useSelectionFallback = LocalSelectionRegistrar.current != null && !LocalInheritSelectableTextColor.current
    val inheritedContentColor = remember { InheritedContentColor() }
    // ColorProducer is consumed in BasicText's draw phase. Ancestor Style updates
    // can repaint text without remeasurement or a state write back to composition.
    val colorProducer =
        ColorProducer {
            val inherited = inheritedContentColor.node
            val color =
                if (specifiedColor != null || useSelectionFallback) {
                    specifiedColor ?: fallbackColor
                } else {
                    inherited?.inheritedColor()?.takeIf { it.isSpecified } ?: fallbackColor
                }
            // When a Style brush paints the glyph mask, its alpha is the fill alpha.
            // Do not multiply it by the original solid text color's alpha a second time.
            if (inherited?.inheritedBrush() != null) color.copy(alpha = 1f) else color
        }
    val ambientDecoration = LocalTextDecoration.current
    val resolvedColor = specifiedColor ?: fallbackColor
    val resolvedStyle =
        run {
            val colored = merged.copy(color = resolvedColor)
            if (ambientDecoration != null && colored.textDecoration == null) {
                colored.copy(textDecoration = ambientDecoration)
            } else {
                colored
            }
        }
    val styleState = remember { MutableStyleState(interactionSource = null) }
    val colorStyle = Style { if (specifiedColor != null) contentColor(specifiedColor) }
    val paintStyle = remember(style) { style.preserveTextCurrentColor() }
    val styledModifier =
        modifier
            .styleable(styleState, paintStyle)
            // Foundation contentColor clears contentBrush on the same Style node.
            // Keep the explicit solid-color fallback nearer, on a separate node,
            // so it retains its precedence without deleting an ancestor brush.
            // Keep both Style nodes attached when the override changes between set and unset.
            .styleable(style = colorStyle)
            .then(AncestorContentColorElement(inheritedContentColor, resolvedColor))
            .then(textParagraphModifier(plainText != null, resolvedStyle))
    // Prefer BasicText(String): only TextStringSimpleNode applies Style-inherited typography
    // (fontSize / lineHeight / fontWeight). AnnotatedString always uses TextAnnotatedStringNode.
    if (plainText != null) {
        BasicText(
            text = plainText,
            modifier = styledModifier,
            style = resolvedStyle,
            color = colorProducer,
            maxLines = maxLines,
            overflow = overflow,
        )
    } else {
        BasicText(
            text = annotatedText!!,
            modifier = styledModifier,
            style = resolvedStyle,
            color = colorProducer,
            maxLines = maxLines,
            overflow = overflow,
            inlineContent = inlineContent,
        )
    }
}

/**
 * Reads the nearest explicitly specified Style content color. Compose Foundation does not expose a
 * public reader. Read each Style node's own resolved color rather than its inherited cache.
 * The element sits inside [styleable] so the nearest node includes this Text's own `contentColor`
 * and any ancestor's.
 */
private class InheritedContentColor {
    var node: AncestorContentColorNode? = null
}

private data class AncestorContentColorElement(
    private val color: InheritedContentColor,
    private val fallbackColor: Color,
) : ModifierNodeElement<AncestorContentColorNode>() {
    override fun create(): AncestorContentColorNode = AncestorContentColorNode(color, fallbackColor)

    override fun update(node: AncestorContentColorNode) {
        node.updateColor(color, fallbackColor)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "ancestorContentColor"
    }
}

private class AncestorContentColorNode(
    private var color: InheritedContentColor,
    private var fallbackColor: Color,
) : Modifier.Node(), LayoutModifierNode, DrawModifierNode {
    // Preserve the existing Style/text layout boundary; color resolution stays in draw.
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    fun updateColor(
        next: InheritedContentColor,
        fallback: Color,
    ) {
        fallbackColor = fallback
        if (color === next) return
        if (color.node === this) color.node = null
        color = next
        if (isAttached) color.node = this
    }

    override fun onAttach() {
        color.node = this
    }

    override fun onDetach() {
        if (color.node === this) color.node = null
    }

    override fun ContentDrawScope.draw() {
        val source = inheritedBrush()
        val brush =
            if (source is TextColorBrush) {
                source.resolveTextColor(
                    colorBeforeBrush().takeIf { it.isSpecified }
                        ?: source.fallbackTextColor.takeIf { it.isSpecified }
                        ?: inheritedColor().takeIf { it.isSpecified }
                        ?: fallbackColor,
                )
            } else {
                source
            }
        if (brush == null) {
            drawContent()
        } else {
            // Isolate only the glyph subtree: the gradient must not paint surrounding
            // whitespace, backgrounds or siblings. No layer is needed for ordinary text.
            drawIntoCanvas { canvas ->
                canvas.saveLayer(Rect(Offset.Zero, size), Paint())
                try {
                    drawContent()
                    drawRect(brush, blendMode = BlendMode.SrcIn)
                } finally {
                    canvas.restore()
                }
            }
        }
    }

    private fun colorBeforeBrush(): Color {
        var result = Color.Unspecified
        traverseAncestors(STYLE_OUTER_NODE_KEY) { node ->
            val resolved = (node as? StyleOuterNode)?.resolveAnimatedStyleFor(TextDrawFlag)
            if (!result.isSpecified && resolved?.contentColor?.isSpecified == true) result = resolved.contentColor
            resolved?.contentBrush == null
        }
        return result
    }

    fun inheritedBrush(): Brush? {
        var brush: Brush? = null
        traverseAncestors(STYLE_OUTER_NODE_KEY) { node ->
            val ownBrush = (node as? StyleOuterNode)?.resolveAnimatedStyleFor(TextDrawFlag)?.contentBrush
            if (ownBrush != null) {
                brush = ownBrush
                false
            } else {
                true
            }
        }
        return brush
    }

    fun inheritedColor(): Color {
        var inherited = Color.Unspecified
        traverseAncestors(STYLE_OUTER_NODE_KEY) { node ->
            val ownColor =
                (node as? StyleOuterNode)
                    ?.resolveAnimatedStyleFor(TextDrawFlag)?.contentColor ?: Color.Unspecified
            if (ownColor.isSpecified) {
                inherited = ownColor
                false
            } else {
                true
            }
        }
        return inherited
    }
}

private const val STYLE_OUTER_NODE_KEY = "StyleOuterNode"
