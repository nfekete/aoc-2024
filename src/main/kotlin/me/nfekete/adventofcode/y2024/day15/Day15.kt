package me.nfekete.adventofcode.y2024.day15

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.CardinalDirection
import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.takeWhileInclusive
import kotlin.test.fail

private fun Char.toCardinalDirection() =
    when (this) {
        '^' -> CardinalDirection.UP
        '>' -> CardinalDirection.RIGHT
        'v' -> CardinalDirection.DOWN
        '<' -> CardinalDirection.LEFT
        else -> fail()
    }

private val Char?.moveable get() =
    this == '@' || this == 'O'

private fun Grid2D<Char>.move(instruction: CardinalDirection) =
    map.entries.first { (_, char) -> char == '@' }.let { (position) ->
        val toMove = generateSequence(position) { coord -> coord + instruction.delta }
            .takeWhileInclusive { map[it].moveable }.toList()
        if (map[toMove.last()] == '#')
            this
        else
            Grid2D(map - toMove.first() + (toMove.dropLast(1).map { (it + instruction.delta) to map.getValue(it) }))
    }

private fun Grid2D<Char>.simulate(instructions: List<CardinalDirection>) =
    instructions.fold(this) { acc, direction -> acc.move(direction) }

private fun Grid2D<Char>.gps() =
    map.entries.filter { (_, char) -> char == 'O' }.sumOf { (coord, char) -> coord.y * 100 + coord.x }

private fun main() {
    classpathFile("day15/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .toList()
        .let { (gridLines, instructionLines) ->
            val grid = Grid2D.from(gridLines)
            val instructions = instructionLines.flatMap { it.toList() }.map { it.toCardinalDirection() }

            grid.simulate(instructions).gps().also { println("Part1: $it") }
        }
}
