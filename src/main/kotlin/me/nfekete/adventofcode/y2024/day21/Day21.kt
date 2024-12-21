package me.nfekete.adventofcode.y2024.day21

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct
import me.nfekete.adventofcode.y2024.common.from
import me.nfekete.adventofcode.y2024.common.memoized
import kotlin.math.absoluteValue

private fun Grid2D<Char>.reverseMap() = map.entries.groupBy({ it.value }) { it.key }
private val numPadLines = listOf("789", "456", "123", " 0A")
private val cursorPadLines = listOf(" ^A", "<v>")

private class Keyboard(lines: List<String>) {
    private val keypad = Grid2D.from(lines)
    private val keyPositions = keypad.reverseMap().mapValues { it.value.single() }
    private val keyDistances = keyPositions.entries.crossProduct(keyPositions.entries) { a, b ->
        a.key to b.key to (a.value - b.value)
    }.toMap()

    fun keyDistance(from: Char, to: Char) = keyDistances[to to from]!!
    fun isAllowedCursorMovement(from: Char, sequence: String) =
        sequence.scan(keyPositions[from]!!) { coord, char ->
            coord + Grid2D.CardinalDirection.from(char).delta
        }.all { keypad.map[it] != ' ' }
}

private val numPad = Keyboard(numPadLines)
private val cursors = Keyboard(cursorPadLines)

class RemoteControl(private val totalRobots: Int) {
    fun enter(code: String) = "A$code".zipWithNext { a, b -> minCostM(a, b, totalRobots) }.sum()
    private val minCostM = ::minCost.memoized()
    private fun minCost(current: Char, next: Char, robots: Int): Long {
        val keyboard = if (robots == totalRobots) numPad else cursors
        return if (robots > 0) {
            val delta = keyboard.keyDistance(current, next)
            val horizontalMoves = (if (delta.x < 0) "<" else ">").repeat(delta.x.absoluteValue.toInt())
            val verticalMoves = (if (delta.y < 0) "^" else "v").repeat(delta.y.absoluteValue.toInt())
            val bestMove = listOf(verticalMoves + horizontalMoves, horizontalMoves + verticalMoves)
                .filter { keyboard.isAllowedCursorMovement(current, it) }
                .map { s -> "A${s}A" }
                .minOf { it.zipWithNext { a, b -> minCostM(a, b, robots - 1) }.sum() }
            bestMove
        } else 1
    }
}

private fun main() {
    val input = classpathFile("day21/input.txt").readLines()

    RemoteControl(3).let { remote ->
        input.sumOf { code -> remote.enter(code) * code.removeSuffix("A").toLong() }
    }.also { println("Part 1: $it") }
    RemoteControl(26).let { remote ->
        input.sumOf { code -> remote.enter(code) * code.removeSuffix("A").toLong() }
    }.also { println("Part 2: $it") }
}
