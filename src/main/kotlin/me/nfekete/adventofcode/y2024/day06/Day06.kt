package me.nfekete.adventofcode.y2024.day06

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.turnRight

private data class GuardPosition(val coord: Grid2D.Coord, val heading: Grid2D.CardinalDirection)

private fun Grid2D<Char>.walk(startingPosition: GuardPosition) =
    generateSequence(startingPosition) { guardPosition ->
        val nextCoord = guardPosition.coord + guardPosition.heading.delta
        when (this[nextCoord]) {
            '#' -> guardPosition.copy(heading = guardPosition.heading.turnRight())
            '.', '^' -> guardPosition.copy(coord = nextCoord)
            else -> null
        }
    }.map { it.coord }.distinct().count()

private fun main() {
    val input = classpathFile("day06/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    input.map.entries.find { it.value == '^' }
        ?.let { (k, v) ->
            val guardPosition = GuardPosition(k, Grid2D.CardinalDirection.UP)
            println(guardPosition)

            input.walk(guardPosition).also { println("Part1: $it") }
        }
}
