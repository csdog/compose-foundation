package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sun.management.ThreadMXBean
import java.lang.management.ManagementFactory
import kotlin.system.measureNanoTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalFoundationStyleApi::class)
class SizingStylePerfTest {
    @Test
    fun twoCapturedStyleBlocksAreNotStructurallyEqual() {
        val w = 16.dp
        val a = Style { width(w) }
        val b = Style { width(w) }
        assertFalse(a === b)
        assertNotEquals(a, b)
        println("Style { width(w) } runtime class = ${a::class.java.name}")
    }

    @Test
    fun twoModifierWidthCallsAreStructurallyEqual() {
        val a = Modifier.width(16.dp)
        val b = Modifier.width(16.dp)
        assertFalse(a === b)
        assertEquals(a, b)
        println("Modifier.width(16.dp) runtime class = ${a::class.java.name}")
    }

    @Test
    fun rememberedInstanceStaysIdentical() {
        val remembered = Style { width(16.dp) }
        assertTrue(remembered === remembered)
        assertTrue(remembered == remembered)
    }

    @Test
    fun reportStyleAndModifierConstructionCost() {
        val iterations = 200_000
        warmup(20_000)

        val w = 16.dp
        val h = 16.dp
        val pad = 16.dp
        val color = Color.Black

        val styleWidth = bench(iterations) { Style { width(w) } }
        val modifierWidth = bench(iterations) { Modifier.width(w) }

        val styleBlock2 =
            bench(iterations) {
                Style {
                    width(w)
                    height(h)
                }
            }
        val modifierChain2 = bench(iterations) { Modifier.width(w).height(h) }

        val styleBlock4 =
            bench(iterations) {
                Style {
                    width(w)
                    height(h)
                    background(color)
                    p(pad)
                }
            }
        val modifierChain4 =
            bench(iterations) { Modifier.width(w).height(h).background(color).padding(pad) }

        val rememberedStyle =
            Style {
                width(w)
                height(h)
                background(color)
                p(pad)
            }
        val rememberedModifier = Modifier.width(w).height(h).background(color).padding(pad)
        val reuseStyle = bench(iterations) { rememberedStyle }
        val reuseModifier = bench(iterations) { rememberedModifier }

        val report =
            buildString {
                appendLine("Style vs Modifier construction ($iterations iterations, JVM)")
                appendLine(row("variant", "ns/op", "bytes/op", "sample class"))
                appendLine(row("Style { width(w) }", styleWidth))
                appendLine(row("Modifier.width(w)", modifierWidth))
                appendLine(row("Style { width; height }", styleBlock2))
                appendLine(row("Modifier.width().height()", modifierChain2))
                appendLine(row("Style { 4 props }", styleBlock4))
                appendLine(row("Modifier 4-chain", modifierChain4))
                appendLine(row("reuse Style block", reuseStyle))
                appendLine(row("reuse Modifier chain", reuseModifier))
            }
        println(report)
        assertTrue(styleWidth.nanosPerOp > 0.0)
        assertTrue(modifierWidth.nanosPerOp > 0.0)
    }

    private fun warmup(n: Int) {
        var sink = 0
        val w = 16.dp
        repeat(n) {
            sink +=
                Style {
                    width(w)
                    height(w)
                    background(Color.Black)
                    p(w)
                }.hashCode()
            sink += Modifier.width(w).height(w).background(Color.Black).padding(w).hashCode()
        }
        check(sink != Int.MIN_VALUE)
    }

    private fun bench(
        iterations: Int,
        factory: () -> Any,
    ): Bench {
        var sink = 0
        val bean = ManagementFactory.getThreadMXBean() as ThreadMXBean
        System.gc()
        val beforeAlloc = bean.currentThreadAllocatedBytes
        val nanos =
            measureNanoTime {
                repeat(iterations) {
                    sink += factory().hashCode()
                }
            }
        val allocated = bean.currentThreadAllocatedBytes - beforeAlloc
        check(sink != Int.MIN_VALUE)
        return Bench(
            nanosPerOp = nanos.toDouble() / iterations,
            bytesPerOp = allocated.toDouble() / iterations,
            sampleClass = factory()::class.java.name,
        )
    }

    private data class Bench(
        val nanosPerOp: Double,
        val bytesPerOp: Double,
        val sampleClass: String,
    )

    private fun row(
        name: String,
        bench: Bench,
    ): String =
        row(
            name,
            "%.1f".format(bench.nanosPerOp),
            "%.1f".format(bench.bytesPerOp),
            bench.sampleClass.substringAfterLast('.'),
        )

    private fun row(
        a: String,
        b: String,
        c: String,
        d: String,
    ): String = "%-32s %10s %10s  %s".format(a, b, c, d)
}
