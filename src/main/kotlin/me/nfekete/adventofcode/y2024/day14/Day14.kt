package me.nfekete.adventofcode.y2024.day14

import me.nfekete.adventofcode.y2024.common.Grid2D
import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import me.nfekete.adventofcode.y2024.common.classpathFile
import me.nfekete.adventofcode.y2024.common.flatten
import me.nfekete.adventofcode.y2024.common.product
import me.nfekete.adventofcode.y2024.day14.Robots.PositionInFold.CENTER
import me.nfekete.adventofcode.y2024.day14.Robots.PositionInFold.FIRST
import me.nfekete.adventofcode.y2024.day14.Robots.PositionInFold.SECOND
import kotlin.math.log2

private data class Robot(val position: Coord, val velocity: Coord) {
    companion object {
        private val regex = "p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)".toRegex()
        fun parse(line: String) = regex.matchEntire(line)!!.destructured.let { (px, py, vx, vy) ->
            Robot(Coord(px.toInt(), py.toInt()), Coord(vx.toInt(), vy.toInt()))
        }
    }
}

private class Robots(private val robots: List<Robot>, private val fieldSize: Coord) {
    private enum class PositionInFold { FIRST, CENTER, SECOND }

    private fun List<Robot>.takeSteps(steps: Long) =
        map { ((it.position + (it.velocity * steps)) % fieldSize + fieldSize) % fieldSize }

    private fun List<Coord>.groupByQuadrants() =
        map { coord ->
            val xHalf = coord.horizontalPositionInFold()
            val yHalf = coord.verticalPositionInFold()
            coord to (xHalf to yHalf)
        }.groupBy({ it.second }) { it.first }

    private fun Coord.verticalPositionInFold() =
        when (y) {
            in 0..<fieldSize.y / 2 -> FIRST
            in fieldSize.y / 2 + 1..<fieldSize.y -> SECOND
            else -> CENTER
        }

    private fun Coord.horizontalPositionInFold() =
        when (x) {
            in 0..<fieldSize.x / 2 -> FIRST
            in fieldSize.x / 2 + 1..<fieldSize.x -> SECOND
            else -> CENTER
        }

    fun safetyValue(steps: Long) =
        robots.takeSteps(steps)
            .groupByQuadrants().entries
            .filter { (key, _) -> key.first != CENTER && key.second != CENTER }
            .map { it.value.size }
            .product()

    fun List<Coord>.entropy(numBins: Int): Double {
        if (isEmpty()) return 0.0
        val binSize = (fieldSize - Coord.one) / numBins + Coord.one
        val bins = groupingBy { coord -> coord / binSize }.eachCount()
        val totalPoints = count().toDouble()
        val probabilities = bins.values.map { it / totalPoints }
        val entropy = -probabilities.sumOf { p -> p * log2(p) }
        return entropy
    }

    fun findChristmasTree() =
        (0..fieldSize.x * fieldSize.y).map { steps ->
            (steps to (robots.takeSteps(steps).let { it to it.entropy(5) })).flatten()
        }.minBy { it.third }
}

private fun List<Coord>.pretty() =
    Grid2D(groupingBy { it }.eachCount()).pretty('.') { '0' + it }

private fun main() {
    val robots = classpathFile("day14/input.txt")
        .readLines()
        .map { Robot.parse(it) }
        .let { Robots(it, Coord(101, 103)) }

    robots.safetyValue(100).also { println("Part1: $it") }
    robots.findChristmasTree().also {
        println(it.second.pretty())
        println("Part2: ${it.first}, entropy: ${it.third}")
    }
}
