package me.nfekete.adventofcode.y2024.day09

import me.nfekete.adventofcode.y2024.common.classpathFile
import java.util.*

private data class FileBlock(val fileId: Int, val used: Int, val free: Int)

private fun String.parseDiskMap() = toList()
    .windowed(2, 2, true)
    .mapIndexed { index, chars ->
        if (chars.size == 1) {
            FileBlock(index, chars[0].digitToInt(), 0)
        } else {
            val (usedS, freeS) = chars
            FileBlock(index, usedS.digitToInt(), freeS.digitToInt())
        }
    }

private fun List<FileBlock>.toBlocks() =
    asSequence().flatMap { fileBlock -> List(fileBlock.used) { fileBlock.fileId } + List(fileBlock.free) { null } }

private fun Sequence<Int?>.defragment(): Sequence<Int> = sequence {
    val blocks = this@defragment.toCollection(LinkedList())
    while (blocks.isNotEmpty()) {
        val block = blocks.removeFirst()
        if (block != null) {
            yield(block)
        } else {
            var last = blocks.removeLast()
            while (last == null) {
                last = blocks.removeLast()
            }
            yield(last)
        }
    }
}

private fun Sequence<Int>.checksum() = withIndex().fold(0L) { acc, (index, value) -> acc + index * value }

private fun main() {

//    "2333133121414131402".parseDiskMap().toBlocks().defragment().checksum().also { println("Sample: $it") }
    val input = classpathFile("day09/input.txt").readLine().parseDiskMap()
    input.toBlocks().defragment().checksum().also { println("Part 1: $it") }
}
