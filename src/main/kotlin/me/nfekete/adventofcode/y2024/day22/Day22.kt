package me.nfekete.adventofcode.y2024.day22

import me.nfekete.adventofcode.y2024.common.classpathFile

private fun Long.nextSecret() = (this shl 6 xor this and 0xFFFFFF)
    .run { this shr 5 xor this and 0xFFFFFF }
    .run { this shl 11 xor this and 0xFFFFFF }

private fun Long.secretSequence() = generateSequence(this) { it.nextSecret() }
private fun Long.nthSecret(n: Int) = secretSequence().drop(n).first()
private val Long.lastDigit get() = this % 10L
private fun List<Long>.part1() = sumOf { it.nthSecret(2000) }
private fun List<Long>.part2(): Long {
    val windows = flatMapIndexed { index, initialSecret ->
        val priceChangesWithCurrentPrices = initialSecret.secretSequence().map { it.lastDigit }.take(2001)
            .zipWithNext().map { (a, b) -> Triple(b - a, b, index) }
        priceChangesWithCurrentPrices.windowed(4).toList()
    }
    return windows.let { windowed ->
        val map = windowed.groupBy({ listOfFour -> listOfFour.map { it.first } }) { listOfFour ->
            listOfFour.last().run { second to third }
        }.mapValues { (_, value) -> value.distinctBy { it.second }.map { it.first } }
        map.entries.maxOf { it.value.sum() }
    }
}

private fun main() {
    val input = classpathFile("day22/input.txt")
        .readLines()
        .map { it.toLong() }

    input.part1().also { println("Part1: $it") }
    input.part2().also { println("Part2: $it") }
}
