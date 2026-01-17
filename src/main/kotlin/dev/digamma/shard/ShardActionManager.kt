package dev.digamma.shard

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.extensions.PluginId
import dev.digamma.shard.action.FocusSplitterAction
import dev.digamma.shard.action.MoveSplitterAction
import dev.digamma.shard.action.ShardBaseAction
import org.intellij.lang.annotations.Language

object ShardActionManager {
    private fun registerAction(id: String, action: ShardBaseAction) {
        @Suppress("UnresolvedPluginConfigReference")
        ActionManager.getInstance().registerAction("Shard.$id", action, PluginId.getId("dev.digamma.shard"))
    }

    private fun replaceAction(@Language("devkit-action-id") id: String, action: ShardBaseAction) {
        ActionManager.getInstance().replaceAction(id, action)
    }

    object StartupActivity : AppLifecycleListener {
        override fun appFrameCreated(commandLineArgs: MutableList<String>) {
            registerAction("FocusSplitter.Left", FocusSplitterAction.LEFT)
            registerAction("FocusSplitter.Top", FocusSplitterAction.TOP)
            registerAction("FocusSplitter.Right", FocusSplitterAction.RIGHT)
            registerAction("FocusSplitter.Bottom", FocusSplitterAction.BOTTOM)

            registerAction("MoveSplitter.Left", MoveSplitterAction.LEFT)
            registerAction("MoveSplitter.Top", MoveSplitterAction.TOP)
            registerAction("MoveSplitter.Right", MoveSplitterAction.RIGHT)
            registerAction("MoveSplitter.Bottom", MoveSplitterAction.BOTTOM)
        }
    }
}
