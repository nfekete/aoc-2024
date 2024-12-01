package me.nfekete.adventofcode.y2024.common

import kotlin.math.abs

data class Grid2D<T>(val map: Map<Coord, T>) {
    data class Coord(val x: Long, val y: Long) : Comparable<Coord> {
        constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

        val neighbors
            get() =
                (-1..1).flatMap { y -> (-1..1).map { x -> Coord(x, y) } }
                    .filter { it != zero }
                    .map { this + it }
                    .toSet()
        val axisNeighbors
            get() =
                (-1..1).flatMap { y -> (-1..1).map { x -> Coord(x, y) } }
                    .filter { (x, y) -> (x == 0L) xor (y == 0L) }
                    .map { this + it }
                    .toSet()

        operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)
        operator fun minus(other: Coord) = Coord(x - other.x, y - other.y)
        operator fun times(factor: Long) = Coord(factor * x, factor * y)
        operator fun rem(divisor: Coord) = Coord(x % divisor.x, y % divisor.y)
        operator fun div(divisor: Coord) = Coord(x / divisor.x, y / divisor.y)
        fun mapX(f: (Long) -> Long) = Coord(f(x), y)
        fun mapY(f: (Long) -> Long) = Coord(x, f(y))
        val manhattan get() = abs(x) + abs(y)
        override fun compareTo(other: Coord) =
            y.compareTo(other.y).takeIf { it != 0 } ?: x.compareTo(other.x)

        companion object {
            val zero = Coord(0, 0)
        }
    }

    val coords get() = map.keys
    val yRange = coords.minOf { it.y }..coords.maxOf { it.y }
    val xRange = coords.minOf { it.x }..coords.maxOf { it.x }
    val dimension get() = Coord(xRange.length, yRange.length)
    operator fun get(coord: Coord) = map[coord]
    fun subGrid(projectionCoords: Set<Coord>) = map.filter { (key, _) -> key in projectionCoords }.toMap().let(::Grid2D)

    fun pretty(emptyValue: Char = ' ', displayTransform: (T) -> Char) =
        yRange.joinToString("\n") { y ->
            xRange.map { x -> this[Coord(x, y)]?.let(displayTransform) ?: emptyValue }.joinToString("")
        }

    companion object {
        fun from(lines: List<String>, acceptFunction: (char: Char) -> Boolean = { true }) =
            lines.flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, char -> if (acceptFunction(char)) Coord(x, y) to char else null }
            }.toMap().let { Grid2D(it) }
    }

    enum class CardinalDirection(val delta: Coord) {
        UP(Coord(0, -1)),
        DOWN(Coord(0, 1)),
        LEFT(Coord(-1, 0)),
        RIGHT(Coord(1, 0)),
    }
}

fun Grid2D<Char>.pretty(emptyValue: Char = '.') = pretty(emptyValue) { it }
