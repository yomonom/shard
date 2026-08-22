package dev.digamma.shard.action

import com.intellij.openapi.actionSystem.AnActionEvent
import dev.digamma.shard.ex.contextComposite
import dev.digamma.shard.ex.isSplittable
import dev.digamma.shard.ex.splitComposite
import dev.digamma.shard.util.Side

abstract class SplitAction(private val side: Side, private val move: Boolean, template: Template) :
    ShardAction(template) {
    override fun doUpdate(event: AnActionEvent) =
        event.editorWindow.let {
            val composite = it?.contextComposite

            when {
                composite == null -> State.HIDDEN
                composite.isSplittable && (!move || it.tabCount > 1) -> State.ENABLED
                else -> State.DISABLED
            }
        }

    override fun doPerform(event: AnActionEvent) {
        event.editorWindow?.run {
            splitComposite(contextComposite ?: return, side, move)
        }
    }

    class Left : SplitAction(Side.LEFT, false, Template("action.split.left.text"))
    class Top : SplitAction(Side.TOP, false, Template("action.split.top.text"))
    class Right : SplitAction(Side.RIGHT, false, Template("action.split.right.text"))
    class Bottom : SplitAction(Side.BOTTOM, false, Template("action.split.bottom.text"))

    class MoveLeft : SplitAction(Side.LEFT, true, Template("action.split.and.move.left.text"))
    class MoveTop : SplitAction(Side.TOP, true, Template("action.split.and.move.top.text"))
    class MoveRight : SplitAction(Side.RIGHT, true, Template("action.split.and.move.right.text"))
    class MoveBottom : SplitAction(Side.BOTTOM, true, Template("action.split.and.move.bottom.text"))
}
