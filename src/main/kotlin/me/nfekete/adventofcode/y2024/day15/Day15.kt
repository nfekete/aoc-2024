@file:Suppress("ConvertArgumentToSet")

package me.nfekete.adventofcode.y2024.day15

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.DOWN
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection.UP
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import kotlin.test.fail

private fun Char.toCardinalDirection() =
    when (this) {
        '^' -> UP
        '>' -> CardinalDirection.RIGHT
        'v' -> DOWN
        '<' -> CardinalDirection.LEFT
        else -> fail()
    }

private val Char?.moveable get() =
    this == '@' || this == 'O' || this == '[' || this == ']'

private fun Grid2D<Char>.move(direction: CardinalDirection) =
    map.entries.first { (_, char) -> char == '@' }.let { (position) ->
        val toMove = generateSequence(position) { coord -> coord + direction.delta }
            .takeWhile { map[it].moveable }.toList()
        if (map[toMove.last() + direction.delta] == '#')
            this
        else
            Grid2D(map - toMove + (toMove.map { (it + direction.delta) to map.getValue(it) }))
    }

private fun Grid2D<Char>.moveWidegrid(direction: CardinalDirection) =
    map.entries.first { (_, char) -> char == '@' }.let { (position) ->
        val toMove = generateSequence(listOf(position)) { coords ->
            coords.flatMap { coord -> coord.extendTo(direction) }.distinct()
        }.takeWhile { coords -> coords.any { map[it].moveable } }.toList()
        if (toMove.any { line -> line.any { coord -> map[coord + direction.delta] == '#' } })
            this
        else
            Grid2D(
                map - toMove.flatten() +
                        (toMove.flatMap { coords ->
                            coords.map { (it + direction.delta) to map.getValue(it) }
                        })
            )
    }

context(Grid2D<Char>)
private fun Coord.extendTo(direction: CardinalDirection) =
    (this+direction.delta).run {
        if (direction == UP || direction == DOWN)
            when (map[this]) {
                '[' -> listOf(this, mapX { x -> x + 1 })
                ']' -> listOf(mapX { x -> x - 1 }, this)
                else -> emptyList()
            }
        else
            when (map[this]) {
                '[', ']' -> listOf(this)
                else -> emptyList()
            }
    }

private fun Grid2D<Char>.simulate(instructions: List<CardinalDirection>) =
    instructions.fold(this) { acc, direction -> acc.move(direction) }

private fun Grid2D<Char>.simulateWideGrid(instructions: List<CardinalDirection>) =
    instructions.fold(this) { acc, direction -> acc.moveWidegrid(direction) }

private fun Grid2D<Char>.gps() =
    map.entries.filter { (_, char) -> char == 'O' || char == '[' }.sumOf { (coord, _) -> coord.y * 100 + coord.x }

private fun List<String>.toWideGrid() =
    map { line ->
        line.flatMap { char ->
            when (char) {
                'O' -> "[]".toList()
                '@' -> "@.".toList()
                else -> listOf(char, char)
            }
        }.joinToString("")
    }.let { Grid2D.from(it) }

private fun main() {
    classpathFile("day15/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .toList()
        .let { (gridLines, instructionLines) ->
            val grid = Grid2D.from(gridLines)
            val instructions = instructionLines.flatMap { it.toList() }.map { it.toCardinalDirection() }

            grid.simulate(instructions).gps().also { println("Part1: $it") }
            val wideGrid = gridLines.toWideGrid()
            wideGrid.simulateWideGrid(instructions)
//                .also { println(it.pretty()) }
                .gps().also { println("Part2: $it")}
        }
}
