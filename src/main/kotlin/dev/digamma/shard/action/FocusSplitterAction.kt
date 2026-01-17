package dev.digamma.shard.action

import com.intellij.openapi.actionSystem.AnActionEvent
import dev.digamma.shard.ext.editorWindow
import dev.digamma.shard.ext.getNeighbor
import dev.digamma.shard.util.Side

class FocusSplitterAction(private val side: Side, template: Template) : ShardBaseAction(template) {
    override fun doUpdate(event: AnActionEvent) =
        event.editorWindow.let {
            when {
                it == null -> State.HIDDEN
                it.getNeighbor(side) != null -> State.ENABLED
                else -> State.DISABLED
            }
        }

    override fun doPerform(event: AnActionEvent) {
        event.editorWindow?.getNeighbor(side)?.requestFocus(true)
    }

    @Suppress("CompanionObjectInExtension")
    companion object {
        val LEFT = FocusSplitterAction(Side.LEFT, Template("action.focus.splitter.left.text"))
        val TOP = FocusSplitterAction(Side.TOP, Template("action.focus.splitter.top.text"))
        val RIGHT = FocusSplitterAction(Side.RIGHT, Template("action.focus.splitter.right.text"))
        val BOTTOM = FocusSplitterAction(Side.BOTTOM, Template("action.focus.splitter.bottom.text"))
    }
}
