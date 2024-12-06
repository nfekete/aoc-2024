package me.nfekete.adventofcode.y2024.day06

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.detectCycle
import me.nfekete.adventofcode.y2024.common.turnRight

private data class GuardPosition(val coord: Grid2D.Coord, val heading: Grid2D.CardinalDirection)

private fun Grid2D<Char>.nextPosition(guardPosition: GuardPosition): GuardPosition? {
    val nextCoord = guardPosition.coord + guardPosition.heading.delta
    return when (this[nextCoord]) {
        '#' -> guardPosition.copy(heading = guardPosition.heading.turnRight())
        '.', '^' -> guardPosition.copy(coord = nextCoord)
        else -> null
    }
}

private fun Grid2D<Char>.walk(startingPosition: GuardPosition) =
    generateSequence(startingPosition) { guardPosition ->
        nextPosition(guardPosition)
    }

private fun Grid2D<Char>.countLoopCreatingObstacleCoords(startingPosition: GuardPosition) =
    walk(startingPosition)
        .map { it.coord }.distinct().let { obstacleCandidateCoords ->
            obstacleCandidateCoords.count { obstacleCandidateCoord ->
                this.copy(map = this.map + (obstacleCandidateCoord to '#'))
                    .walk(startingPosition)
                    .detectCycle() != null
            }
        }

private fun main() {
    val input = classpathFile("day06/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    input.map.entries.find { it.value == '^' }
        ?.let { (k, v) ->
            val guardPosition = GuardPosition(k, Grid2D.CardinalDirection.UP)

            input.walk(guardPosition).map { it.coord }.distinct().count().also { println("Part1: $it") }
            input.countLoopCreatingObstacleCoords(guardPosition).also { println("Part2: $it") }
        }
}
