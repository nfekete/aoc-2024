package me.nfekete.adventofcode.y2024.day19

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.memoized

class TowelCombiner(private val towels: Set<String>) {
    fun howManyWaysFor(design: String) = howManyWaysForRM(design)
    private val howManyWaysForRM = ::howManyWaysForR.memoized()
    private fun howManyWaysForR(design: String): Long =
        if (design.isEmpty())
            1
        else
            (0..design.length)
                .sumOf { n -> if (design.take(n) in towels) howManyWaysForRM(design.drop(n)) else 0 }
}

private fun main() {
    val lines = classpathFile("day19/input.txt").readLines()
    val towels = lines.first().splitToSequence(", ").toSet()
    val designs = lines.drop(2)
    val towelCombiner = TowelCombiner(towels)
    val ways = designs.map { design -> towelCombiner.howManyWaysFor(design) }
    ways.count { it > 0 }.also { println("Part1: $it") }
    ways.sum().also { println("Part2: $it") }
}
