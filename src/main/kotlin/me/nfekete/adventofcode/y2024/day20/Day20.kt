package me.nfekete.adventofcode.y2024.day20

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.crossProductNotNull
import me.nfekete.adventofcode.y2024.common.produceIf

private fun Char?.isPartOfRacetrack() = this == '.' || this == 'S' || this == 'E'
private class Racetrack(val grid: Grid2D<Char>) {
    val end = grid.map.entries.single { (_, value) -> value == 'E' }.key
    val distances = walkPath(end)

    fun walkPath(from: Coord): Map<Coord, Int> {
        val visited = mutableSetOf<Coord>()
        var coord: Coord? = from
        do {
            visited.add(coord!!)
            coord = coord.axisNeighbors.find { grid.map[it].isPartOfRacetrack() && it !in visited }
        } while (coord != null)
        return visited.withIndex().associateBy({ it.value }) { it.index }
    }

    fun computeGains(maxDistances: List<Long>): List<Int> {
        val path = distances.keys
        return maxDistances.map { maxDistance ->
            val gains = path.flatMap { a ->
                val range = (-maxDistance..maxDistance)
                range.crossProductNotNull(range) { dx, dy ->
                    (a + Coord(dx, dy)).takeIf { it in path }?.let { b ->
                        val manhattan = (b - a).manhattan
                        produceIf(manhattan in 2..maxDistance) { distances[b]!! - distances[a]!! - manhattan }
                    }
                }
            }.filter { it > 0 }
            gains.count { it >= 100 }
        }
    }
}

private fun main() {
    val grid = classpathFile("day20/input.txt").readLines().let { lines -> Grid2D.from(lines) }
    Racetrack(grid)
        .computeGains(listOf(2, 20))
        .forEachIndexed { index, jumps -> println("Part$index: $jumps") }
}
