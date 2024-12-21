package me.nfekete.adventofcode.y2024.common

fun interface Semigroup<T> {
    fun T.combine(other: T): T
}

interface Monoid<T> : Semigroup<T> {
    companion object
    val identity: T
}

fun Monoid.Companion.longAddition() = object : Monoid<Long> {
    override fun Long.combine(other: Long) = this + other
    override val identity get() = 0L
}

fun Monoid.Companion.intAddition() = object : Monoid<Int> {
    override fun Int.combine(other: Int) = this + other
    override val identity get() = 0
}

fun <T> Monoid.Companion.list() = object : Monoid<List<T>> {
    override fun List<T>.combine(other: List<T>) = this + other
    override val identity: List<T> = emptyList()
}

fun <T> Monoid.Companion.set() = object : Monoid<Set<T>> {
    override fun Set<T>.combine(other: Set<T>) = this + other
    override val identity: Set<T> = emptySet()
}

fun Monoid.Companion.booleanOr() = object : Monoid<Boolean> {
    override fun Boolean.combine(other: Boolean) = this || other
    override val identity = false
}

fun Monoid.Companion.booleanAnd() = object : Monoid<Boolean> {
    override fun Boolean.combine(other: Boolean) = this && other
    override val identity = true
}
