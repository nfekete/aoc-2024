package me.nfekete.adventofcode.y2024.day03

import me.nfekete.adventofcode.y2024.common.classpathFile

private val mulExpressionRegex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
private val instructionRegex = Regex("""mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\)""")

sealed interface Instruction {
    companion object {
        fun parseAll(text: String) =
            instructionRegex.findAll(text)
                .map { it.groupValues }
                .map { (instruction, a, b) ->
                    when (instruction) {
                        "do()" -> Do
                        "don't()" -> Dont
                        else -> Mul(a.toLong(), b.toLong())
                    }
                }
                .toList()
    }
}

private data class Mul(val a: Long, val b: Long) : Instruction {
    companion object {
        fun parseAll(text: String) =
            mulExpressionRegex.findAll(text)
                .map { it.groupValues }
                .map { Mul(it[1].toLong(), it[2].toLong()) }
                .toList()
    }
}

private data object Do : Instruction
private data object Dont : Instruction

@JvmName("evaluateMul")
private fun Collection<Mul>.evaluate() = sumOf { it.a * it.b }

private data class EvalState(val evalEnabled: Boolean = true, val sum: Long = 0)

private fun Collection<Instruction>.evaluate() =
    fold(EvalState()) { state, instruction ->
        with(state) {
            when (instruction) {
                is Mul -> copy(sum = sum + if (evalEnabled) instruction.a * instruction.b else 0)
                is Do -> copy(evalEnabled = true)
                is Dont -> copy(evalEnabled = false)
            }
        }
    }.sum

private fun main() {
    val input = classpathFile("day03/input.txt")
        .readText()

    Mul.parseAll(input).evaluate().also { println("Part1: $it") }
    Instruction.parseAll(input).evaluate().also { println("Part2: $it") }
}
