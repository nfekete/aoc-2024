package me.nfekete.adventofcode.y2024.day10

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile

context(Grid2D<Char>)
fun Coord.trailEnds(visited: MutableSet<Coord> = mutableSetOf()): Set<Coord> {
    if (this in visited) return emptySet()
    val elevation = map[this] ?: error("Something went wrong")
    if (elevation == '9') return setOf(this)
    visited += this
    return axisNeighbors.filter { map[it] == elevation + 1 }
        .flatMap { it.trailEnds(visited) }
        .toSet()
}

context(Grid2D<Char>)
fun Coord.trails(visited: Set<Coord> = setOf()): Set<Collection<Coord>> {
    if (this in visited) return emptySet()
    val elevation = map[this] ?: error("Something went wrong")
    if (elevation == '9') return setOf((visited + this).toList())
    return axisNeighbors.filter { map[it] == elevation + 1 }
        .flatMap { it.trails(visited + this) }
        .toSet()
}

private fun Grid2D<Char>.part1(): Int {
    return map.entries.filter { (_, value) -> value == '0' }
        .sumOf { (coord, _) -> coord.trailEnds().size }
}

private fun Grid2D<Char>.part2(): Int {
    return map.entries.filter { (_, value) -> value == '0' }
        .sumOf { (coord, _) -> coord.trails().size }
}

private fun main() {
    val input = classpathFile("day10/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    input.part1().also { println("Part1: $it") }
    input.part2().also { println("Part2: $it") }
}
