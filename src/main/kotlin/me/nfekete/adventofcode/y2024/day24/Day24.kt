package me.nfekete.adventofcode.y2024.day24

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.inOrder
import me.nfekete.adventofcode.y2024.common.map2
import me.nfekete.adventofcode.y2024.common.mapBoth
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

    fun xin(index: Int) = "x%02d".format(index)
    fun yin(index: Int) = "y%02d".format(index)
    fun zout(index: Int) = "z%02d".format(index)

    private fun verifyOutputs() =
        gates.values.filter {
            it.output.startsWith("z")
                    && it.output.removePrefix("z").toInt() in 0..44
                    && it.gateType != XOR
        }.map { it.output }.toSet()

    private fun inputXorGates() =
        gates.values.filter { it.gateType == XOR }
            .filter { gate -> gate.inputs.mapBoth { it.first() }.inOrder == ('x' to 'y') }

    private fun inputAndGates() =
        gates.values.filter { it.gateType == AND }
            .filter { gate -> gate.inputs.mapBoth { it.first() }.inOrder == ('x' to 'y') }

    private fun orGates() =
        gates.values.filter { it.gateType == OR }

    private fun nonInputAndGates() =
        gates.values.filter { it.gateType == AND }
            .filterNot { gate -> gate.inputs.mapBoth { it.first() }.inOrder == ('x' to 'y') }

    private fun nonInputXorGates() =
        gates.values.filter { it.gateType == XOR }
            .filterNot { gate -> gate.inputs.mapBoth { it.first() }.inOrder == ('x' to 'y') }

    private fun verifyInputXorGatesLeadToNonOutputs() =
        inputXorGates()
            .filter { gate -> gate.inputs.toSet() != setOf("x00", "y00") && gate.output == "z00" }
            .filter { it.output.startsWith("z") }.map { it.output }.toSet()

    private fun verifyNonInputXorGatesLeadToOutputs() =
        nonInputXorGates()
            .filter { !it.output.startsWith("z") }
            .map { it.output }
            .toSet()

    private fun verifyXorChain(): Set<String> {
        val nonInputXorgateInputs = nonInputXorGates().flatMap { it.inputs.toSet() }.toSet()
        return inputXorGates()
            .filter { it.inputs.toSet() != setOf("x00", "y00") }.map { it.output }.filter { output -> output !in nonInputXorgateInputs }.toSet()
    }

    private fun verifyAndChain(): Set<String> {
        val orGateInputs = orGates().flatMap { it.inputs.toSet() }
        return inputAndGates().filter { it.inputs.toSet() != setOf("x00", "y00") }
            .filter { it.output !in orGateInputs }
            .map { it.output }.toSet()
    }

    fun verifyCircuit() = verifyOutputs() union
                verifyInputXorGatesLeadToNonOutputs() union
                verifyNonInputXorGatesLeadToOutputs() union
                verifyXorChain() union
                verifyAndChain()
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
            circuit.verifyCircuit().toSortedSet().joinToString(",").also { println("Part2: $it") }
            circuit.createDotFile("src/main/resources/day24/circuit.dot")
        }
}
