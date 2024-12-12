package me.nfekete.adventofcode.y2024.day12

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.DOWN
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.LEFT
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.RIGHT
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.UP
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct
import me.nfekete.adventofcode.y2024.common.mapBoth

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

context(Grid2D<Char>)
private fun Set<Coord>.detectCorners() =
    sumOf { coord ->
        (listOf(LEFT, RIGHT) crossProduct listOf(UP, DOWN))
            .map { direction -> direction.mapBoth { it.delta } }
            .count { (dx, dy) ->
                map[coord + dx] == map[coord] &&
                        map[coord + dy] == map[coord] &&
                        map[coord + dx + dy] != map[coord] ||
                        map[coord + dx] != map[coord] &&
                        map[coord + dy] != map[coord]
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
        get() = coords.detectCorners()
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
