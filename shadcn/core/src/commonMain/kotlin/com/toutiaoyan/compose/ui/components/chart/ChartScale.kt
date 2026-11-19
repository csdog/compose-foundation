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

internal const val CHART_NUMERIC_GRID_DIVISIONS = 4
private const val CHART_NUMERIC_TICK_COUNT = CHART_NUMERIC_GRID_DIVISIONS + 1
private const val SINGLE_DIGIT_STEP_FRACTION = 0.1
private const val ADAPTIVE_STEP_FRACTION = 0.05
private const val TICK_ROUNDING_TOLERANCE = 1e-12

internal data class ChartDomain(val minimum: Double, val maximum: Double)

/**
 * Recharts v3.3 adaptive five-tick numeric domain, including zero like the default Y axis.
 * Ported from getFormatStep/calculateStep; Double arithmetic replaces decimal.js-light.
 * Source, version and license: ../archive/compose-foundation/2026-11-19/docs/web-parity/chart-r1/recharts-scale-sources.json.
 */
internal fun chartNiceDomain(values: List<Float>): ChartDomain {
    val finite = values.filter(Float::isFinite).map(Float::toDouble)
    val low = (finite.minOrNull() ?: 0.0).coerceAtMost(0.0)
    val high = (finite.maxOrNull() ?: 0.0).coerceAtLeast(0.0)
    if (low == high) return ChartDomain(0.0, CHART_NUMERIC_GRID_DIVISIONS.toDouble())
    val rough = (high - low) / CHART_NUMERIC_GRID_DIVISIONS
    val digits = floor(log10(rough)).toInt() + 1
    val magnitude = 10.0.pow(digits)
    val ratioScale = if (digits == 1) SINGLE_DIGIT_STEP_FRACTION else ADAPTIVE_STEP_FRACTION
    val base = ceil(rough / magnitude / ratioScale - TICK_ROUNDING_TOLERANCE)
    var correction = 0
    while (true) {
        val step = (base + correction) * ratioScale * magnitude
        var below = ceil(-low / step - TICK_ROUNDING_TOLERANCE).toInt()
        var above = ceil(high / step - TICK_ROUNDING_TOLERANCE).toInt()
        val count = below + above + 1
        if (count > CHART_NUMERIC_TICK_COUNT) {
            correction++
            continue
        }
        if (count < CHART_NUMERIC_TICK_COUNT) {
            val missingTicks = CHART_NUMERIC_TICK_COUNT - count
            if (high > 0) above += missingTicks else below += missingTicks
        }
        return ChartDomain(-below * step, above * step)
    }
}
