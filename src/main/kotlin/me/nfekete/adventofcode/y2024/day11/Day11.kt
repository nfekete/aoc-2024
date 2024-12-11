package me.nfekete.adventofcode.y2024.day11

import me.nfekete.adventofcode.y2024.common.classpathFile

class Stones(private val stones: List<Long>) {

    private fun recurse(step: Int, stone: Long): Long =
        if (step == 0) 1
        else
            stone.toString().let { stoneStr ->
                val remainingSteps = step - 1
                when {
                    stone == 0L -> recurse(remainingSteps, 1L)
                    stoneStr.length % 2 == 0 ->
                        recurse(remainingSteps, stoneStr.take(stoneStr.length / 2).toLong()) +
                                recurse(remainingSteps, stoneStr.drop(stoneStr.length / 2).toLong())

                    else -> recurse(remainingSteps, stone * 2024)
                }
            }

    fun numStonesAfterBlinks(times: Int) = stones.sumOf { recurse(times, it) }
}

private fun main() {
    val sample = listOf(125L, 17)
    Stones(sample).numStonesAfterBlinks(6).also { println("Sample after 6 steps: $it") }
    Stones(sample).numStonesAfterBlinks(25).also { println("Sample after 25 steps: $it") }

    val input = classpathFile("day11/input.txt").readLine().split(' ').map { it.toLong() }
    Stones(input).numStonesAfterBlinks(25).also { println("Part1: $it") }
}
