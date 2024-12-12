package me.nfekete.adventofcode.y2024.day12

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.DOWN
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.LEFT
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.RIGHT
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.UP
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.Grid2D.ObliqueDirection.DOWN_RIGHT
import me.nfekete.adventofcode.y2024.common.Grid2D.ObliqueDirection.UP_RIGHT
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.day12.Axis.HORIZONTAL
import me.nfekete.adventofcode.y2024.day12.Axis.VERTICAL

context(Grid2D<Char>)
private fun Set<Coord>.perimeter() =
    sumOf { coord ->
        coord.axisNeighbors.count { map[it] != map[coord] }
    }

context(Grid2D<Char>)
private fun Coord.detectArea(visited: MutableSet<Coord> = mutableSetOf()): Set<Coord> =
    if (this !in coords || this in visited)
        emptySet()
    else {
        visited.add(this)
        axisNeighbors.filter { it in coords && map[it] == map[this] && it !in visited }
            .forEach { it.detectArea(visited) }
        visited
    }

private enum class Axis { HORIZONTAL, VERTICAL }
private data class Wall(val coord: Coord, val axis: Axis)

context(Grid2D<Char>)
private fun Set<Coord>.detectSides() =
    flatMap { coord ->
        Grid2D.CardinalDirection.entries.mapNotNull { direction ->
            val wall = when (direction) {
                UP -> Wall(coord, HORIZONTAL)
                DOWN -> Wall(coord + DOWN.delta, HORIZONTAL)
                LEFT -> Wall(coord, VERTICAL)
                RIGHT -> Wall(coord + RIGHT.delta, VERTICAL)
            }
            wall.takeIf { map[coord + direction.delta] != map[coord] }
        }
    }.let { walls ->
        walls.map { wall ->
            when (wall.axis) {
                HORIZONTAL -> HORIZONTAL to wall.coord.y to wall.coord.x
                VERTICAL -> VERTICAL to wall.coord.x to wall.coord.y
            }
        }.groupBy({ it.first }) { it.second }.let { map ->
            val sides = map.mapValues { (key, positions) ->
                val sorted = positions.sorted()
                1 + sorted.windowed(2).count { (a, b) -> b - a != 1L }
            }.values.sum()
            val compensation = 2 * count { coord ->
                (coord + DOWN.delta) !in this &&
                        (coord + RIGHT.delta) !in this &&
                        (coord + DOWN_RIGHT.delta) in this ||
                        (coord + UP.delta) !in this &&
                        (coord + RIGHT.delta) !in this &&
                        (coord + UP_RIGHT.delta) in this
            }
            sides + compensation
        }
    }

private data class Area(val plant: Char, val coords: Set<Coord>) {
    context(Grid2D<Char>)
    val perimeter
        get() = coords.perimeter()
    val area get() = coords.size
    context(Grid2D<Char>)
    val wallPrice
        get() = area * perimeter
    context(Grid2D<Char>)
    val sides
        get() = coords.detectSides()
    context(Grid2D<Char>)
    val sidePrice
        get() = area * sides
}

private fun Grid2D<Char>.fencingPrice(priceExtractor: (Area) -> Int): Int =
    areas().sumOf(priceExtractor)

private fun Grid2D<Char>.areas(): MutableList<Area> {
    val remainingCoords = coords.toMutableSet()
    val areas = mutableListOf<Area>()
    while (remainingCoords.isNotEmpty()) {
        val coord = remainingCoords.first()
        val area = coord.detectArea()
        remainingCoords -= area
        areas.add(Area(map.getValue(coord), area))
    }
    return areas
}

private fun main() {
    val grid = classpathFile("day12/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    with(grid) {
        fencingPrice { it.wallPrice }.also { println("Part1: $it") }
        fencingPrice { it.sidePrice }.also { println("Part2: $it") } // 886378 not correct
    }
}
