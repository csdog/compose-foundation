/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-present recharts
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.toutiaoyan.compose.ui.components.chart

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

internal data class ChartDomain(val minimum: Double, val maximum: Double)

/**
 * Recharts v3.3 adaptive five-tick numeric domain, including zero like the default Y axis.
 * Ported from getFormatStep/calculateStep; Double arithmetic replaces decimal.js-light.
 * Source, version and license: docs/web-parity/chart-r1/recharts-scale-sources.json.
 */
internal fun chartNiceDomain(values: List<Float>): ChartDomain {
    val finite = values.filter(Float::isFinite).map(Float::toDouble)
    val low = (finite.minOrNull() ?: 0.0).coerceAtMost(0.0)
    val high = (finite.maxOrNull() ?: 0.0).coerceAtLeast(0.0)
    if (low == high) return ChartDomain(0.0, 4.0)
    val rough = (high - low) / 4
    val digits = floor(log10(rough)).toInt() + 1
    val magnitude = 10.0.pow(digits)
    val ratioScale = if (digits == 1) .1 else .05
    val base = ceil(rough / magnitude / ratioScale - 1e-12)
    var correction = 0
    while (true) {
        val step = (base + correction) * ratioScale * magnitude
        var below = ceil(-low / step - 1e-12).toInt()
        var above = ceil(high / step - 1e-12).toInt()
        val count = below + above + 1
        if (count > 5) {
            correction++
            continue
        }
        if (count < 5) {
            if (high > 0) above += 5 - count else below += 5 - count
        }
        return ChartDomain(-below * step, above * step)
    }
}
