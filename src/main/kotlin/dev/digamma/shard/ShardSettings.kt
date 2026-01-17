package dev.digamma.shard

import com.intellij.openapi.components.*

@Service(Service.Level.APP)
@State(name = "ShardSettings", category = SettingsCategory.UI, storages = [Storage("shard.xml")])
class ShardSettings : SimplePersistentStateComponent<ShardSettings.State>(State()) {
    enum class FocusStrategy { LATEST, NEAREST }

    class State : BaseState() {
        var splitOnMove by property(true)
        var focusStrategy by enum(FocusStrategy.LATEST)
    }

    companion object {
        fun getState() = service<ShardSettings>().state
    }
}
