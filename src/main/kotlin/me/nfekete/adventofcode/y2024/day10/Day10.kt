package me.nfekete.adventofcode.y2024.day10

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.mapBoth

context(Grid2D<Char>)
fun Coord.trails(visited: Set<Coord> = setOf()): Set<Collection<Coord>> {
    if (this in visited) return emptySet()
    val elevation = map[this] ?: error("Something went wrong")
    if (elevation == '9') return setOf((visited + this).toList())
    return axisNeighbors.filter { map[it] == elevation + 1 }
        .flatMap { it.trails(visited + this) }
        .toSet()
}

private fun Grid2D<Char>.bothParts(): Pair<Int, Int> =
    map.entries.filter { (_, value) -> value == '0' }
        .map { (coord, _) ->
            val trails = coord.trails()
            trails.map { it.last() }.distinct().size to trails.size
        }.unzip()
        .mapBoth { it.sum() }

private fun main() {
    val input = classpathFile("day10/input.txt")
        .readLines()
        .let { Grid2D.from(it) }

    input.bothParts().also {
        println("Part1: ${it.first}")
        println("Part2: ${it.second}")
    }
}
