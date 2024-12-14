package me.nfekete.adventofcode.y2024.day14

import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.product

private data class Robot(val position: Coord, val velocity: Coord) {
    companion object {
        private val regex = "p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)".toRegex()
        fun parse(line: String) = regex.matchEntire(line)!!.destructured.let { (px, py, vx, vy) ->
            Robot(Coord(px.toInt(), py.toInt()), Coord(vx.toInt(), vy.toInt()))
        }
    }
}

private fun List<Robot>.safetyValue(size: Coord, steps: Int): Int {
    val map = map { ((it.position + (it.velocity * steps)) % size + size) % size }
//    Grid2D(map.groupingBy { it }.eachCount()).pretty('.') { '0' + it }.also { println(it) }
    val eachCount = map
        .onEach { println(it) }
        .mapNotNull { coord ->
            val xHalf = when (coord.x) {
                in 0..<size.x / 2 -> 1
                in size.x / 2 + 1..<size.x -> 2
                else -> null
            }
            val yHalf = when (coord.y) {
                in 0..<size.y / 2 -> 1
                in size.y / 2 + 1..<size.y -> 2
                else -> null
            }
            xHalf?.let { yHalf?.let { xHalf to yHalf } }
        }
        .groupingBy { it }.eachCount()
    return eachCount.values.product()
}

private fun main() {
    val robots = classpathFile("day14/input.txt")
        .readLines()
        .map { Robot.parse(it) }

    val sampleSize = Coord(11, 7)
    val inputSize = Coord(101, 103)
    robots.safetyValue(inputSize, 100).also { println("Part1: $it") }
}
