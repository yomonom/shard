package dev.digamma.shard

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.application.WriteIntentReadAction
import com.intellij.openapi.fileEditor.impl.EditorWindowHolder
import com.intellij.ui.tabs.impl.JBEditorTabs
import dev.digamma.shard.ex.hierarchy
import java.awt.Component
import java.awt.KeyboardFocusManager
import java.util.*

object ShardFocusManager {
    private val lastFocusTime = WeakHashMap<Any, Long>()

    fun getLastFocusTime(component: Any) = lastFocusTime[component] ?: 0L

    private fun trackFocus(component: Any) {
        lastFocusTime[component] = System.currentTimeMillis()
    }

    private fun processComponent(component: Component) {
        if (component is EditorWindowHolder) trackFocus(component.editorWindow)
        if (component is JBEditorTabs) {
            WriteIntentReadAction.run {
                val changed = component.targetInfo?.let(component::getTabLabel)?.updateTabActions()
                if (changed == true) component.revalidateAndRepaint(false)
            }
        }
    }

    object StartupActivity : AppLifecycleListener {
        override fun appFrameCreated(commandLineArgs: List<String?>) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener {
                (it.newValue as? Component)?.hierarchy?.forEach(::processComponent)
            }
        }
    }
}
