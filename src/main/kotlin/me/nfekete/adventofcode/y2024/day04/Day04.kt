package me.nfekete.adventofcode.y2024.day04

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.Grid2D.Direction.DOWN_LEFT
import me.nfekete.adventofcode.y2024.common.Grid2D.Direction.DOWN_RIGHT
import me.nfekete.adventofcode.y2024.common.Grid2D.Direction.UP_LEFT
import me.nfekete.adventofcode.y2024.common.Grid2D.Direction.UP_RIGHT
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct
import me.nfekete.adventofcode.y2024.common.mapBoth

private operator fun List<Coord>.plus(delta: Coord) = map { it + delta }
private val fourElementSequenceCoords =
    Grid2D.Direction.entries.map { direction ->
        (0..3).map { distance ->
            direction.delta * distance.toLong()
        }
    }
private val crosses =
    (listOf(UP_LEFT, DOWN_RIGHT) crossProduct listOf(UP_RIGHT, DOWN_LEFT))
        .map { directions ->
            directions.mapBoth { direction ->
                (-1..1).map { distance ->
                    direction.delta * distance.toLong()
                }
            }
        }

private fun Grid2D<Char>.extract(positions: List<Coord>) =
    if (positions.all { it in coords }) positions.map { this[it] } else null

private val MAS = "MAS".toList()
private val XMAS = "XMAS".toList()

private fun main() {
    val input = classpathFile("day04/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    input.coords.sumOf { coord ->
        fourElementSequenceCoords.map { it + coord }
            .count { input.extract(it) == XMAS }
    }.also { println("Part1: $it") }

    input.coords.sumOf { coord ->
        crosses.count { crossLegs ->
            crossLegs.mapBoth { input.extract(it + coord) } == MAS to MAS
        }
    }.also { println("Part2: $it") }
}
