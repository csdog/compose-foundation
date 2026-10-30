package com.toutiaoyan.compose.ui.components.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import kotlin.math.abs
import kotlin.test.assertTrue

/** GetTextLayoutResult carries layout input; ColorProducer supplies the actual paint. */
internal fun SemanticsNodeInteraction.assertPaintedTextColor(expected: Color) {
    val pixels = captureToImage().toPixelMap()
    var matching = 0
    for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
        val actual = pixels[x, y]
        if (abs(actual.red - expected.red) < .025f &&
            abs(actual.green - expected.green) < .025f &&
            abs(actual.blue - expected.blue) < .025f && actual.alpha > .9f
        ) {
            matching++
        }
    }
    assertTrue(matching >= 3, "Expected painted glyphs of $expected, found $matching matching pixels")
}
