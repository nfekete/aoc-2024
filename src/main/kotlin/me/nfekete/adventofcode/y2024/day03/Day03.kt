package me.nfekete.adventofcode.y2024.day03

import me.nfekete.adventofcode.y2024.common.classpathFile

private val mulExpressionRegex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")

private data class Mul(val a: Long, val b: Long) {
    companion object {
        fun parseAll(text: String) =
            mulExpressionRegex.findAll(text)
                .map { it.groupValues }
                .map { Mul(it[1].toLong(), it[2].toLong()) }
                .toList()
    }
}

private fun Collection<Mul>.evaluate() = sumOf { it.a * it.b }

private fun main() {
    val input = classpathFile("day03/input.txt")
        .readText()

    Mul.parseAll(input).also(::println).evaluate().also(::println)
}
