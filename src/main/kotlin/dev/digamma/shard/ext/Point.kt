package dev.digamma.shard.ext

import java.awt.Point

operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)

operator fun Point.plusAssign(other: Point) {
    x += other.x
    y += other.y
}

operator fun Point.minus(other: Point) = Point(x - other.x, y - other.y)

operator fun Point.minusAssign(other: Point) {
    x -= other.x
    y -= other.y
}
