package dev.digamma.shard.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.ui.tabs.JBTabsEx
import com.intellij.ui.tabs.impl.JBEditorTabs
import dev.digamma.shard.ShardBundle
import dev.digamma.shard.ex.ancestors
import dev.digamma.shard.ex.isNavigable

abstract class ShardAction(template: Template) : DumbAwareAction() {
    protected enum class State { HIDDEN, DISABLED, ENABLED }

    data class Template(
        val text: ShardBundle.Key
    )

    init {
        templatePresentation.text = ShardBundle.message(template.text)
    }

    protected abstract fun doUpdate(event: AnActionEvent): State

    protected abstract fun doPerform(event: AnActionEvent)

    final override fun update(event: AnActionEvent) {
        doUpdate(event).let {
            event.presentation.isVisible = it != State.HIDDEN
            event.presentation.isEnabled = it == State.ENABLED
        }
    }

    final override fun actionPerformed(event: AnActionEvent) {
        doPerform(event)
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    internal val AnActionEvent.editorWindow
        get() = getData(EditorWindow.DATA_KEY)

    internal val AnActionEvent.editorTabs
        get() = (getData(JBTabsEx.NAVIGATION_ACTIONS_KEY) as? JBEditorTabs)?.let { tabs ->
            if (tabs.isNavigable) tabs
            else tabs.ancestors.filterIsInstance<JBEditorTabs>().firstOrNull { it.isNavigable }
        }
}
