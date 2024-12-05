package me.nfekete.adventofcode.y2024.day05

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile

private fun List<Pair<Int, Int>>.isSequenceOrderedCorrectly(pages: List<Int>) =
    groupBy({ it.second }, { it.first }).mapValues { it.value.toSet() }
        .isSequenceOrderedCorrectly(pages)

private fun Map<Int, Set<Int>>.isSequenceOrderedCorrectly(pages: Collection<Int>): Boolean =
    if (pages.size <= 1)
        true
    else {
        val first = pages.first()
        val rest = pages.drop(1).toSet()
        val mustNotFollow = get(first) ?: emptySet()
        rest.none { it in mustNotFollow } && isSequenceOrderedCorrectly(rest)
    }

private fun main() {
    classpathFile("day05/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .toList()
        .let { (ruleStrings, pageSequenceStrings) ->
            val rules =
                ruleStrings.map { ruleString -> ruleString.split("|").map { it.toInt() }.let { (a, b) -> a to b } }
            val pageSequences = pageSequenceStrings.map { pageSequence -> pageSequence.split(",").map { it.toInt() } }

            pageSequences
                .filter { rules.isSequenceOrderedCorrectly(it) }
                .sumOf { it[it.size / 2] }
                .also { println("Part1: $it") }
        }
}
