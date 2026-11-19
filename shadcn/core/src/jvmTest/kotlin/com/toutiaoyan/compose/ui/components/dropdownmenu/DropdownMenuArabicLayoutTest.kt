@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import java.io.File
import java.security.MessageDigest
import kotlin.math.roundToInt
import javax.imageio.ImageIO
import kotlin.test.*
import org.junit.Rule
import org.junit.Test

/** Diagnostic fixture: records the real row/text metrics, never forces a guessed 28px height. */
class DropdownMenuArabicLayoutTest {
    @get:Rule val rule = createComposeRule()

    @Test fun exactSettledWebFontAndArabicStringsExposeBaselineOverflowAndInkBounds() {
        val resource = "fonts/dropdown_noto_sans_arabic_v33.woff2"
        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream(resource)).use { it.readBytes() }
        val hash = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
        assertEquals("4e2ca0745c908761dc5c5db951662873887c59366fa1a5693ad22c0864abf1bd", hash)
        val labels =
            listOf(
                "الحساب", "الفريق", "دعوة المستخدمين", "فريق جديد", "شريط الحالة", "شريط النشاط",
                "اللوحة", "أعلى", "أسفل", "يمين", "يسار", "تسجيل الخروج",
            )
        val referenceLayouts = mutableMapOf<Int, TextLayoutResult>()
        rule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Theme(preset = StylePreset.Nova, fontFamily = FontFamily(Font(resource)), minTouchTarget = null) {
                    ArabicReferenceLabels(resource, labels, referenceLayouts)
                    Box(Modifier.size(400.dp, 600.dp)) {
                        DropdownMenu(open = true, onOpenChange = {}, modal = false) {
                            DropdownMenuContent(style = Style { width(144.dp) }) {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger(labels[0], Modifier.testTag("row-0"))
                                }
                                for (i in 1..3) DropdownMenuItem(labels[i], {}, Modifier.testTag("row-$i"))
                                for (i in 4..6) DropdownMenuCheckboxItem(
                                    labels[i],
                                    i == 4,
                                    Modifier.testTag("row-$i"),
                                    onCheckedChange = {},
                                )
                                DropdownMenuRadioGroup("8", {}) {
                                    for (i in 7..10) DropdownMenuRadioItem("$i", labels[i], Modifier.testTag("row-$i"))
                                }
                                DropdownMenuItem(labels[11], {}, Modifier.testTag("row-11"))
                            }
                        }
                    }
                }
            }
        }
        val report = File("build/reports/dropdown-stage20/arabic").apply { mkdirs() }
        val failures = mutableListOf<String>()
        val records =
            labels.mapIndexed { index, label ->
                arabicRowMetrics(ArabicRow(index, label, referenceLayouts.getValue(index)), report, hash, failures)
            }
        File(report, "metrics.json").writeText(records.joinToString(",\n", "[\n", "\n]"))
        File(report, "failures.txt").writeText(failures.joinToString("\n"))
        assertTrue(failures.isEmpty(), failures.joinToString("; "))
    }

    @Composable
    private fun ArabicReferenceLabels(
        resource: String,
        labels: List<String>,
        referenceLayouts: MutableMap<Int, TextLayoutResult>,
    ) {
        Column {
            labels.forEachIndexed { i, label ->
                BasicText(
                    label,
                    Modifier.width(IntrinsicSize.Max).testTag("reference-$i"),
                    style =
                        TextStyle(
                            fontFamily = FontFamily(Font(resource)),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = currentTheme.colors.popoverForeground,
                            lineHeightStyle =
                                LineHeightStyle(
                                    LineHeightStyle.Alignment.Center,
                                    LineHeightStyle.Trim.None,
                                ),
                        ),
                    onTextLayout = { referenceLayouts[i] = it },
                )
            }
        }
    }

    private data class ArabicRow(val index: Int, val label: String, val reference: TextLayoutResult)

    private fun arabicRowMetrics(
        rowData: ArabicRow,
        report: File,
        hash: String,
        failures: MutableList<String>,
    ): String {
        val (i, label, reference) = rowData
        val layouts = mutableListOf<TextLayoutResult>()
        val text = rule.onNode(hasText(label) and !hasTestTag("reference-$i"), useUnmergedTree = true)
        text.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        val row = rule.onNodeWithTag("row-$i").fetchSemanticsNode().boundsInWindow
        val textBounds = text.fetchSemanticsNode().boundsInWindow
        val image = rule.onNodeWithTag("row-$i").captureToImage()
        ImageIO.write(image.toAwtImage(), "png", File(report, "row-$i.png"))
        val pixels = image.toPixelMap()
        val ink = mutableListOf<Pair<Int, Int>>()
        // Ignore the left-side indicator/chevron; include the complete right-aligned label zone.
        for (y in 0 until pixels.height) for (x in 32 until pixels.width - 4) {
            val c = pixels[x, y]
            if (c.red < .4f && c.green < .4f && c.blue < .4f) ink += x to y
        }
        if (layout.lineCount != 1) failures += "$label lineCount=${layout.lineCount}"
        if (layout.getLineEnd(0, visibleEnd = true) != label.length) failures += "$label truncated"
        // String-node semantics reconstructs at max constraints, not the drawn intrinsic paragraph.
        val actualTextImage = text.captureToImage()
        ImageIO.write(actualTextImage.toAwtImage(), "png", File(report, "actual-text-$i.png"))
        val actualGlyphs = actualTextImage.toAwtImage()
        val referenceImage = rule.onNodeWithTag("reference-$i").captureToImage()
        ImageIO.write(referenceImage.toAwtImage(), "png", File(report, "reference-$i.png"))
        val expectedGlyphs = referenceImage.toAwtImage()

        val actualMask = glyphMask(actualGlyphs)
        val expectedMask = glyphMask(expectedGlyphs)
        if (actualMask != expectedMask) failures += "$label rendered glyph mask differs from complete reference"
        failures += rowLayoutFailures(label, layout, reference, row, textBounds)
        if (ink.isEmpty()) failures += "$label no glyph ink"
        if (ink.any { (_, y) -> y == 0 || y == pixels.height - 1 }) failures += "$label ink on row edge"
        val glyphBounds = (label.indices).map { layout.getBoundingBox(it) }
        return """{"label":"$label","fontSha256":"$hash","row":"$row","textBounds":"$textBounds",""" +
            """"textSize":"${layout.size}","baseline":${layout.firstBaseline},""" +
            """"lineTop":${layout.getLineTop(0)},"lineBottom":${layout.getLineBottom(0)},""" +
            """"lineLeft":${layout.getLineLeft(0)},"lineRight":${layout.getLineRight(0)},""" +
            """"overflow":${layout.hasVisualOverflow},"heightOverflow":${layout.didOverflowHeight},""" +
            """"glyphBounds":"$glyphBounds","referenceOverflow":${reference.hasVisualOverflow},""" +
            """"referenceSize":"${reference.size}","referenceBaseline":${reference.firstBaseline},""" +
            """"completeGlyphMaskEqual":${actualMask == expectedMask},""" +
            """"inkMinY":${ink.minOfOrNull { it.second }},"inkMaxY":${ink.maxOfOrNull { it.second }}}"""
    }

    private fun rowLayoutFailures(
        label: String,
        layout: TextLayoutResult,
        reference: TextLayoutResult,
        row: androidx.compose.ui.geometry.Rect,
        textBounds: androidx.compose.ui.geometry.Rect,
    ): List<String> =
        buildList {
            if (reference.hasVisualOverflow) add("$label reference hasVisualOverflow")
            if (layout.didOverflowHeight) add("$label vertical overflow")
            if (textBounds.top < row.top || textBounds.bottom > row.bottom) add("$label text outside row")
            if (layout.firstBaseline <= 0 || layout.firstBaseline >= layout.size.height) {
                add("$label baseline")
            }
        }

    private fun glyphMask(pixels: java.awt.image.BufferedImage): List<Pair<Int, Int>> {
        val points = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
            val argb = pixels.getRGB(x, y)
            val alpha = argb ushr 24 and 255

            fun overWhite(shift: Int): Int {
                val channel = argb ushr shift and 255
                return (channel * alpha / 255.0 + 255 - alpha).roundToInt()
            }
            if (overWhite(16) < 102 && overWhite(8) < 102 && overWhite(0) < 102) {
                points += x to y
            }
        }
        val left = points.minOfOrNull { it.first } ?: 0
        val top = points.minOfOrNull { it.second } ?: 0
        return points.map { (x, y) -> x - left to y - top }
    }
}
