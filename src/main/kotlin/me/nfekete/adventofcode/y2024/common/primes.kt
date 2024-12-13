package me.nfekete.adventofcode.y2024.common

fun Long.primeFactors() = sequence {
    val max = this@primeFactors
    var acc = max
    var maybeDivisor = 2L
    while (maybeDivisor <= max && acc > 1) {
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
