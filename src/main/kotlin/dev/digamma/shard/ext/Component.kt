package dev.digamma.shard.ext

import java.awt.Component

val Component.hierarchy
    get() = generateSequence(this) { it.parent }
