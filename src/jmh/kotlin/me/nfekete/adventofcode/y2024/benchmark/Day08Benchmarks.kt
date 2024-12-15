//package me.nfekete.adventofcode.y2024.benchmark
//
//import me.nfekete.adventofcode.y2024.common.Grid2D
//import me.nfekete.adventofcode.y2024.common.Grid2D.Coord.Companion.zero
//import me.nfekete.adventofcode.y2024.common.classpathFile
//import me.nfekete.adventofcode.y2024.common.crossProduct
//import org.openjdk.jmh.annotations.Benchmark
//import org.openjdk.jmh.annotations.BenchmarkMode
//import org.openjdk.jmh.annotations.Fork
//import org.openjdk.jmh.annotations.Measurement
//import org.openjdk.jmh.annotations.Mode
//import org.openjdk.jmh.annotations.OutputTimeUnit
//import org.openjdk.jmh.annotations.Scope
//import org.openjdk.jmh.annotations.State
//import org.openjdk.jmh.annotations.Warmup
//import org.openjdk.jmh.runner.Runner
//import org.openjdk.jmh.runner.options.Options
//import org.openjdk.jmh.runner.options.OptionsBuilder
//import java.util.concurrent.TimeUnit
//
//@Fork(1)
//@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
//@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//open class Day08Benchmarks {
//
//    @State(Scope.Thread)
//    open class BenchmarkState {
//        @field:Volatile
//        var grid: Grid2D<Char>
//
//        init {
//            grid = classpathFile("day08/input.txt")
//                .readLines()
//                .let { Grid2D.from(it) }
//        }
//    }
//
//    private fun Grid2D<Char>.reverseMap() =
//        map.entries.groupBy({ it.value }) { it.key }
//
//    private fun Grid2D<Char>.part1() =
//        reverseMap().entries
//            .filter { it.key != '.' }
//            .flatMap { (_, sameFrequencyCoords) ->
//                (sameFrequencyCoords crossProduct sameFrequencyCoords)
//                    .filter { (a, b) -> a != b }
//                    .flatMap { (a, b) -> listOf(a + (a - b), b + (b - a)) }
//            }.filter { it in coords }
//            .toSet().size
//
//    private fun Grid2D<Char>.part2() =
//        reverseMap().entries
//            .filter { it.key != '.' }
//            .flatMap { (_, sameFrequencyCoords) ->
//                (sameFrequencyCoords crossProduct sameFrequencyCoords)
//                    .filter { (a, b) -> a != b }
//                    .flatMap { (a, b) ->
//                        val delta = a - b
//                        val set1 = generateSequence(zero) { it + delta }
//                            .map { a + it }
//                            .takeWhile { it in coords }
//                        val set2 = generateSequence(delta) { it + delta }
//                            .map { a - it }
//                            .takeWhile { it in coords }
//                        set1 + set2
//                    }
//            }
//            .toSet().size
//
//    @Benchmark
//    fun testPart1() {
//        BenchmarkState().run {
//            grid.part1()
//        }
//    }
//
//    @Benchmark
//    fun testPart2() {
//        BenchmarkState().run {
//            grid.part2()
//        }
//    }
//
//    @Benchmark
//    fun testPart1InputPreloaded(state: BenchmarkState) {
//        with (state) {
//            grid.part1()
//        }
//    }
//
//    @Benchmark
//    fun testPart2InputPreloaded(state: BenchmarkState) {
//        with (state) {
//            grid.part2()
//        }
//    }
//}
//
//fun main() {
//    val opt: Options = OptionsBuilder()
//        .include(Day08Benchmarks::class.java.getSimpleName())
//        .forks(1)
//        .build()
//
//    Runner(opt).run()
//}
