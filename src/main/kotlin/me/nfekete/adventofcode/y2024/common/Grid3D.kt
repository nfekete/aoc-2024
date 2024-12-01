package me.nfekete.adventofcode.y2024.common

import kotlin.math.abs

data class Grid3D<T>(val map: Map<Coord, T>) {
    data class Coord(val x: Long, val y: Long, val z: Long) : Comparable<Coord> {
        constructor(x: Int, y: Int, z: Int) : this(x.toLong(), y.toLong(), z.toLong())

        val neighbors
            get() = (-1..1).flatMap { z ->
                (-1..1).flatMap { y -> (-1..1).map { x -> Coord(x, y, z) } }
            }
                .filter { it != zero }
                .map { this + it }
                .toSet()
        val axisNeighbors
            get() = (-1..1).flatMap { z ->
                (-1..1).flatMap { y -> (-1..1).map { x -> Coord(x, y, z) } }
            }
                .filter { (x, y) -> (x == 0L) xor (y == 0L) xor (z == 0L) }
                .map { this + it }
                .toSet()


        operator fun plus(other: Coord) = Coord(x + other.x, y + other.y, z + other.z)
        operator fun minus(other: Coord) = Coord(x - other.x, y - other.y, z - other.z)
        operator fun times(factor: Long) = Coord(factor * x, factor * y, factor * z)
        operator fun rem(divisor: Coord) = Coord(x % divisor.x, y % divisor.y, z % divisor.z)
        operator fun div(divisor: Coord) = Coord(x / divisor.x, y / divisor.y, z / divisor.z)
        fun mapX(f: (Long) -> Long) = Coord(f(x), y, z)
        fun mapY(f: (Long) -> Long) = Coord(x, f(y), z)
        fun mapZ(f: (Long) -> Long) = Coord(x, y, f(z))
        val manhattan get() = abs(x) + abs(y) + abs(z)
        override fun compareTo(other: Coord) =
            z.compareTo(other.z).takeIf { it != 0 } ?: y.compareTo(other.y).takeIf { it != 0 } ?: x.compareTo(other.x)

        companion object {
            val zero = Coord(0, 0, 0)
        }

        enum class CardinalDirection(val delta: Coord) {
            UP(Coord(0, 0, 1)),
            DOWN(Coord(0, 0, -1)),
            LEFT(Coord(-1, 0, 0)),
            RIGHT(Coord(1, 0, 0)),
            FORWARD(Coord(0, 1, 0)),
            BACKWARD(Coord(0, -1, 0)),
        }
    }

    val coords get() = map.keys
    val zRange = coords.minOf { it.z }..coords.maxOf { it.z }
    val yRange = coords.minOf { it.y }..coords.maxOf { it.y }
    val xRange = coords.minOf { it.x }..coords.maxOf { it.x }
    val dimension get() = Coord(xRange.length, yRange.length, zRange.length)
    operator fun get(coord: Coord) = map[coord]
}
