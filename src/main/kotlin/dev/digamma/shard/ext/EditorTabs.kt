package dev.digamma.shard.ext

import com.intellij.ui.tabs.impl.JBEditorTabs

val JBEditorTabs.isNavigatable
    get() = !isHideTabs && targetInfo?.isHidden == false

val JBEditorTabs.allowsReordering
    get() = !isAlphabeticalMode()
