package me.nfekete.adventofcode.y2024.day17

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.splitByDelimiter
import me.nfekete.adventofcode.y2024.day17.Operand.Combo
import me.nfekete.adventofcode.y2024.day17.Operand.Literal
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

private sealed interface Operand {
    data class Literal(val value: Int) : Operand
    data class Combo(private val rawOperand: Int) : Operand {
        context(State)
        val value
            get() = when (rawOperand) {
                in 0..3 -> rawOperand.toLong()
                4 -> a
                5 -> b
                6 -> c
                7 -> fail("reserved")
                else -> fail("invalid")
            }
    }
}

private sealed interface Instruction {
    companion object {
        fun from(opcode: Int, operand: Int) = when (opcode) {
            0 -> Adv(Combo(operand))
            1 -> Bxl(Literal(operand))
            2 -> Bst(Combo(operand))
            3 -> Jnz(Literal(operand))
            4 -> Bxc
            5 -> Out(Combo(operand))
            6 -> Bdv(Combo(operand))
            7 -> Cdv(Combo(operand))
            else -> fail()
        }
    }

    data class Adv(val operand: Combo) : Instruction
    data class Bxl(val operand: Literal) : Instruction
    data class Bst(val operand: Combo) : Instruction
    data class Jnz(val operand: Literal) : Instruction
    data object Bxc : Instruction
    data class Out(val operand: Combo) : Instruction
    data class Bdv(val operand: Combo) : Instruction
    data class Cdv(val operand: Combo) : Instruction
}

private fun List<Int>.parseInstructions() =
    windowed(2, 2).map { (opcode, operand) -> Instruction.from(opcode, operand) }

private data class State(val a: Long = 0L, val b: Long = 0L, val c: Long = 0L, val ip: Int = 0, val output: List<Int> = emptyList()) {
    context(List<Instruction>)
    fun next() =
        when (val instruction = get((ip / 2))) {
            is Instruction.Adv -> copy(a = a / (1L shl instruction.operand.value.toInt() ), ip = ip + 2)
            is Instruction.Bxl -> copy(b = b xor instruction.operand.value.toLong(), ip = ip + 2)
            is Instruction.Bst -> copy(b = instruction.operand.value % 8, ip = ip + 2)
            is Instruction.Jnz -> if (a == 0L) copy(ip = ip + 2) else copy(ip = instruction.operand.value)
            Instruction.Bxc -> copy(b = b xor c, ip = ip + 2)
            is Instruction.Out -> copy(output = output + (instruction.operand.value % 8).toInt(), ip = ip + 2)
            is Instruction.Bdv -> copy(b = a / (1 shl instruction.operand.value.toInt()), ip = ip + 2)
            is Instruction.Cdv -> copy(c = a / (1 shl instruction.operand.value.toInt()), ip = ip + 2)
        }
}

context(List<Instruction>)
private fun State.trace() =
    generateSequence(this) { state ->
        if (state.ip / 2 >= size.toLong())
            null
        else
            state.next()
    }

context(List<Instruction>)
private fun State.execute() = trace().last()

private fun List<Instruction>.verify(state: State, update: (State) -> State) =
    assertEquals(update(state), state.trace().last().copy(ip = 0))

class Tests {
    @Test
    fun tests() {
        listOf(2, 6).parseInstructions().verify(State(c = 9)) { it.copy(b = 1) }
        listOf(5, 0, 5, 1, 5, 4).parseInstructions().verify(State(a = 10)) { it.copy(output = listOf(0, 1, 2)) }
        listOf(0, 1, 5, 4, 3, 0).parseInstructions().verify(State(a = 2024)) {
            it.copy(a = 0, output = listOf(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0))
        }
        listOf(1, 7).parseInstructions().verify(State(b = 29)) { it.copy(b = 26) }
        listOf(4, 0).parseInstructions().verify(State(b = 2024, c = 43690)) { it.copy(b = 44354) }
        listOf(0, 1, 5, 4, 3, 0).parseInstructions().verify(State(a = 729)) {
            it.copy(
                a = 0,
                output = listOf(4, 6, 3, 5, 6, 3, 5, 2, 1, 0)
            )
        }

        listOf(0,3,5,4,3,0).let { instructions -> instructions.parseInstructions().verify(State(a = 117440)) { it.copy(a = 0, output = instructions)} }
    }
}

private fun State.pretty() = "a=${a.to64Bits()}, b=${b.to64Bits()}, c=${c.to64Bits()}, output=${output.to3BitBinary()}"
private fun Long.to64Bits() = toString(2).padStart(63, '0')
private fun List<Int>.to3BitBinary() = joinToString { it.toString(2).padStart(3, '0') }

private fun main() {
    classpathFile("day17/input.txt")
        .readLines()
        .also { lines ->
            val (a, b, c) = lines.take(3).map { it.splitByDelimiter(": ").second.toLong() }
            val opcodes = lines[4].splitByDelimiter(": ").second.split(',').map { it.toInt() }
            val instructions = opcodes.parseInstructions()

            with (instructions) {
                State(a, b, c).execute().output.joinToString(",").also { println("Part1: $it") }
            }

            with(instructions) {
                (1..opcodes.size).fold(setOf(0L)) { set, _ ->
                    set.flatMap { acc ->
                        (0..7L).filter { lsbs ->
                            State(acc shl 3 or lsbs, b, c).execute().output.let { it == opcodes.takeLast(it.size) }
                        }.map { lsbs -> acc shl 3 or lsbs }
                    }.toSet()
                }.minBy { regA -> State(regA, b, c).execute().output == opcodes }
                    .also { println("Part2: $it") }
            }
        }
}
