package me.nfekete.adventofcode.y2024.benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.OperationsPerInvocation
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
open class SomeBenchmarks {

    @State(Scope.Thread)
    open class BenchmarkState {
//        @field:Volatile
//        var property = init block
    }

    @Benchmark
    @OperationsPerInvocation(1000)
    fun testBucketQueue(state: BenchmarkState) {
        with(state) {
            // code to benchmark
        }
    }
}

fun main() {
    val opt: Options = OptionsBuilder()
        .include(SomeBenchmarks::class.java.getSimpleName())
        .forks(1)
        .build()

    Runner(opt).run()
}
