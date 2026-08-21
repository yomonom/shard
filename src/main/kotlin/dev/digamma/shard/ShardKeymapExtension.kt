package dev.digamma.shard

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.keymap.KeymapExtension
import com.intellij.openapi.keymap.KeymapGroup
import com.intellij.openapi.keymap.impl.ui.ActionsTreeUtil
import com.intellij.openapi.keymap.impl.ui.Group
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import dev.digamma.shard.ShardBundle.message
import dev.digamma.shard.util.PLUGIN_ID

class ShardKeymapExtension : KeymapExtension {
    override fun createGroup(filtered: Condition<in AnAction>, project: Project?): KeymapGroup {
        val manager = ActionManagerEx.getInstanceEx()
        val group = Group(message("settings.name"), ShardActionManager.PREFIX)

        manager.getPluginActions(PLUGIN_ID)
            .map(manager::getAction)
            .sortedBy { it.templateText }
            .forEach { ActionsTreeUtil.addAction(group, it, filtered) }

        return group
    }

    override fun skipPluginGroup(id: PluginId) = id == PLUGIN_ID
}
