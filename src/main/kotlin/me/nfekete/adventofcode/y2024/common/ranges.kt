package me.nfekete.adventofcode.y2024.common

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

val Pair<Int, Int>.range get() = if (first <= second) first..second else first downTo second
val Pair<Long, Long>.range get() = if (first <= second) first..second else first downTo second
val Pair<Double, Double>.range get() = if (first <= second) first..second else second..first
infix fun <T : Comparable<T>> ClosedFloatingPointRange<T>.intersect(other: ClosedFloatingPointRange<T>) =
    if (start <= other.endInclusive && other.start <= endInclusive)
        maxOf(start, other.start)..minOf(endInclusive, other.endInclusive)
    else
        null

val ClosedFloatingPointRange<Double>.enclosedLongRange get() = ceil(start).toLong()..floor(endInclusive).toLong()
infix fun IntRange.intersect(other: IntRange) =
    if (first <= other.last && other.first <= last)
        maxOf(first, other.first)..minOf(last, other.last)
    else
        IntRange.EMPTY

infix fun IntRange.crossProduct(other: IntRange) =
    asSequence().flatMap { element -> other.map { otherElement -> element to otherElement } }

fun <R> crossProduct(ra: IntRange, rb: IntRange, fn: (Int, Int) -> R) =
    ra.flatMap { a -> rb.map { b -> fn(a, b) } }

fun <R> crossProduct(ra: IntRange, rb: IntRange, rc: IntRange, fn: (Int, Int, Int) -> R) =
    ra.flatMap { a -> rb.flatMap { b -> rc.map { c -> fn(a, b, c) } } }

fun <R> crossProduct(ra: IntRange, rb: IntRange, rc: IntRange, rd: IntRange, fn: (Int, Int, Int, Int) -> R) =
    ra.flatMap { a -> rb.flatMap { b -> rc.flatMap { c -> rd.map { d -> fn(a, b, c, d) } } } }

val IntRange.length get() = max(0, endInclusive - start + 1).toLong()
val LongRange.length get() = max(0, endInclusive - start + 1).toLong()
