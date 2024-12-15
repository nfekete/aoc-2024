//package me.nfekete.adventofcode.y2024.benchmark
//
//import me.nfekete.adventofcode.y2024.common.chunkBy
//import me.nfekete.adventofcode.y2024.common.classpathFile
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
//open class Day05Benchmarks {
//
//    private fun Map<Int, Set<Int>>.isSequenceOrderedCorrectly(pages: List<Int>): Boolean =
//        if (pages.size <= 1)
//            true
//        else {
//            val first = pages.first()
//            val rest = pages.drop(1)
//            val mustNotFollow = get(first) ?: emptySet()
//            rest.none { it in mustNotFollow } && isSequenceOrderedCorrectly(rest)
//        }
//
//    private fun Map<Int, Set<Int>>.fixSequence(pages: List<Int>): List<Int> =
//        if (pages.size <= 1)
//            pages
//        else {
//            val first = pages.first()
//            val rest = pages.drop(1)
//            val mustNotFollow = get(first) ?: emptySet()
//            val newFirst = rest.firstOrNull { it in mustNotFollow }
//            if (newFirst == null)
//                listOf(first) + fixSequence(rest)
//            else
//                fixSequence(listOf(newFirst) + first + (rest - newFirst))
//        }
//
//    @State(Scope.Thread)
//    open class BenchmarkState {
//        @field:Volatile
//        var rules: Map<Int, Set<Int>>
//
//        @field:Volatile
//        var pageSequences: List<List<Int>>
//
//        init {
//            loadInput().let { (rules, pageSequences) ->
//                this.rules = rules
//                this.pageSequences = pageSequences
//            }
//        }
//
//        private fun loadInput() = classpathFile("day05/input.txt")
//            .lineSequence()
//            .chunkBy { it.isEmpty() }
//            .toList()
//            .let { (ruleStrings, pageSequenceStrings) ->
//                val rules = ruleStrings.map { ruleString ->
//                    ruleString.split("|").map { it.toInt() }.let { (a, b) -> a to b }
//                }.groupBy({ it.second }, { it.first }).mapValues { it.value.toSet() }
//                val pageSequences =
//                    pageSequenceStrings.map { pageSequence -> pageSequence.split(",").map { it.toInt() } }
//
//                rules to pageSequences
//            }
//
//
//    }
//
//    @Benchmark
//    fun testPart1() {
//        BenchmarkState().run {
//            pageSequences
//                .filter { rules.isSequenceOrderedCorrectly(it) }
//                .sumOf { it[it.size / 2] }
//        }
//    }
//
//    @Benchmark
//    fun testPart2() {
//        BenchmarkState().run {
//            pageSequences
//                .filter { !rules.isSequenceOrderedCorrectly(it) }
//                .map { rules.fixSequence(it) }
//                .sumOf { it[it.size / 2] }
//        }
//    }
//
//    @Benchmark
//    fun testPart1InputPreloaded(state: BenchmarkState) {
//        with (state) {
//            pageSequences
//                .filter { rules.isSequenceOrderedCorrectly(it) }
//                .sumOf { it[it.size / 2] }
//        }
//    }
//
//    @Benchmark
//    fun testPart2InputPreloaded(state: BenchmarkState) {
//        with (state) {
//            pageSequences
//                .filter { !rules.isSequenceOrderedCorrectly(it) }
//                .map { rules.fixSequence(it) }
//                .sumOf { it[it.size / 2] }
//        }
//    }
//}
//
//fun main() {
//    val opt: Options = OptionsBuilder()
//        .include(Day05Benchmarks::class.java.getSimpleName())
//        .forks(1)
//        .build()
//
//    Runner(opt).run()
//}
