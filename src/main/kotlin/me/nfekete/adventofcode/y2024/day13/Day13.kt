package me.nfekete.adventofcode.y2024.day13

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile

private data class Button(val id: String, val x: Long, val y: Long, val cost: Long) {
    companion object {
        private val regex = "Button ([AB]): X\\+(\\d+), Y\\+(\\d+)".toRegex()
        fun parse(line: String): Button {
            val (letter, x, y) = regex.matchEntire(line)!!.destructured
            return Button(letter, x.toLong(), y.toLong(), if (letter == "A") 3 else 1)
        }
    }
}
private data class ClawMachine(val buttonA: Button, val buttonB: Button, val prizeX: Long, val prizeY: Long) {
    companion object {
        private val regex = "Prize: X=(\\d+), Y=(\\d+)".toRegex()
        fun parse(lines: List<String>): ClawMachine {
            val (prizeX, prizeY) = regex.matchEntire(lines[2])!!.destructured
            return ClawMachine(Button.parse(lines[0]), Button.parse(lines[1]), prizeX.toLong(), prizeY.toLong())
        }
    }

    fun costToWin(shiftPrizeLocation: Long = 0L): Long? {
        val ax = buttonA.x
        val ay = buttonA.y
        val bx = buttonB.x
        val by = buttonB.y
        val px = prizeX + shiftPrizeLocation
        val py = prizeY + shiftPrizeLocation

        val a = (by * px - bx * py).toDouble() / (ax * by - ay * bx)
        val b = (-ay * px + ax * py).toDouble() / (ax * by - ay * bx)
        return if (a % 1.0 == 0.0 && b % 1.0 == 0.0)
            a.toLong() * buttonA.cost + b.toLong() * buttonB.cost
        else
            null
    }
}

private fun main() {
    val clawMachines = classpathFile("day13/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .map { ClawMachine.parse(it) }
        .toList()

    clawMachines.sumOf { it.costToWin() ?: 0}.also { println("Part1: $it") }
    clawMachines.sumOf { it.costToWin(10000000000000) ?: 0}.also { println("Part2: $it") }
}
