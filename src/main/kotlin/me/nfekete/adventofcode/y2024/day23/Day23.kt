package me.nfekete.adventofcode.y2024.day23

import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProduct
import me.nfekete.adventofcode.y2024.common.flatten
import me.nfekete.adventofcode.y2024.common.splitByDelimiter
import me.nfekete.adventofcode.y2024.common.swapped

private fun Map<String, Set<String>>.part1() =
    (keys crossProduct keys crossProduct keys).map { it.flatten() }
        .filter { (a, b, c) -> a < b && b < c }
        .filter { (a, b, c) ->
            this[a]!!.let { b in it && c in it } &&
                    this[b]!!.let { a in it && c in it } &&
                    this[c]!!.let { a in it && b in it }
        }.filter { (a, b, c) -> a.startsWith("t") || b.startsWith("t") || c.startsWith("t") }
        .size

private fun Map<String, Set<String>>.part2(): String {
    val sequence = sequence {
        suspend fun SequenceScope<Set<String>>.recurse(r: Set<String>, p: Set<String>, x: Set<String>) {
            val p = p.toMutableSet()
            val x = x.toMutableSet()
            if (p.isEmpty() && x.isEmpty())
                yield(r)
            else
                while (p.isNotEmpty()) {
                    p.first().let { v ->
                        val vNeighbors = this@part2[v]!!
                        recurse(r + v, p intersect vNeighbors, x intersect vNeighbors)
                        p.remove(v)
                        x.add(v)
                    }
                }
        }
        recurse(r = emptySet(), p = keys, x = emptySet())
    }
    return sequence.maxBy { it.size }.toSortedSet().joinToString(",")
}

private fun main() {
    val input = classpathFile("day23/input.txt")
        .readLines()
        .map { it.splitByDelimiter('-') }

    val map = (input + input.map { it.swapped }).groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
    map.part1().also { println("Part1: $it") }
    map.part2().also { println("Part2: $it") }
}
