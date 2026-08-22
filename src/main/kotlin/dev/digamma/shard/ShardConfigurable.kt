package dev.digamma.shard

import com.intellij.ide.DataManager
import com.intellij.openapi.keymap.impl.ui.KeymapPanel
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.options.ex.Settings
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import dev.digamma.shard.ShardBundle.message
import java.awt.Component

class ShardConfigurable : BoundSearchableConfigurable(message("settings.name"), message("settings.name")) {
    private val settings
        get() = ShardSettings.getState()

    override fun createPanel() = panel {
        group(message("settings.tabs.title")) {
            row {
                checkBox(message("settings.prevent.closing.pinned.tabs.label"))
                    .bindSelected(settings::preventClosingPinnedTabs)
            }

            row {
                checkBox(message("settings.split.on.move.label"))
                    .comment(message("settings.split.on.move.description"))
                    .bindSelected(settings::splitOnMove)
            }
        }

        group(message("settings.splitters.title")) {
            row(message("settings.focus.strategy.label")) {
                comboBox(ShardSettings.FocusStrategy.entries, textListCellRenderer { it?.text })
                    .bindItem(settings::focusStrategy.toNullableProperty())
                    .apply {
                        comment(component.item.description)
                        component.addItemListener { comment?.text = component.item.description }
                    }
            }
        }

        group(message("settings.shortcuts.title")) {
            row {
                button(message("settings.configure.shortcuts.label")) {
                    openKeymapPanel(it.source as? Component)
                }
            }
        }
    }

    private fun openKeymapPanel(component: Component?) {
        val settings = DataManager.getInstance().getDataContext(component).getData(Settings.KEY) ?: return
        val panel = settings.find(KeymapPanel::class.java) ?: return

        settings.select(panel).doWhenDone {
            panel.selectAction(ShardActionManager.PREFIX)
        }
    }
}
