package me.nfekete.adventofcode.y2024.day25

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct
import me.nfekete.adventofcode.y2024.common.transpose

data class Key(val heights: List<Int>)
data class Lock(val heights: List<Int>)

private fun Key.unlocks(lock: Lock) = heights.zip(lock.heights) { a, b -> a + b }.all { it < 6 }

private fun main() {
    val input = classpathFile("day25/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .map { lines ->
            val numbers = lines.map { it.toList() }.transpose().map { it.count { char -> char == '#' }.dec() }
            if (lines.first().all { it == '#' }) Key(numbers) else Lock(numbers)
        }
        .toList()

    (input.filterIsInstance<Key>().crossProduct(input.filterIsInstance<Lock>()))
        .count { (key, lock) -> key.unlocks(lock) }
        .also { println("Part1: $it") }
}
