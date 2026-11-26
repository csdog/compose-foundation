package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize

internal fun Modifier.scrollThumbPointer(
    vertical: Boolean,
    enabled: Boolean,
    offset: Float,
    length: Float,
    interactionSource: MutableInteractionSource,
    onDelta: (Float) -> Unit,
): Modifier = this then ScrollThumbPointerElement(vertical, enabled, offset, length, interactionSource, onDelta)

private data class ScrollThumbPointerElement(
    val vertical: Boolean,
    val enabled: Boolean,
    val offset: Float,
    val length: Float,
    val interactionSource: MutableInteractionSource,
    val onDelta: (Float) -> Unit,
) : ModifierNodeElement<ScrollThumbPointerNode>() {
    override fun create() = ScrollThumbPointerNode(this)

    override fun update(node: ScrollThumbPointerNode) {
        node.update(this)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "scrollThumbPointer"
    }
}

/** Capture the track without a suspending gesture loop; track presses center the thumb. */
private class ScrollThumbPointerNode(private var data: ScrollThumbPointerElement) :
    Modifier.Node(), PointerInputModifierNode {
    private var pointer: PointerId? = null
    private var drag: DragInteraction.Start? = null

    fun update(value: ScrollThumbPointerElement) {
        if (!value.enabled || value.vertical != data.vertical || value.interactionSource !== data.interactionSource) {
            onCancelPointerInput()
        }
        data = value
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        if (pass != PointerEventPass.Initial || !data.enabled) return
        val active = pointer
        if (active == null) {
            if (pointerEvent.type != PointerEventType.Press) return
            val change =
                pointerEvent.changes.firstOrNull {
                    it.changedToDown() && (it.type != PointerType.Mouse || pointerEvent.buttons.isPrimaryPressed)
                } ?: return
            val position = if (data.vertical) change.position.y else change.position.x
            pointer = change.id
            drag = DragInteraction.Start().also { data.interactionSource.tryEmit(it) }
            if (position < data.offset || position > data.offset + data.length) {
                data.onDelta(position - data.length / 2 - data.offset)
            }
            change.consume()
        } else {
            val change = pointerEvent.changes.firstOrNull { it.id == active } ?: return
            if (!change.pressed) {
                drag?.let { data.interactionSource.tryEmit(DragInteraction.Stop(it)) }
                drag = null
                pointer = null
            } else {
                val delta = change.positionChange()
                data.onDelta(if (data.vertical) delta.y else delta.x)
            }
            change.consume()
        }
    }

    override fun onCancelPointerInput() {
        drag?.let { data.interactionSource.tryEmit(DragInteraction.Cancel(it)) }
        drag = null
        pointer = null
    }

    override fun onDetach() {
        onCancelPointerInput()
    }
}
