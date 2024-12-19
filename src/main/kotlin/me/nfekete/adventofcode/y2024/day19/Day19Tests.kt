package me.nfekete.adventofcode.y2024.day19

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class Day19Tests {
    @TestFactory
    fun test() =
        TowelCombiner(setOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")).let { towelCombiner ->
            listOf(
                "brwrr" to 2,
                "bggr" to 1,
                "gbbr" to 4,
                "rrbgbr" to 6,
                "bwurrg" to 1,
                "ubwu" to 0,
                "bbrgwb" to 0,
            ).map { (design, expected) ->
                DynamicTest.dynamicTest(design) {
                    Assertions.assertEquals(expected.toLong(), towelCombiner.howManyWaysFor(design))
                }
            }
        }
}
