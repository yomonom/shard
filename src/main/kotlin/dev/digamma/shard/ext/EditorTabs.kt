package dev.digamma.shard.ext

import com.intellij.ui.tabs.impl.JBEditorTabs

@Suppress("UnstableApiUsage")
val JBEditorTabs.isNavigable
    get() = !isHideTabs && targetInfo?.isHidden == false

val JBEditorTabs.allowsReordering
    get() = !isAlphabeticalMode()
