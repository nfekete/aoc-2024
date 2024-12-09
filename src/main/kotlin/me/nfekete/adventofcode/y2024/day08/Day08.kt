package me.nfekete.adventofcode.y2024.day08

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct

private fun Grid2D<Char>.reverseMap() =
    map.entries.groupBy({ it.value }) { it.key }

private fun Grid2D<Char>.part1() =
    reverseMap().entries
        .filter { it.key != '.' }
        .flatMap { (_, sameFrequencyCoords) ->
            (sameFrequencyCoords crossProduct sameFrequencyCoords)
                .filter { (a, b) -> a != b }
                .flatMap { (a, b) -> listOf(a + (a - b), b + (b - a)) }
        }.filter { it in coords }
        .toSet().size

private fun Grid2D<Char>.part2() =
    reverseMap().entries
        .filter { it.key != '.' }
        .flatMap { (_, sameFrequencyCoords) ->
            (sameFrequencyCoords crossProduct sameFrequencyCoords)
                .filter { (a, b) -> a != b }
                .flatMap { (a, b) ->
                    val delta = a - b
                    val set1 = generateSequence(zero) { it + delta }
                        .map { a + it }
                        .takeWhile { it in coords }
                    val set2 = generateSequence(delta) { it + delta }
                        .map { a - it }
                        .takeWhile { it in coords }
                    set1 + set2
                }
        }
        .toSet().size

private fun main() {
    val grid = classpathFile("day08/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    grid.part1().also { println("Part1: $it") }
    grid.part2().also { println("Part2: $it") }
}
