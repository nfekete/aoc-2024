package me.nfekete.adventofcode.y2024.day07

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.map1
import me.nfekete.adventofcode.y2024.common.map2
import me.nfekete.adventofcode.y2024.common.splitByDelimiter

private fun Pair<Long, List<Long>>.canBeSatisfied(): Boolean {
    val result = first
    val requirements = second

    fun recurse(acc: Long?, numbers: List<Long>): Boolean {
        return if (numbers.isEmpty())
            acc == result
        else
            recurse((acc ?: 0) + numbers.first(), numbers.drop(1)) || recurse((acc ?: 1) * numbers.first(), numbers.drop(1))
    }

    return recurse(null, requirements)
}

private infix fun Long?.concat(other: Long) = ((this?.let { this.toString() } ?: "") + other.toString()).toLong()

private fun Pair<Long, List<Long>>.canBeSatisfied2(): Boolean {
    val result = first
    val requirements = second

    fun recurse(acc: Long?, numbers: List<Long>): Boolean {
        return if (numbers.isEmpty())
            acc == result
        else
            recurse((acc ?: 0) + numbers.first(), numbers.drop(1)) ||
                    recurse((acc ?: 1) * numbers.first(), numbers.drop(1)) ||
                    recurse(acc.concat(numbers.first()), numbers.drop(1))
    }

    return recurse(null, requirements)
}


private fun main() {
    val input = classpathFile("day07/input.txt")
        .readLines()
        .map { line ->
            line.splitByDelimiter(": ")
                .map1 { it.toLong() }
                .map2 { numberStrings -> numberStrings.split(' ').map { it.toLong() } }
        }

    input.filter { it.canBeSatisfied() }.sumOf { it.first }.also { println("Part1: $it") }
    input.filter { it.canBeSatisfied2() }.sumOf { it.first }.also { println("Part2: $it") }
}
