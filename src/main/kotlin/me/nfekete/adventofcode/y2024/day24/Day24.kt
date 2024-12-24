package me.nfekete.adventofcode.y2024.day24

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.map2
import me.nfekete.adventofcode.y2024.common.splitByDelimiter
import me.nfekete.adventofcode.y2024.day24.GateType.AND
import me.nfekete.adventofcode.y2024.day24.GateType.OR
import me.nfekete.adventofcode.y2024.day24.GateType.XOR

private enum class GateType { AND, OR, XOR }
private data class Gate(val gateType: GateType, val input1: String, val input2: String, val output: String) {
    companion object {
        private val lineRegex = "(...) (AND|OR|XOR) (...) -> (...)".toRegex()
        fun parse(line: String) = lineRegex.matchEntire(line)!!.destructured.let { (input1, gateType, input2, output) ->
            Gate(GateType.valueOf(gateType), input1, input2, output)
        }
    }
}

private class Circuit(gates: List<Gate>, val initialValues: Map<String, Int>) {
    val gates = gates.associateBy { it.output }
    val values: MutableMap<String, Int> = initialValues.toMutableMap()

    fun Gate.resolve(map: MutableMap<String, Int>): Int =
        if (output in map.keys) map[output]!! else {
            val v1 = map[input1] ?: gates[input1]!!.resolve(map)
            val v2 = map[input2] ?: gates[input2]!!.resolve(map)
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
            Circuit(gates, initialValues).resolve().also { println("Part1: $it") }
        }
}
