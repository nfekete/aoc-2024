package me.nfekete.adventofcode.y2024.common

import kotlin.math.floor

fun Long.primeFactors() = sequence {
    var acc = this@primeFactors
    var maybeDivisor = 2L
    val max = floor(this@primeFactors.toDouble()).toLong()
    while (maybeDivisor < max && acc > 1) {
        while (acc % maybeDivisor == 0L) {
            yield(maybeDivisor)
            acc /= maybeDivisor
        }
        maybeDivisor++
    }
}

tailrec fun Long.gcd(other: Long): Long =
    if (other == 0L) this else other.gcd(this % other)
fun Long.lcm(other: Long) = this / gcd(other) * other
fun Iterable<Long>.lcm(): Long = fold(1L, Long::lcm)
@JvmName("ilcm")
fun Iterable<Int>.lcm(): Long = fold(1L) { l, other -> l.lcm(other.toLong()) }
