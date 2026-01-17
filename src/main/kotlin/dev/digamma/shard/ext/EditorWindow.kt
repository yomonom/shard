package dev.digamma.shard.ext

import com.intellij.openapi.fileEditor.impl.EditorComposite
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.fileEditor.impl.EditorWindowHolder
import com.intellij.openapi.fileEditor.impl.FileEditorOpenOptions
import com.intellij.openapi.ui.Splitter
import dev.digamma.shard.ShardFocusManager
import dev.digamma.shard.ShardSettings
import dev.digamma.shard.util.Side
import java.awt.Component
import java.awt.Point
import javax.swing.SwingConstants

val EditorWindow.component
    get() = tabbedPane.component

val EditorWindow.splitter
    get() = component.parent as? Splitter

fun EditorWindow.getNeighbor(side: Side): EditorWindow? =
    when (ShardSettings.getState().focusStrategy) {
        ShardSettings.FocusStrategy.LATEST -> getNeighbors(side).maxByOrNull(ShardFocusManager::getLastFocusTime)
        ShardSettings.FocusStrategy.NEAREST -> getNearestNeighbor(side)
    }

fun EditorWindow.getNeighbors(side: Side): Sequence<EditorWindow> {
    // Find all editor windows to the specified side of the target component
    fun traverse(target: Component, location: Point): Sequence<EditorWindow> = sequence {
        if (target.touches(location, component.size, side)) {
            when (target) {
                is EditorWindowHolder -> yield(target.editorWindow)
                is Splitter -> yieldAll(target.children.flatMap {
                    traverse(it, location - target.location)
                })
            }
        }
    }

    // Offset the target location by 1 pixel to account for the width of the divider
    val location = when (side) {
        Side.LEFT -> Point(-1, 0)
        Side.TOP -> Point(0, -1)
        Side.RIGHT -> Point(1, 0)
        Side.BOTTOM -> Point(0, 1)
    }

    // Traverse the component hierarchy of the opposite component in each ancestral splitter
    return component.hierarchy
        .takeWhile { it.parent != owner }
        .flatMap { source ->
            location += source.location
            (source.parent as? Splitter)
                ?.getOtherComponent(source)
                ?.let { traverse(it, location) }.orEmpty()
        }
}

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
        .takeWhile { it.parent !== owner }
        .firstNotNullOfOrNull { source ->
            location += source.location
            source.parent.takeIf { it.contains(location) }
        }

    // Traverse all splitters at the target location down to the first editor window
    while (target is Splitter) target = target.getComponentAt(location).also { location -= it.location }
    return (target as? EditorWindowHolder)?.editorWindow
}

@Suppress("UnstableApiUsage")
fun EditorWindow.moveComposite(composite: EditorComposite, target: EditorWindow) {
    closeFile(composite.file, disposeIfNeeded = true, transferFocus = true)

    target.manager.openFile(
        file = composite.file,
        window = target,
        options = FileEditorOpenOptions(
            pin = composite.isPinned,
            requestFocus = true,
            selectAsCurrent = true
        )
    )
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
