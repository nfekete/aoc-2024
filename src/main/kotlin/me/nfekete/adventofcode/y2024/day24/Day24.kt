package me.nfekete.adventofcode.y2024.day24

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct
import me.nfekete.adventofcode.y2024.common.map2
import me.nfekete.adventofcode.y2024.common.removePrefix
import me.nfekete.adventofcode.y2024.common.splitByDelimiter
import me.nfekete.adventofcode.y2024.common.toSet
import me.nfekete.adventofcode.y2024.day24.GateType.AND
import me.nfekete.adventofcode.y2024.day24.GateType.OR
import me.nfekete.adventofcode.y2024.day24.GateType.XOR
import java.io.PrintWriter

private enum class GateType { AND, OR, XOR }
private data class Gate(val gateType: GateType, val inputs: Pair<String, String>, val output: String) {
    companion object {
        private val lineRegex = "(...) (AND|OR|XOR) (...) -> (...)".toRegex()
        fun parse(line: String) = lineRegex.matchEntire(line)!!.destructured.let { (input1, gateType, input2, output) ->
            Gate(GateType.valueOf(gateType), input1 to input2, output)
        }
    }
}

private class Circuit(gates: List<Gate>, val initialValues: Map<String, Int>) {
    val gates = gates.associateBy { it.output }
    val errors = mutableListOf<String>()
    val candidateWires = mutableSetOf<String>()
    val correctWires = mutableSetOf<String>()

    fun Gate.resolve(map: MutableMap<String, Int>): Int =
        if (output in map.keys) map[output]!! else {
            val v1 = map[inputs.first] ?: gates[inputs.first]!!.resolve(map)
            val v2 = map[inputs.first] ?: gates[inputs.second]!!.resolve(map)
            when (this.gateType) {
                AND -> v1 and v2
                OR -> v1 or v2
                XOR -> v1 xor v2
            }.also { map[output] = it }
        }

    fun resolve(selector: (Gate) -> Boolean = { it.output.startsWith("z") }): Long {
        val map = initialValues.toMutableMap()
        return gates.values.filter(selector).sortedByDescending { it.output }
            .map { it.resolve(map) }
            .joinToString("")
            .toLong(2)
    }

    infix fun String.xor(other: String): String =
        gates.values.singleOrNull {
            it.gateType == XOR && it.inputs.toSet() == setOf(
                this,
                other
            )
        }?.output?.also { correctWires.add(it) }
            ?: "???".also {
                errors.add("couldn't find XOR gate with inputs: ${this@xor}, $other")
                candidateWires.add(this)
                candidateWires.add(other)
            }

    infix fun String.and(other: String): String =
        gates.values.singleOrNull {
            it.gateType == AND && it.inputs.toSet() == setOf(
                this,
                other
            )
        }?.output?.also { correctWires.add(it) }
            ?: "???".also { errors.add("couldn't find AND gate with inputs: ${this@and}, $other") }

    infix fun String.or(other: String): String =
        gates.values.singleOrNull {
            it.gateType == OR && it.inputs.toSet() == setOf(
                this,
                other
            )
        }?.output?.also { correctWires.add(it) }
            ?: "???".also { errors.add("couldn't find OR gate with inputs: ${this@or}, $other") }

    fun xin(index: Int) = "x%02d".format(index)
    fun yin(index: Int) = "y%02d".format(index)
    fun zout(index: Int) = "z%02d".format(index)

    fun expectAdder(index: Int, expectedCarryOutName: String? = null): Pair<String, String> {
        val xin = xin(index)
        val yin = yin(index)
//        val zout = zout(index)
        if (index > 0) {
            val (_, prevCarryOut) = expectAdder(index - 1)
            val xor1 = xin xor yin
            val sum = xor1 xor prevCarryOut
            val and1 = xor1 and prevCarryOut
            val and2 = xin and yin
            val carry = and1 or and2
            return sum to carry
        } else {
            val sum = xin xor yin
            val carry = xin and yin
            return sum to carry
        }
    }

    private fun verifyOutputs() {
        gates.values.filter {
            it.output.startsWith("z")
                    && it.output.removePrefix("z").toInt() in 0..44
                    && it.gateType != XOR
        }.map { it.output }
            .let { candidateWires.addAll(it) }
    }

//    private fun verify

    fun expect45BitAdder() {
        verifyOutputs()
        expectAdder(44, "z45").also { println(it) }
        errors.distinct().forEach(::println)
        candidateWires.sorted().forEach(::println)
    }

    fun swapNames(name1: String, name2: String) =
        gates.values.map {
            when (it.output) {
                name1 -> it.copy(output = name2)
                name2 -> it.copy(output = name1)
                else -> it
            }
        }.let { Circuit(it, initialValues) }

    fun tryFixOne(): Circuit? {
        expect45BitAdder()
        val errorCount = errors.size
        println("Current error count: $errorCount")
        return if (errors.isEmpty())
            null
        else {
            val candidateWires = gates.keys - correctWires
            (candidateWires crossProduct candidateWires)
                .filter { (a, b) -> a < b }
                .map { (a, b) ->
                    swapNames(a, b)
                }
                .find {
                    it.expect45BitAdder()
                    println("Error count with replacement: ${it.errors.size}")
                    it.errors.size < errorCount
                }
        }
    }

}

private fun Circuit.createDotFile(filename: String) {
    PrintWriter(filename).use { pw ->
        pw.println("digraph gates {")
        pw.println("subgraph cluster_input {")
        pw.println(" rank=same;")
        pw.println(" ordering=out;")
        initialValues.keys.sortedDescending().forEach { pw.println("  $it;") }
        pw.println("}")
        pw.println("subgraph cluster_xors {")
        pw.println(" rank=same;")
        gates.values.filter { it.gateType == XOR }.forEach { gate ->
            pw.println("  ${gate.output} [label=\"${gate.gateType}\"];")
        }
        pw.println("}")
        pw.println("subgraph cluster_ands {")
        pw.println(" rank=same;")
        gates.values.filter { it.gateType == AND }.forEach { gate ->
            pw.println("  ${gate.output} [label=\"${gate.gateType}\"];")
        }
        pw.println("}")
        pw.println("subgraph cluster_ors {")
        pw.println(" rank=same;")
        gates.values.filter { it.gateType == OR }.forEach { gate ->
            pw.println("  ${gate.output} [label=\"${gate.gateType}\"];")
        }
        pw.println("}")
        gates.values.forEach { gate ->
            pw.println("  ${gate.inputs.first} -> ${gate.output};")
            pw.println("  ${gate.inputs.second} -> ${gate.output};")
        }
        pw.println("subgraph cluster_output {")
        pw.println(" rank=same;")
        gates.values.filter { it.output.startsWith("z") }.sortedByDescending { it.output }
            .forEach { pw.println("${it.output};") }
        pw.println("}")
        pw.println("}")
    }
}

private fun main() {
    classpathFile("day24/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .toList()
        .let { (initialValueLines, gateLines) ->
            val initialValues = initialValueLines.associate { line ->
                line.splitByDelimiter(": ").map2 { it.toInt() }
            }
            val gates = gateLines.map { line -> Gate.parse(line) }
            val circuit = Circuit(gates, initialValues)
            circuit.resolve().also { println("Part1: $it") }
//            circuit.resolve { it.output == "z01" }

            circuit.expect45BitAdder()
//            generateSequence(circuit) { it.tryFixOne() }.count().also { "Fixed circuit after $it tries" }


            circuit.createDotFile("src/main/resources/day24/circuit.dot")
        }
}
