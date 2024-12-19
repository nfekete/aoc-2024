package me.nfekete.adventofcode.y2024.day19

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.memoized

private class TowelCombiner(private val towels: Set<String>) {

    private val recurseM = ::recurse.memoized()
    private fun recurse(design: String): Boolean =
        if (design.isEmpty() || design in towels)
            true
        else
            (1..<design.length).any { n -> recurseM(design.take(n)) && recurseM(design.drop(n)) }

    fun canBeMade(design: String) = recurseM(design)

}

private fun main() {
    classpathFile("day19/input.txt")
        .readLines()
        .let { lines ->
            val towels = lines.first().splitToSequence(", ").toSet()
            val designs = lines.drop(2)

            val towelCombiner = TowelCombiner(towels)
            designs.count { towelCombiner.canBeMade(it) }.also { println("Part1: $it") }
        }
}
