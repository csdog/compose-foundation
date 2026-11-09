package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/** Shared by a group and its separator; registrations do not become render state. */
internal class ResizablePointerRegion {
    var handle: LayoutCoordinates? = null
    var horizontal = true
    var enabled = false
    var onStart: () -> Unit = {}
    var onDelta: (Float) -> Unit = {}
    var cancelGesture: (() -> Unit)? = null
}

/** Route from the entire group so a wider hit target never consumes panel layout space. */
internal fun Modifier.resizablePointerRouter(region: ResizablePointerRegion): Modifier =
    this then ResizableDragElement(region)

private data class ResizableDragElement(val region: ResizablePointerRegion) : ModifierNodeElement<ResizableDragNode>() {
    override fun create() = ResizableDragNode(region)

    override fun update(node: ResizableDragNode) {
        node.updateRegion(region)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "resizablePointerRouter"
    }
}

private class ResizableDragNode(private var region: ResizablePointerRegion) :
    Modifier.Node(),
    PointerInputModifierNode {
    private var pointer: PointerId? = null
    private val cancelCallback: () -> Unit = { onCancelPointerInput() }

    fun updateRegion(value: ResizablePointerRegion) {
        if (region === value) return
        if (region.cancelGesture === cancelCallback) region.cancelGesture = null
        onCancelPointerInput()
        region = value
        if (isAttached) region.cancelGesture = cancelCallback
    }

    override fun onAttach() {
        region.cancelGesture = cancelCallback
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        if (pass != PointerEventPass.Initial) return
        if (!region.enabled) {
            onCancelPointerInput()
            return
        }
        val active = pointer
        if (active == null) {
            if (pointerEvent.type != PointerEventType.Press) return
            val pressed =
                pointerEvent.changes.firstOrNull {
                    it.changedToDown() && (it.type != PointerType.Mouse || pointerEvent.buttons.isPrimaryPressed)
                } ?: return
            val handle = region.handle?.takeIf { it.isAttached } ?: return
            val rectangle = requireLayoutCoordinates().localBoundingBoxOf(handle, clipBounds = false)
            val minimum = with(requireDensity()) { (if (pressed.type == PointerType.Touch) 20.dp else 10.dp).toPx() }
            val extra = ((minimum - if (region.horizontal) rectangle.width else rectangle.height) / 2).coerceAtLeast(0f)
            val target =
                if (region.horizontal) {
                    Rect(
                        rectangle.left - extra,
                        rectangle.top,
                        rectangle.right +
                            extra,
                        rectangle.bottom,
                    )
                } else {
                    Rect(rectangle.left, rectangle.top - extra, rectangle.right, rectangle.bottom + extra)
                }
            if (!target.contains(pressed.position)) return
            pointer = pressed.id
            region.onStart()
            pressed.consume()
        } else {
            val change = pointerEvent.changes.firstOrNull { it.id == active } ?: return
            if (!change.pressed) {
                pointer = null
                change.consume()
                return
            }
            val delta = change.positionChange()
            val distance = if (region.horizontal) delta.x else delta.y
            if (distance != 0f) {
                region.onDelta(distance)
                change.consume()
            }
        }
    }

    override fun onCancelPointerInput() {
        pointer = null
    }

    override fun onDetach() {
        onCancelPointerInput()
        if (region.cancelGesture === cancelCallback) {
            region.cancelGesture =
                null
        }
    }
}

/** Keeps pointer overshoot until it returns inside the permitted range. */
internal class ResizableDragAccumulator {
    private var initialFraction = 0f
    private var extent = 0f
    private var displacement = 0f

    fun start(
        fraction: Float,
        availableExtent: Float,
    ) {
        initialFraction = fraction
        extent = availableExtent
        displacement = 0f
    }

    fun drag(
        delta: Float,
        min: Float,
        max: Float,
        rtl: Boolean,
    ): Float {
        if (delta.isFinite()) displacement += if (rtl) -delta else delta
        return resizablePanelFraction(initialFraction, displacement, extent, min, max)
    }
}
