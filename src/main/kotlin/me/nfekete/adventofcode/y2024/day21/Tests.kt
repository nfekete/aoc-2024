package me.nfekete.adventofcode.y2024.day21

import me.nfekete.adventofcode.y2024.common.flatten
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class Tests {
    @TestFactory
    fun tests() =
        listOf(
            1 to "029A" to 12,
            1 to "980A" to 12,
            1 to "179A" to 14,
            1 to "456A" to 12,
            1 to "379A" to 14,
            2 to "029A" to 28,
            3 to "029A" to 68,
            3 to "980A" to 60,
            3 to "179A" to 68,
            3 to "456A" to 64,
            3 to "379A" to 64,
        ).map {
            DynamicTest.dynamicTest(it.toString()) {
                val (levels, code, expected) = it.flatten()
                val actual = RemoteControl(levels).enter(code)
                Assertions.assertEquals(expected.toLong(), actual)
            }
        }
}
