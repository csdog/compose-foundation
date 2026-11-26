package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

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
    CompositionLocalConsumerModifierNode,
    PointerInputModifierNode {
    private var pointer: PointerId? = null
    private var touchOrigin: Offset? = null
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
        if (pass == PointerEventPass.Final && touchOrigin != null) {
            if (pointerEvent.changes.any { it.id == pointer && it.isConsumed }) onCancelPointerInput()
        }
        if (pass != PointerEventPass.Initial) return
        if (!region.enabled) {
            onCancelPointerInput()
            return
        }
        val active = pointer
        if (active == null) {
            if (pointerEvent.type == PointerEventType.Press) beginDrag(pointerEvent)
        } else {
            val change = pointerEvent.changes.firstOrNull { it.id == active } ?: return
            if (change.isConsumed) {
                onCancelPointerInput()
                return
            }
            val origin = touchOrigin
            if (!change.pressed) {
                onCancelPointerInput()
                if (origin == null) change.consume()
                return
            }
            if (origin != null) {
                claimTouchDrag(change, origin)
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

    private fun beginDrag(event: PointerEvent) {
        val pressed =
            event.changes.firstOrNull {
                it.changedToDown() && (it.type != PointerType.Mouse || event.buttons.isPrimaryPressed)
            } ?: return
        val handle = region.handle?.takeIf { it.isAttached } ?: return
        val rectangle = requireLayoutCoordinates().localBoundingBoxOf(handle, clipBounds = false)
        val minimum = with(requireDensity()) { (if (pressed.type == PointerType.Touch) 20.dp else 10.dp).toPx() }
        val extra = ((minimum - if (region.horizontal) rectangle.width else rectangle.height) / 2).coerceAtLeast(0f)
        val target =
            if (region.horizontal) {
                Rect(rectangle.left - extra, rectangle.top, rectangle.right + extra, rectangle.bottom)
            } else {
                Rect(rectangle.left, rectangle.top - extra, rectangle.right, rectangle.bottom + extra)
            }
        if (pressed.position in target) {
            pointer = pressed.id
            if (pressed.type == PointerType.Touch) {
                touchOrigin = pressed.position
            } else {
                region.onStart()
                pressed.consume()
            }
        }
    }

    private fun claimTouchDrag(
        change: PointerInputChange,
        origin: Offset,
    ) {
        val movement = change.position - origin
        val along = if (region.horizontal) movement.x else movement.y
        val across = if (region.horizontal) movement.y else movement.x
        val slop = currentValueOf(LocalViewConfiguration).touchSlop
        if (abs(across) > slop && abs(across) > abs(along)) {
            onCancelPointerInput()
        } else if (abs(along) > slop) {
            touchOrigin = null
            region.onStart()
            region.onDelta(along)
            change.consume()
        }
    }

    override fun onCancelPointerInput() {
        pointer = null
        touchOrigin = null
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
