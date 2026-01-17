package dev.digamma.shard.ext

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.impl.EditorWindow

val AnActionEvent.editorWindow
    get() = getData(EditorWindow.DATA_KEY)
