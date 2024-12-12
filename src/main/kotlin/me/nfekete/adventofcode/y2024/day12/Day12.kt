package me.nfekete.adventofcode.y2024.day12

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile

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

private data class Area(val plant: Char, val coords: Set<Coord>) {
    context(Grid2D<Char>)
    val perimeter get() = coords.perimeter()
    val area get() = coords.size
    context(Grid2D<Char>)
    val price get() = area * perimeter
}

private fun Grid2D<Char>.fencingPrice(): Int {
    val remainingCoords = coords.toMutableSet()
    val areas = mutableListOf<Area>()
    while (remainingCoords.isNotEmpty()) {
        val coord = remainingCoords.first()
        val area = coord.detectArea()
        remainingCoords -= area
        areas.add(Area(map.getValue(coord), area))
    }
    return areas.sumOf { it.price }
}

private fun main() {
    val grid = classpathFile("day12/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    grid.fencingPrice().also { println("Part1: $it") }
}
