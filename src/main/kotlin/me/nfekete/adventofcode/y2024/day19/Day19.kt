package me.nfekete.adventofcode.y2024.day19

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.memoized
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

private class TowelCombiner(private val towels: Set<String>) {

    private val howManyWaysForRM = ::howManyWaysForR.memoized()
    private fun howManyWaysForR(design: String): Long =
        if (design.isEmpty())
            1
        else
            (0..design.length)
                .filter { n -> design.take(n) in towels }
                .sumOf { n ->
                    howManyWaysForRM(design.drop(n))
                }

    fun howManyWaysFor(design: String) = howManyWaysForRM(design)
}

private fun main() {
    classpathFile("day19/input.txt")
        .readLines()
        .let { lines ->
            val towels = lines.first().splitToSequence(", ").toSet()
            val designs = lines.drop(2)

            val towelCombiner = TowelCombiner(towels)
            designs.map { design ->
                towelCombiner.howManyWaysFor(design)
            }.let { ways ->
                ways.count { it > 0 }.also { println("Part1: $it") }
                ways.sum().also { println("Part2: $it") }
            }
        }
}

private class Tests {
    @TestFactory
    fun test() =
        TowelCombiner(setOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")).let { towelCombiner ->
            listOf(
                "brwrr" to 2,
                "bggr" to 1,
                "gbbr" to 4,
                "rrbgbr" to 6,
                "bwurrg" to 1,
                "ubwu" to 0,
                "bbrgwb" to 0,
            ).map { (design, expected) ->
                DynamicTest.dynamicTest(design) {
                    assertEquals(expected.toLong(), towelCombiner.howManyWaysFor(design))
                }
            }
        }
}
