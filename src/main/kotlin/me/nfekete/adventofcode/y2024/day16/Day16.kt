package me.nfekete.adventofcode.y2024.day16

import me.nfekete.adventofcode.y2024.common.Graph
import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.RIGHT
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.Monoid
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.dijkstraCapturingAllShortestPaths
import me.nfekete.adventofcode.y2024.common.longAddition
import me.nfekete.adventofcode.y2024.common.turnLeft
import me.nfekete.adventofcode.y2024.common.turnRight
import kotlin.time.measureTime

private data class Node(val coord: Coord, val direction: CardinalDirection)

private class Maze(private val grid: Grid2D<Char>) : Graph<Node, Long> {
    override val vertices = grid.map.entries
        .filter { (_, char) -> char != '#' }
        .map { it.key }
        .flatMap { coord -> CardinalDirection.entries.map { Node(coord, it) } }
        .toSet()

    private val Char?.canMoveTo get() = this == '.' || this == 'S' || this == 'E'

    override fun Node.neighborsWithCost(): Map<Node, Long> {
        val noTurnNeighbor = listOfNotNull(
            (coord + direction.delta).takeIf { neighbor -> grid.map[neighbor].canMoveTo }
                ?.let { copy(coord = it) to 1L }
        )
        val turns = listOf(direction.turnRight(), direction.turnLeft())
            .map { direction -> copy(direction = direction) to 1000L }
        return (turns + noTurnNeighbor).toMap()
    }

    fun bothParts() =
        grid.map.entries.first { (_, char) -> char == 'S' }.key.let { startCoord ->
            val start = Node(startCoord, RIGHT)
            val endCoord = grid.map.entries.first { (_, char) -> char == 'E' }.key
            val ends = CardinalDirection.entries.map { direction -> Node(endCoord, direction) }
            val shortestPaths = with(Monoid.longAddition()) { dijkstraCapturingAllShortestPaths(start) }
            val shortestPathsToEnd = ends.flatMap { shortestPaths[it] }
            val minCost = shortestPathsToEnd.minOf { it.cost }
            val pathsHavingMinimalCost = shortestPathsToEnd
                .filter { it.cost == minCost }
            val numberCoordsInTheUnionOfShortestPaths = pathsHavingMinimalCost
                .map { pathWithCost -> pathWithCost.path }
                .map { path -> path.map { it.coord } }
                .fold(emptySet<Coord>()) { acc, nodes -> acc union nodes }
                .size
            minCost to numberCoordsInTheUnionOfShortestPaths
        }
}

private fun main() {
    measureTime {
        val grid = classpathFile("day16/input.txt").readLines().let { Grid2D.from(it) }

        val graph = Maze(grid)
        graph.bothParts().also {
            println("Part1: ${it.first}")
            println("Part2: ${it.second}")
        }
    }.also { println("Took: $it") }
}
