package dev.digamma.shard.action

import com.intellij.openapi.actionSystem.AnActionEvent
import dev.digamma.shard.ShardSettings
import dev.digamma.shard.ex.contextComposite
import dev.digamma.shard.ex.getNeighbor
import dev.digamma.shard.ex.moveComposite
import dev.digamma.shard.ex.splitComposite
import dev.digamma.shard.util.Side

abstract class MoveTabToSplitterAction(private val side: Side, template: Template) : ShardAction(template) {
    override fun doUpdate(event: AnActionEvent) =
        event.editorWindow.let {
            when {
                it?.contextComposite == null -> State.HIDDEN
                it.getNeighbor(side) != null || (ShardSettings.getState().splitOnMove && it.tabCount > 1) -> State.ENABLED
                else -> State.DISABLED
            }
        }

    override fun doPerform(event: AnActionEvent) {
        val project = event.project ?: return

        event.editorWindow?.run {
            val composite = contextComposite ?: return
            val target = getNeighbor(side)

            if (target != null) moveComposite(project, composite, target)
            else if (tabCount > 1) splitComposite(composite, side, true)
        }
    }

    class Left : MoveTabToSplitterAction(Side.LEFT, Template("action.move.tab.to.splitter.left.text"))
    class Top : MoveTabToSplitterAction(Side.TOP, Template("action.move.tab.to.splitter.top.text"))
    class Right : MoveTabToSplitterAction(Side.RIGHT, Template("action.move.tab.to.splitter.right.text"))
    class Bottom : MoveTabToSplitterAction(Side.BOTTOM, Template("action.move.tab.to.splitter.bottom.text"))
}
