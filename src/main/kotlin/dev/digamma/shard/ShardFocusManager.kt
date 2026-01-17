package dev.digamma.shard

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.fileEditor.impl.EditorWindowHolder
import dev.digamma.shard.ext.hierarchy
import java.awt.Component
import java.awt.KeyboardFocusManager
import java.util.*

object ShardFocusManager {
    private val lastFocusTime = WeakHashMap<Any, Long>()

    fun getLastFocusTime(component: Any) = lastFocusTime[component] ?: 0L

    private fun trackFocus(component: Any) {
        lastFocusTime[component] = System.currentTimeMillis()
    }

    object StartupActivity : AppLifecycleListener {
        override fun appFrameCreated(commandLineArgs: List<String?>) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener { event ->
                (event.newValue as? Component)?.hierarchy?.forEach {
                    when (it) {
                        is EditorWindowHolder -> trackFocus(it.editorWindow)
                    }
                }
            }
        }
    }
}
