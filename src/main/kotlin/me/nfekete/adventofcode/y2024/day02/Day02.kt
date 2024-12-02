package me.nfekete.adventofcode.y2024.day02

import me.nfekete.adventofcode.y2024.common.classpathFile
import kotlin.math.absoluteValue
import kotlin.math.sign

private fun List<Int>.isSafeLevel(): Boolean {
    val signs = windowed(2).map { (a, b) -> (a - b).sign }
    val deltas = windowed(2).map { (a, b) -> (a - b).absoluteValue }
    return signs.distinct().size == 1 && deltas.all { it in 1..3 }
}

private fun main() {
    val input = classpathFile("day02/input.txt")
        .readLines()
        .map { line ->
            line.split(Regex("\\s+"))
                .map { it.toInt() }
        }

    input.count { level -> level.isSafeLevel() }
        .also { println("Part1: $it") }

    input.count { level ->
        level.indices.any { indexToDrop ->
            level.mapIndexedNotNull { index, i -> i.takeUnless { index == indexToDrop } }
                .isSafeLevel()
        }
    }.also { println("Part2: $it") }
}
