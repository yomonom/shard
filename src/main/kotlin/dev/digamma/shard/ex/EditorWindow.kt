package dev.digamma.shard.ex

import com.intellij.openapi.fileEditor.ex.FileEditorOpenRequest
import com.intellij.openapi.fileEditor.impl.EditorComposite
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.fileEditor.impl.EditorWindowHolder
import com.intellij.openapi.project.Project
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

val EditorWindow.contextComposite
    get() = getSelectedComposite(false)

fun EditorWindow.getNeighbor(side: Side) =
    when (ShardSettings.getState().focusStrategy) {
        ShardSettings.FocusStrategy.LATEST -> getNeighbors(side).maxByOrNull(ShardFocusManager::getLastFocusTime)
        ShardSettings.FocusStrategy.NEAREST -> getNearestNeighbor(side)
    }

fun EditorWindow.getNeighbors(side: Side): Sequence<EditorWindow> {
    fun traverse(target: Component, location: Point): Sequence<EditorWindow> = sequence {
        if (target.touches(location, component.size, side)) {
            when (target) {
                is EditorWindowHolder -> yield(target.editorWindow)
                is Splitter -> yieldAll(target.children.flatMap { traverse(it, location - target.location) })
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

    // Traverse the hierarchy of the opposing component within each ancestral splitter
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

    // Find the first ancestor that contains the target location
    var target: Component? = component.hierarchy
        .takeWhile { it.parent !== owner }
        .firstNotNullOfOrNull { source ->
            location += source.location
            source.parent.takeIf { it.contains(location) }
        }

    // Traverse the hierarchy at the target location down to the first editor window
    while (target is Splitter) {
        target = target.getComponentAt(location)
            .also { location -= it.location }
    }

    return (target as? EditorWindowHolder)?.editorWindow
}

fun EditorWindow.moveComposite(project: Project, composite: EditorComposite, target: EditorWindow) {
    closeFile(composite.file, disposeIfNeeded = true, transferFocus = true)

    project.fileEditorManager.openFile(
        file = composite.file,
        options = FileEditorOpenRequest(
            targetWindow = target,
            pin = composite.isPinned,
            requestFocus = true,
            selectAsCurrent = true
        )
    )

    if (composite.isPinned) target.setFilePinned(composite.file, true)
}

fun EditorWindow.splitComposite(composite: EditorComposite, side: Side, move: Boolean) {
    if (move) closeFile(composite.file, disposeIfNeeded = false, transferFocus = false)

    val window = split(
        focusNew = true,
        forceSplit = true,
        virtualFile = composite.file,
        fileIsSecondaryComponent = side == Side.RIGHT || side == Side.BOTTOM,
        orientation = when (side) {
            Side.LEFT, Side.RIGHT -> SwingConstants.VERTICAL
            Side.TOP, Side.BOTTOM -> SwingConstants.HORIZONTAL
        }
    ) ?: return

    if (composite.isPinned) window.setFilePinned(composite.file, true)
}
