package dev.digamma.shard.ext

import com.intellij.openapi.fileEditor.impl.EditorComposite
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.fileEditor.impl.EditorWindowHolder
import com.intellij.openapi.ui.Splitter
import dev.digamma.shard.util.Side
import java.awt.Component
import java.awt.Point
import javax.swing.SwingConstants

val EditorWindow.component
    get() = tabbedPane.component

val EditorWindow.splitter
    get() = component.parent as? Splitter

fun EditorWindow.getNeighbor(side: Side): EditorWindow? =
    getNearestNeighbor(side)

fun EditorWindow.getNearestNeighbor(side: Side): EditorWindow? {
    // Shift the target location by 25% to compensate for slight visual offsets
    val location = when (side) {
        Side.LEFT -> Point(-2, component.height / 4)
        Side.TOP -> Point(component.width / 4, -2)
        Side.RIGHT -> Point(component.width + 2, component.height / 4)
        Side.BOTTOM -> Point(component.width / 4, component.height + 2)
    }

    // Find the first ancestor containing the target location
    var target: Component? = component.hierarchy
        .takeWhile { it.parent != owner }
        .firstNotNullOfOrNull { source ->
            location += source.location
            source.parent.takeIf { it.contains(location) }
        }

    // Traverse all splitters at the target location down to the first editor window
    while (target is Splitter) target = target.getComponentAt(location).also { location -= it.location }
    return (target as? EditorWindowHolder)?.editorWindow
}

fun EditorWindow.splitComposite(composite: EditorComposite, side: Side, move: Boolean) {
    if (move) closeFile(composite.file, disposeIfNeeded = false, transferFocus = false)

    split(
        focusNew = true,
        forceSplit = true,
        virtualFile = composite.file,
        fileIsSecondaryComponent = side == Side.RIGHT || side == Side.BOTTOM,
        orientation = when (side) {
            Side.LEFT, Side.RIGHT -> SwingConstants.VERTICAL
            Side.TOP, Side.BOTTOM -> SwingConstants.HORIZONTAL
        }
    )?.setFilePinned(composite.file, composite.isPinned)
}
