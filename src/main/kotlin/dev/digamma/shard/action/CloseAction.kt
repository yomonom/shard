package dev.digamma.shard.action

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.CloseAction.CloseTarget
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.tabs.impl.JBEditorTabs
import dev.digamma.shard.ShardSettings

class CloseAction : ShardAction(template = Template("action.close.text")) {
    override fun doUpdate(event: AnActionEvent): State {
        event.presentation.icon = if (event.isFromActionToolbar) AllIcons.Actions.Close else null

        return event.closeTarget.let {
            when {
                it == null -> State.HIDDEN
                preventClose(it) -> State.DISABLED
                else -> State.ENABLED
            }
        }
    }

    override fun doPerform(event: AnActionEvent) {
        event.closeTarget?.takeUnless(::preventClose)?.close()
    }

    private fun preventClose(target: CloseTarget) = when (target) {
        is JBEditorTabs -> target.targetInfo?.isPinned == true && ShardSettings.getState().preventClosingPinnedTabs
        else -> false
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    internal val AnActionEvent.closeTarget
        get() = getData(CloseTarget.KEY)
}
