package dev.digamma.shard

import com.intellij.openapi.components.*

@Service(Service.Level.APP)
@State(name = "ShardSettings", category = SettingsCategory.TOOLS, storages = [Storage("shard.xml")])
class ShardSettings : SimplePersistentStateComponent<ShardSettings.State>(State()) {
    class State : BaseState() {
        var preventClosingPinnedTabs by property(true)
        var splitOnMove by property(true)
        var focusStrategy by enum(FocusStrategy.LATEST)
    }

    companion object {
        fun getState() = service<ShardSettings>().state
    }

    enum class FocusStrategy(text: ShardBundle.Key, description: ShardBundle.Key) {
        LATEST("settings.focus.strategy.latest.text", "settings.focus.strategy.latest.description"),
        NEAREST("settings.focus.strategy.nearest.text", "settings.focus.strategy.nearest.description");

        val text = ShardBundle.message(text)
        val description = ShardBundle.message(description)
    }
}
