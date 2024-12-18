package me.nfekete.adventofcode.y2024.day18

import me.nfekete.adventofcode.y2024.common.Graph
import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.Monoid
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.dijkstra
import me.nfekete.adventofcode.y2024.common.intAddition
import me.nfekete.adventofcode.y2024.common.mapBoth
import me.nfekete.adventofcode.y2024.common.splitByDelimiter

private class Memory(val start: Coord = Coord.zero, val end: Coord, val grid: Grid2D<Boolean>) : Graph<Coord, Int> {
    override val vertices: Set<Coord> =
        (start.x..end.x).flatMap { x -> (start.y..end.y).map { y -> Coord(x, y) } }.toSet() - grid.coords

    override fun Coord.neighborsWithCost() = axisNeighbors.filter { it in vertices }.associateWith { 1 }

}

private fun main() {
    val bytes = 1024
    val end = Coord(70, 70)
    val input = classpathFile("day18/input.txt")
        .readLines()
        .map { line -> line.splitByDelimiter(",").mapBoth { it.toInt() } }

    val grid = input.take(bytes).map { (x, y) -> Coord(x, y) }.associateWith { true }.let { Grid2D(it) }
//    println(grid.pretty('.') { if (it) '#' else '.' } )
    val memory = Memory(Coord(0, 0), end, grid)
    val shortestPaths = with(Monoid.intAddition()) { memory.dijkstra(memory.start) }
    shortestPaths.get(memory.end)?.also { println("Part1: ${it.cost}") }

}
