@file:Suppress("unused")

package me.nfekete.adventofcode.y2024.common

import java.io.BufferedReader
import java.io.InputStreamReader

fun classpathFile(path: String) =
    BufferedReader(
        InputStreamReader(
            Thread.currentThread().contextClassLoader.getResourceAsStream(path)!!
        )
    )

fun String.translate(vararg chars: Pair<Char, Char>) = chars.toMap().let { map ->
    this.map { map.getOrDefault(it, it) }.joinToString("")
}

fun <E> Sequence<E>.chunkBy(predicate: (E) -> Boolean) = sequence {
    val currentChunk = mutableListOf<E>()
    for (element in this@chunkBy) {
        if (predicate(element)) {
            yield(currentChunk.toList())
            currentChunk.clear()
        } else {
            currentChunk.add(element)
        }
    }
    yield(currentChunk.toList())
}

fun String.splitByDelimiter(delimiter: Char) =
    indexOf(delimiter)
        .let { index -> take(index) to substring(index + 1) }

fun String.splitByDelimiter(delimiter: String) =
    indexOf(delimiter)
        .let { index -> take(index) to substring(index + delimiter.length) }

fun <A, B, C> Pair<A, B>.map1(fn: (A) -> C): Pair<C, B> = let { (a, b) -> fn(a) to b }
fun <A, B, C> Pair<A, B>.map2(fn: (B) -> C): Pair<A, C> = let { (a, b) -> a to fn(b) }
fun <A, B, R> Pair<A, B>.map(fn: (A, B) -> R): R = let { (a, b) -> fn(a, b) }
fun <T, R> Pair<T, T>.mapBoth(fn: (T) -> R) = let { (a, b) -> fn(a) to fn(b) }
val <A, B> Pair<A, B>.swapped get() = second to first
val <T : Comparable<T>> Pair<T, T>.inOrder get() = if (first < second) this else swapped
fun <T> Pair<T, T>.toSet() = setOf(first, second)

fun Iterable<Long>.product() = fold(1L) { acc, i -> acc * i }
fun Sequence<Long>.product() = fold(1L) { acc, i -> acc * i }

fun <A, B, R> crossProduct(sa: Sequence<A>, sb: Sequence<B>, fn: (A, B) -> R) =
    sa.flatMap { a -> sb.map { b -> fn(a, b) } }

infix fun <A, B> Iterable<A>.crossProduct(other: Iterable<B>) =
    flatMap { a -> other.map { b -> a to b } }

inline fun <A, B, R> Iterable<A>.crossProduct(other: Iterable<B>, fn: (A, B) -> R) =
    flatMap { a -> other.map { b -> fn(a, b) } }

inline fun <A, B, R> Iterable<A>.crossProductNotNull(other: Iterable<B>, fn: (A, B) -> R?) =
    flatMap { a -> other.mapNotNull { b -> fn(a, b) } }

infix fun <A, B> Sequence<A>.crossProduct(other: Sequence<B>) =
    flatMap { a -> other.map { b -> a to b } }

fun <A, B, C, R> crossProduct(sa: Sequence<A>, sb: Sequence<B>, sc: Sequence<C>, fn: (A, B, C) -> R) =
    sa.flatMap { a -> sb.flatMap { b -> sc.map { c -> fn(a, b, c) } } }

fun <A, B, C, D, R> crossProduct(
    sa: Sequence<A>, sb: Sequence<B>, sc: Sequence<C>, sd: Sequence<D>, fn: (A, B, C, D) -> R
) = sa.flatMap { a -> sb.flatMap { b -> sc.flatMap { c -> sd.map { d -> fn(a, b, c, d) } } } }

fun <T> List<List<T>>.transpose() = first().indices.map { columnIndex -> map { row -> row[columnIndex] } }
val <T> List<List<T>>.pretty get() = joinToString("\n") { it.joinToString("") }

fun <P, R> ((P) -> R).memoized(cache: MutableMap<P, R> = mutableMapOf()): (P) -> R =
    fun(p: P) =
        if (p in cache) {
            cache[p]!!
        } else {
            val r = this(p)
            cache[p] = r
            r
        }

fun <P1, P2, R> ((P1, P2) -> R).memoized(cache: MutableMap<Pair<P1, P2>, R> = mutableMapOf()): (P1, P2) -> R =
    fun(p1: P1, p2: P2) = (p1 to p2).let {
        if (it in cache) {
            cache[it]!!
        } else {
            val r = this(it.first, it.second)
            cache[it] = r
            r
        }
    }

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).memoized(cache: MutableMap<Triple<P1, P2, P3>, R> = mutableMapOf()): (P1, P2, P3) -> R =
    fun(p1: P1, p2: P2, p3: P3) = Triple(p1, p2, p3).let {
        if (it in cache) {
            cache[it]!!
        } else {
            val r = this(it.first, it.second, it.third)
            cache[it] = r
            r
        }
    }

inline fun <R> produceIf(test: Boolean, producer: () -> R): R? =
    when {
        test -> producer()
        else -> null
    }

fun <T> Sequence<T>.takeWhileInclusive(predicate: (T) -> Boolean) = sequence {
    val it = iterator()
    var cont = true
    while (it.hasNext() && cont) {
        val current = it.next()
        cont = predicate(current)
        yield(current)
    }
}

fun <T> Iterable<T>.takeWhileInclusive(predicate: (T) -> Boolean) = asSequence().takeWhileInclusive(predicate).toList()

fun <T> Iterator<T>.chunked(windowSize: Int) = let { other ->
    object : Iterator<List<T>> {
        private val buffer = ArrayList<T>(windowSize)

        override fun hasNext(): Boolean = buffer.isNotEmpty() || other.hasNext()

        override fun next(): List<T> {
            buffer.clear()
            while (other.hasNext() && buffer.size < windowSize) {
                buffer.add(other.next())
            }
            return buffer.toList()
        }
    }.asSequence()
}

inline fun <A, B, C, D, R> Iterable<A>.zip(
    bs: Iterable<B>,
    cs: Iterable<C>,
    ds: Iterable<D>,
    transform: (a: A, b: B, c: C, d: D) -> R
): List<R> {
    val first = iterator()
    val second = bs.iterator()
    val third = cs.iterator()
    val fourth = ds.iterator()
    val list = ArrayList<R>()
    while (first.hasNext() && second.hasNext() && third.hasNext() && fourth.hasNext()) {
        list.add(transform(first.next(), second.next(), third.next(), fourth.next()))
    }
    return list
}

fun Collection<Int>.product() = fold(1, Int::times)
fun Collection<Long>.product() = fold(1L, Long::times)

fun <R> String.regexMatchWith(pattern: String, mapper: (MatchResult.Destructured) -> R): R =
    Regex(pattern).matchEntire(this)?.destructured.let {
        requireNotNull(it) { "Regex '$pattern' didn't match string: '$this'" }
        mapper(it)
    }

fun String.removePrefix(prefix: String) =
    if (startsWith(prefix)) drop(prefix.length) else error("String '$this' does not start with prefix '$prefix'")

data class Cycle<T, A>(val lead: Int, val cycleLength: Int, val elements: Map<A, T>)
fun <T> Sequence<T>.detectCycle() = detectCycle { it }
fun <T, A> Sequence<T>.detectCycle(selector: (T) -> A) =
    mutableMapOf<A, T>().let { map ->
        withIndex().find { (_, element) ->
            val value = selector(element)
            if (value in map.keys)
                true
            else {
                map[value] = element
                false
            }
        }?.let { (index, element) ->
            val lead = map.keys.indexOf(selector(element))
            Cycle(lead, index - lead, map)
        }
    }

@JvmName("flattenL")
fun <A, B, C> Pair<Pair<A, B>, C>.flatten() = Triple(first.first, first.second, second)
@JvmName("flattenR")
fun <A, B, C> Pair<A, Pair<B, C>>.flatten() = Triple(first, second.first, second.second)

infix fun <T> Set<T>.symmetricDifference(other: Set<T>) = (this + other).toSet() - this.intersect(other.toSet()).toSet()

operator fun <T: Comparable<T>> Iterable<T>.compareTo(other: Iterable<T>): Int {
    val left = iterator()
    val right = other.iterator()
    while (left.hasNext() && right.hasNext()) {
        val result = left.next().compareTo(right.next())
        if (result != 0) return result
    }
    return if (!left.hasNext() && right.hasNext()) -1 else if (!right.hasNext() && left.hasNext()) 1 else 0
}
