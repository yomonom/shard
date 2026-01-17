package dev.digamma.shard.ext

import com.intellij.openapi.ui.Splitter
import javax.swing.JComponent

val Splitter.children
    get() = sequence {
        firstComponent?.let { yield(it) }
        secondComponent?.let { yield(it) }
    }

fun Splitter.replace(oldComponent: JComponent, newComponent: JComponent) {
    if (firstComponent === oldComponent) this.firstComponent = newComponent
    else if (secondComponent === oldComponent) this.secondComponent = newComponent
}
