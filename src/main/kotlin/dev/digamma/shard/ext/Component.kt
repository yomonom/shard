package dev.digamma.shard.ext

import dev.digamma.shard.util.Side
import java.awt.Component
import java.awt.Dimension
import java.awt.Point

val Component.hierarchy
    get() = generateSequence(this) { it.parent }

fun Component.touches(point: Point, dimension: Dimension, side: Side): Boolean {
    val dx = point.x - x
    val dy = point.y - y

    return when (side) {
        Side.LEFT -> dx in 0..width && dy in -dimension.height..height
        Side.TOP -> dx in -dimension.width..width && dy in 0..height
        Side.RIGHT -> dx + dimension.width in 0..width && dy in -dimension.height..height
        Side.BOTTOM -> dx in -dimension.width..width && dy + dimension.height in 0..height
    }
}
