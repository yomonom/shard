package dev.digamma.shard.ext

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.ui.tabs.JBTabsEx
import com.intellij.ui.tabs.impl.JBEditorTabs

val AnActionEvent.editorWindow
    get() = getData(EditorWindow.DATA_KEY)

val AnActionEvent.editorTabs
    get() = (getData(JBTabsEx.NAVIGATION_ACTIONS_KEY) as? JBEditorTabs)?.let { tabs ->
        if (tabs.isNavigatable) tabs
        else tabs.ancestors.filterIsInstance<JBEditorTabs>().firstOrNull { it.isNavigatable }
    }
