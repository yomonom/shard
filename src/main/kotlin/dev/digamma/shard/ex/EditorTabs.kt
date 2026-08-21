package dev.digamma.shard.ex

import com.intellij.ui.tabs.JBTabs
import com.intellij.ui.tabs.impl.JBEditorTabs
import com.intellij.ui.tabs.impl.TabLabel

val JBEditorTabs.isNavigable
    get() = !isHideTabs && targetInfo?.isHidden == false

val JBEditorTabs.allowsReordering
    get() = !isAlphabeticalMode()

val JBEditorTabs.visibleInfos
    get() = tabs.filterNot { it.isHidden }

val JBEditorTabs.targetLabel
    get() = targetInfo?.let { (this as JBTabs).getTabLabel(it) as? TabLabel }
