package me.nfekete.adventofcode.y2024.day09

import me.nfekete.adventofcode.y2024.common.classpathFile
import java.util.*
import kotlin.collections.ArrayDeque

private data class File(val fileId: Int, val used: Int, val free: Int)
typealias Block = Int? // null means empty

private fun String.parseDiskMap() = toList()
    .windowed(2, 2, true)
    .mapIndexed { index, chars ->
        if (chars.size == 1) {
            File(index, chars[0].digitToInt(), 0)
        } else {
            val (usedS, freeS) = chars
            File(index, usedS.digitToInt(), freeS.digitToInt())
        }
    }

private fun List<File>.toBlocks() =
    flatMap { fileBlock -> List(fileBlock.used) { fileBlock.fileId } + List(fileBlock.free) { null } }

private fun List<Block>.defragment(): List<Int?> = sequence {
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
}.toList()

@JvmName("defragmentFiles")
private fun List<File>.defragment(): List<Block> =
    toBlocks().toMutableList().let { blocks ->
        val queue = toCollection(ArrayDeque())
        while (queue.isNotEmpty()) {
            val file = queue.removeLast()
            val newIndex = blocks.windowed(file.used, 1).indexOfFirst { window -> window.all { it == null } }
            if (newIndex != -1) {
                val oldIndex =
                    blocks.windowed(file.used, 1).indexOfFirst { windows -> windows.all { it == file.fileId } }
                if (newIndex < oldIndex) {
                    (newIndex..<newIndex + file.used).forEach { blocks[it] = file.fileId }
                    (oldIndex..<oldIndex + file.used).forEach { blocks[it] = null }
                }
            }
        }
        blocks
    }


private fun List<Int?>.checksum() = withIndex().fold(0L) { acc, (index, value) ->
    if (value == null) acc else acc + index * value
}

private fun List<Block>.pretty() =
    joinToString("") { block -> block?.toString() ?: "." }

private fun main() {
    "2333133121414131402".parseDiskMap().toBlocks().defragment().toList().checksum()
        .also { println("Sample part 1: $it") }
    "2333133121414131402".parseDiskMap().defragment()
        .checksum().also { println("Sample part 2: $it") }

    val input = classpathFile("day09/input.txt").readLine().parseDiskMap()
    input.toBlocks().defragment().checksum().also { println("Part 1: $it") }
    input.defragment().checksum().also { println("Part 2: $it") }
}
