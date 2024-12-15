package com.example.adventofcode

import android.renderscript.RenderScript.Priority
import java.io.File
import java.io.InputStream
import java.util.LinkedList
import java.util.PriorityQueue
import kotlin.math.exp

class Main09 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val expandedDisk = mutableListOf<String>()
            val freeSpaceMap: MutableMap<Int, MutableList<Chunk>> = hashMapOf() // X -> List of chunk data for free space of *at least* size X
            val fileIndexMap: MutableMap<Int, Chunk> = hashMapOf() // file ID -> list of file chunk data
            var fileCount = 0

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input09.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                var isFile = true
                var fileId = 0
                var strPos = 0
                for (ch in it) {
                    val size = ch.toString().toInt()
                    if (isFile) {
                        for (i in 1..size) {
                            expandedDisk.add(fileId.toString())
                        }
                        fileIndexMap[fileId] = Chunk(
                            type = ChunkType.FILE,
                            fileId = fileId,
                            startPos = strPos,
                            size = size
                        )
                        fileId++
                    } else {
                        for (i in 1..size) {
                            expandedDisk.add(".")
                            if (!freeSpaceMap.containsKey(i)) {
                                freeSpaceMap[i] = mutableListOf()
                            }
                            freeSpaceMap[i]!!.add(Chunk(
                                type = ChunkType.FREE_SPACE,
                                startPos = strPos,
                                size = size
                            ))
                        }
                    }
                    strPos += size
                    isFile = !isFile
                }
                fileCount = fileId
                debugPrint("filecount is $fileCount")
            }

            debugPrint(expandedDisk)
            debugPrint(freeSpaceMap.toString())
            debugPrint(fileIndexMap.toString())

            // call answer methods
            println(doFileCompacting(expandedDisk))
            println(doFileCompacting2(freeSpaceMap, fileIndexMap, fileCount))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun doFileCompacting(expandedDisk: List<String>): Long {
            val newDisk = mutableListOf<String>().apply { addAll(expandedDisk) }
            var endPtr = newDisk.size - 1
            var startPtr = 0
            while (startPtr < endPtr) {
                if (newDisk[endPtr] == ".") {
                    endPtr--
                    continue
                }
                if (newDisk[startPtr] != ".") {
                    startPtr++
                    continue
                }
                newDisk[startPtr] = newDisk[endPtr]
                newDisk[endPtr] = "."
                endPtr--
                startPtr++
            }
            return calculateChecksum(newDisk)
        }

        // kept my debug statements in here, since this algorithm is kinda hard to debug
        private fun doFileCompacting2(
            freeSpaceMap: MutableMap<Int, MutableList<Chunk>>,
            fileIndexMap: MutableMap<Int, Chunk>,
            fileCount: Int
        ): Long {
            var currentFileId = fileCount - 1
            while (currentFileId > 0) {
                val file = fileIndexMap[currentFileId]!!
                val fileSize = file.size
                debugPrint("Looking at fileId $currentFileId, which has a size of $fileSize")
                if (freeSpaceMap.containsKey(fileSize) && freeSpaceMap[fileSize]!![0].startPos < file.startPos) {
                    val leftmostFreeSpaceThatFits = freeSpaceMap[fileSize]!![0]
                    debugPrint("Leftmost free space that fits this file: $leftmostFreeSpaceThatFits")
                    val originalFreeSize = leftmostFreeSpaceThatFits.size
                    file.startPos = leftmostFreeSpaceThatFits.startPos
                    debugPrint("Changing file start pos to: ${leftmostFreeSpaceThatFits.startPos}")
                    val extraSize = originalFreeSize - fileSize
                    for (i in originalFreeSize downTo extraSize + 1) {
                        debugPrint("Remove this free space from map key $i")
                        freeSpaceMap[i]?.remove(leftmostFreeSpaceThatFits)
                        if (freeSpaceMap[i]?.isEmpty() == true) {
                            freeSpaceMap.remove(i)
                        }
                    }
                    for (i in extraSize downTo 1) {
                        debugPrint("Modify this free space's size in map key $i, add $fileSize to startPos")
                        val freeSpace = freeSpaceMap[i]?.find { it == leftmostFreeSpaceThatFits }
                        freeSpace?.let {
                            it.size = extraSize
                            it.startPos += fileSize
                        }
                    }
                }
                debugPrint("New free space map: $freeSpaceMap")
                debugPrint("New file map: $fileIndexMap")
                currentFileId--
            }
            return calculateChecksum(fileIndexMap)
        }

        private fun calculateChecksum(disk: List<String>): Long {
            var sum = 0L
            for ((index, id) in disk.withIndex()) {
                if (id != ".") {
                    sum += index * id.toInt()
                }
            }
            return sum
        }

        private fun calculateChecksum(fileIndexMap: Map<Int, Chunk>): Long {
            var sum = 0L
            for (entry in fileIndexMap) {
                val fileId = entry.key
                val size = entry.value.size
                val startPos = entry.value.startPos
                for (i in startPos..< (startPos + size)) {
                    sum += i * fileId
                }
            }
            return sum
        }

        enum class ChunkType {
            FILE,
            FREE_SPACE
        }

        data class Chunk(
            val type: ChunkType,
            val fileId: Int = -1, // -1 is default for free space
            var startPos: Int, // start position (index) in disk string
            var size: Int // size in blocks
        )
    }
}