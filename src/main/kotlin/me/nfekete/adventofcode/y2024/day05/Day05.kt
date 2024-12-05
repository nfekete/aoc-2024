package me.nfekete.adventofcode.y2024.day05

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile

private fun Map<Int, Set<Int>>.isSequenceOrderedCorrectly(pages: List<Int>): Boolean =
    if (pages.size <= 1)
        true
    else {
        val first = pages.first()
        val rest = pages.drop(1)
        val mustNotFollow = get(first) ?: emptySet()
        rest.none { it in mustNotFollow } && isSequenceOrderedCorrectly(rest)
    }

private fun Map<Int, Set<Int>>.fixSequence(pages: List<Int>): List<Int> =
    if (pages.size <= 1)
        pages
    else {
        val first = pages.first()
        val rest = pages.drop(1)
        val mustNotFollow = get(first) ?: emptySet()
        val newFirst = rest.firstOrNull { it in mustNotFollow }
        if (newFirst == null)
            listOf(first) + fixSequence(rest)
        else
            fixSequence(listOf(newFirst) + first + (rest - newFirst))
    }

private fun main() {
    classpathFile("day05/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .toList()
        .let { (ruleStrings, pageSequenceStrings) ->
            val rules = ruleStrings.map { ruleString ->
                ruleString.split("|").map { it.toInt() }.let { (a, b) -> a to b }
            }.groupBy({ it.second }, { it.first }).mapValues { it.value.toSet() }
            val pageSequences = pageSequenceStrings.map { pageSequence -> pageSequence.split(",").map { it.toInt() } }

            pageSequences
                .filter { rules.isSequenceOrderedCorrectly(it) }
                .sumOf { it[it.size / 2] }
                .also { println("Part1: $it") }

            pageSequences
                .filter { !rules.isSequenceOrderedCorrectly(it) }
                .map { rules.fixSequence(it) }
                .sumOf { it[it.size / 2] }
                .also { println("Part2: $it") }
        }
}
