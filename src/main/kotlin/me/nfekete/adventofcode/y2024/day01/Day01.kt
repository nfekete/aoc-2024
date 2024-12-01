package me.nfekete.adventofcode.y2024.day01

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.map1
import me.nfekete.adventofcode.y2024.common.map2
import kotlin.math.absoluteValue

private fun main() {
    val input = classpathFile("day01/input.txt")
        .readLines()
        .map { line ->
            line.split(Regex("\\s+"))
                .map { it.toLong() }
        }.map { (a, b) -> a to b }.unzip()

    input.map1 { it.sorted() }
        .map2 { it.sorted() }
        .let { (left, right) ->
            left.zip(right)
                .map { (a, b) -> (a - b).absoluteValue }
        }
        .sum()
        .also { println("Part 1: $it") }

    input.map2 { list -> list.groupingBy { it }.eachCount() }
        .let { (left, right) ->
            left.sumOf { it * (right[it] ?: 0) }
        }
        .also { println("Part 2: $it") }
}
