package me.nfekete.adventofcode.y2024.day22

import me.nfekete.adventofcode.y2024.common.classpathFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private const val twentyFourBits = (1L shl 24) - 1
private fun Long.nextSecret() = (this shl 6 xor this and twentyFourBits)
    .run { this shr 5 xor this and twentyFourBits }
    .run { this shl 11 xor this and twentyFourBits }
private fun Long.nthSecret(n: Int) = generateSequence(this) { it.nextSecret() }.drop(n).first()

class Tests {
    @Test
    fun test123() {
        val actual = generateSequence(123L) { it.nextSecret() }.take(11).toList()
        assertEquals(listOf(123L, 15887950L, 16495136L, 527345L, 704524L, 1553684L, 12683156L, 11100544L, 12249484L, 7753432L, 5908254L), actual)
    }
}

private fun main() {
    val input = classpathFile("day22/input.txt")
        .readLines()
        .map { it.toLong() }

    input.sumOf { it.nthSecret(2000) }.also { println("Part1: $it") }
}
