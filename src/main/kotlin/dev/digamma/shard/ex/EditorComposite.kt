package dev.digamma.shard.ex

import com.intellij.openapi.fileEditor.FileEditorManagerKeys
import com.intellij.openapi.fileEditor.impl.EditorComposite

val EditorComposite.isSplittable
    get() = file.getUserData(FileEditorManagerKeys.FORBID_TAB_SPLIT) != true
