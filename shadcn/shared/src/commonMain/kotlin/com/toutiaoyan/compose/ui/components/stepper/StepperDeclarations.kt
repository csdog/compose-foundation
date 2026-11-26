package com.toutiaoyan.compose.ui.components.stepper

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import kotlinx.coroutines.channels.Channel

private class StepperEntry(val step: State<StepperStep>)

private class StepperRegistry {
    var registered by mutableStateOf<List<StepperEntry>>(emptyList())
    var measured by mutableStateOf<List<StepperEntry>>(emptyList())
    val layoutOrder = Channel<List<StepperEntry>>(Channel.CONFLATED)
    var lastLayoutOrder = emptyList<StepperEntry>()

    fun steps(): List<StepperStep> {
        // A declaration nested inside a caller's layout is registered by composition.
        // Direct declarations can follow actual layout order even if keyed nodes are moved without recomposition.
        val hasMeasuredEveryEntry = measured.size == registered.size && measured.all { it in registered }
        val entries = if (hasMeasuredEveryEntry) measured else registered
        return entries.map { it.step.value }
    }
}

private class StepperCollector(val registry: StepperRegistry) {
    val declarations = mutableListOf<StepperEntry>()
}

private val LocalStepperCollector = staticCompositionLocalOf<StepperCollector?> { null }

@Composable
internal fun collectStepperSteps(content: @Composable () -> Unit): List<StepperStep> {
    val registry = remember { StepperRegistry() }
    LaunchedEffect(registry) {
        for (order in registry.layoutOrder) registry.measured = order
    }
    val collector = StepperCollector(registry)
    Layout(
        content = {
            CompositionLocalProvider(LocalStepperCollector provides collector) {
                content()
                val declarations = collector.declarations.toList()
                SideEffect { registry.registered = declarations }
            }
        },
    ) { measurables, constraints ->
        val entries = measurables.mapNotNull { it.layoutId as? StepperEntry }
        // Measurement observes keyed moves without writing snapshot state during layout.
        if (entries != registry.lastLayoutOrder) {
            registry.lastLayoutOrder = entries
            registry.layoutOrder.trySend(entries)
        }
        val children = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val width = children.maxOfOrNull { it.width } ?: 0
        val height = children.maxOfOrNull { it.height } ?: 0
        layout(width, height) { children.forEach { it.placeRelative(0, 0) } }
    }
    return registry.steps()
}

@Composable
internal fun DeclareStepperStep(step: StepperStep) {
    val collector =
        checkNotNull(LocalStepperCollector.current) {
            "StepperItem must be used inside Stepper { … }"
        }
    val state = rememberUpdatedState(step)
    val entry = remember { StepperEntry(state) }
    collector.declarations.add(entry)
    val registry = collector.registry
    SideEffect {
        if (entry !in registry.registered) registry.registered = registry.registered + entry
    }
    DisposableEffect(registry, entry) {
        onDispose { registry.registered = registry.registered - entry }
    }
    Spacer(Modifier.layoutId(entry))
}
