package me.nfekete.adventofcode.y2024.benchmark

import me.nfekete.adventofcode.y2024.common.chunkBy
import me.nfekete.adventofcode.y2024.common.classpathFile
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.TimeUnit

@Fork(1)
@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
open class Day13Benchmarks {

    data class Button(val id: String, val x: Long, val y: Long, val cost: Long) {
        companion object {
            private val regex = "Button ([AB]): X\\+(\\d+), Y\\+(\\d+)".toRegex()
            fun parse(line: String): Button {
                val (letter, x, y) = regex.matchEntire(line)!!.destructured
                return Button(letter, x.toLong(), y.toLong(), if (letter == "A") 3 else 1)
            }
        }
    }
    data class ClawMachine(val buttonA: Button, val buttonB: Button, val prizeX: Long, val prizeY: Long) {
        companion object {
            private val regex = "Prize: X=(\\d+), Y=(\\d+)".toRegex()
            fun parse(lines: List<String>): ClawMachine {
                val (prizeX, prizeY) = regex.matchEntire(lines[2])!!.destructured
                return ClawMachine(Button.parse(lines[0]), Button.parse(lines[1]), prizeX.toLong(), prizeY.toLong())
            }
        }

        fun costToWin(shiftPrizeLocation: Long = 0L): Long? {
            val ax = buttonA.x
            val ay = buttonA.y
            val bx = buttonB.x
            val by = buttonB.y
            val px = prizeX + shiftPrizeLocation
            val py = prizeY + shiftPrizeLocation

            val a = (by * px - bx * py).toDouble() / (ax * by - ay * bx)
            val b = (-ay * px + ax * py).toDouble() / (ax * by - ay * bx)
            return if (a % 1.0 == 0.0 && b % 1.0 == 0.0)
                a.toLong() * buttonA.cost + b.toLong() * buttonB.cost
            else
                null
        }
    }

    @State(Scope.Thread)
    open class BenchmarkState {
        @field:Volatile
        var clawMachines: List<ClawMachine>

        init {
            clawMachines = classpathFile("day13/input.txt")
                .lineSequence()
                .chunkBy { it.isEmpty() }
                .map { ClawMachine.parse(it) }
                .toList()
        }
    }

    @Benchmark
    fun testPart1() {
        BenchmarkState().run {
            clawMachines.sumOf { it.costToWin() ?: 0 }
        }
    }

    @Benchmark
    fun testPart2() {
        BenchmarkState().run {
            clawMachines.sumOf { it.costToWin(10000000000000) ?: 0}
        }
    }

    @Benchmark
    fun testPart1InputPreloaded(state: BenchmarkState) {
        with (state) {
            clawMachines.sumOf { it.costToWin() ?: 0 }
        }
    }

    @Benchmark
    fun testPart2InputPreloaded(state: BenchmarkState) {
        with (state) {
            clawMachines.sumOf { it.costToWin() ?: 0 }
        }
    }
}

fun main() {
    val opt: Options = OptionsBuilder()
        .include(Day13Benchmarks::class.java.getSimpleName())
        .forks(1)
        .build()

    Runner(opt).run()
}
