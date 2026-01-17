package dev.digamma.shard.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.advanced.AdvancedSettings
import dev.digamma.shard.ext.allowsReordering
import dev.digamma.shard.ext.editorTabs
import java.util.Collections.rotate

@Suppress("UnstableApiUsage")
class MoveTabAction(private val direction: Direction, template: Template) : ShardBaseAction(template) {
    enum class Direction { BACKWARD, FORWARD, START, END }

    private val groupPinnedTabs
        get() = AdvancedSettings.getBoolean("editor.keep.pinned.tabs.on.left")

    override fun doUpdate(event: AnActionEvent): State {
        val tabs = event.editorTabs?.takeIf { it.allowsReordering } ?: return State.HIDDEN
        val source = tabs.targetInfo!!
        val target = tabs.getVisibleInfos().run {
            // Ensure that the current tab is not the first one in the direction of movement
            when (direction) {
                Direction.BACKWARD, Direction.START -> indexOf(source).dec().takeUnless { it < 0 }
                Direction.FORWARD, Direction.END -> indexOf(source).inc().takeUnless { it > lastIndex }
            }?.let(::get)
        }

        // Ensure that tabs cannot be moved between different groups
        // (Both the source and target tabs must be either pinned or unpinned)
        return if (target != null && (!groupPinnedTabs || target.isPinned == source.isPinned)) State.ENABLED
        else State.DISABLED
    }

    override fun doPerform(event: AnActionEvent) {
        val tabs = event.editorTabs ?: return
        val source = tabs.targetInfo!!

        with(tabs.getVisibleInfos() as? MutableList ?: return) {
            val index = indexOf(source)

            when (direction) {
                // Swap the current tab with the one to its left
                Direction.BACKWARD -> set(index, set(index - 1, source))

                // Swap the current tab with the one to its right
                Direction.FORWARD -> set(index, set(index + 1, source))

                // Rotate the current group of tabs to the left
                Direction.START -> rotate(
                    subList(if (groupPinnedTabs && !source.isPinned) indexOfFirst { !it.isPinned } else 0, index + 1),
                    1
                )

                // Rotate the current group of tabs to the right
                Direction.END -> rotate(
                    subList(index, if (groupPinnedTabs && source.isPinned) indexOfLast { it.isPinned } + 1 else size),
                    -1
                )
            }
        }

        tabs.resetTabsCache()
        tabs.revalidateAndRepaint(false)
    }

    @Suppress("CompanionObjectInExtension")
    companion object {
        val BACKWARD = MoveTabAction(Direction.BACKWARD, Template("action.move.tab.backward.text"))
        val FORWARD = MoveTabAction(Direction.FORWARD, Template("action.move.tab.forward.text"))
        val START = MoveTabAction(Direction.START, Template("action.move.tab.start.text"))
        val END = MoveTabAction(Direction.END, Template("action.move.tab.end.text"))
    }
}
