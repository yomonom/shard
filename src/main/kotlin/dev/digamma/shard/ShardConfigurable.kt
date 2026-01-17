package dev.digamma.shard

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import dev.digamma.shard.ShardBundle.message
import javax.swing.DefaultComboBoxModel

class ShardConfigurable :
    BoundSearchableConfigurable(message("settings.name"), message("settings.name")) {
    private val settings
        get() = ShardSettings.getState()

    override fun createPanel() = panel {
        group(message("settings.group.splitters.title")) {
            row(message("settings.option.focusStrategy.label")) {
                comboBox(
                    DefaultComboBoxModel(ShardSettings.FocusStrategy.entries.toTypedArray()),
                    textListCellRenderer {
                        when (it) {
                            ShardSettings.FocusStrategy.LATEST -> message("settings.option.focusStrategy.value.latest")
                            ShardSettings.FocusStrategy.NEAREST -> message("settings.option.focusStrategy.value.nearest")
                            else -> null
                        }
                    }
                ).comment(message("settings.option.focusStrategy.label.comment"))
                    .bindItem(settings::focusStrategy.toNullableProperty())
            }
        }
    }
}
