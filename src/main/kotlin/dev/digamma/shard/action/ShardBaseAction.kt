package dev.digamma.shard.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import dev.digamma.shard.ShardBundle
import org.jetbrains.annotations.PropertyKey

abstract class ShardBaseAction(template: Template) : DumbAwareAction() {
    protected enum class State { HIDDEN, DISABLED, ENABLED }

    data class Template(
        val text: @PropertyKey(resourceBundle = ShardBundle.BUNDLE) String
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

    final override fun getActionUpdateThread() = ActionUpdateThread.EDT
}
