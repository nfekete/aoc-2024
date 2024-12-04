package me.nfekete.adventofcode.y2024.day04

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile

private operator fun List<Coord>.plus(delta: Coord) = map { it + delta }
private val fourElementSequenceCoords =
    Grid2D.Direction.entries.map { direction ->
        (0..3).map { distance ->
            direction.delta * distance.toLong()
        }
    }

private fun Grid2D<Char>.extract(positions: List<Coord>) =
    if (positions.all { it in coords }) positions.map { this[it] } else null

private fun main() {
    val input = classpathFile("day04/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    input.coords.sumOf { coord ->
        fourElementSequenceCoords.map { it + coord }
            .mapNotNull { input.extract(it)?.joinToString("") }
            .count { it == "XMAS" }
    }.also { println("Part1: $it") }
}
