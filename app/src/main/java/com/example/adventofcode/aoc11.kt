package com.example.adventofcode

import java.io.File
import java.io.InputStream

class Main11 {
    companion object {
        const val debug = true

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            var stones: List<String> = listOf()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input11.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                stones = it.split(" ")
            }

            // call method
            println(blinkAtStones(stones, 25).size)
            println(blinkAtStonesOptimized(stones, 75))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun blinkAtStones(stones: List<String>, blinkCount: Int): List<String> {
            val newStones = mutableListOf<String>()
            newStones.addAll(stones)
            for (b in 1..blinkCount) {
                var i = 0
                var size = newStones.size
                while (i < size) {
                    val stone = newStones[i]
                    if (stone == "0") {
                        newStones[i] = "1"
                        i++
                    } else if (stone.length % 2 == 0) {
                        // split the string
                        val splitAt = stone.length / 2
                        val stone1 = stone.substring(IntRange(0, splitAt - 1))
                        var stone2 = stone.substring(IntRange(splitAt, stone.length - 1)).trimStart('0')
                        if (stone2.isEmpty()) stone2 = "0"
                        newStones[i] = stone1
                        newStones.add(i + 1, stone2)
                        i += 2
                    } else {
                        newStones[i] = (stone.toLong() * 2024L).toString()
                        i++
                    }
                    size = newStones.size
                }
            }
            return newStones
        }

        // Got help from https://github.com/polarfish/advent-of-code-2024/blob/a945c1eb013391ec7619223136f0e9c7ae29ae3a/src/main/java/Day11.java
        // Reworked their solution into mine. Haven't done a DP question in a while and was struggling :')
        private fun blinkAtStone(stone: String, blinkCount: Int, cache: HashMap<String, HashMap<Int, Long>>): Long {
            if (blinkCount == 0) {
                return 1
            }
            if (cache.containsKey(stone) && cache[stone]!!.containsKey(blinkCount)) {
                return cache[stone]!![blinkCount]!!
            } else if (!cache.containsKey(stone)) {
                cache[stone] = hashMapOf()
            }

            val result: Long
            if (stone == "0") {
                result = blinkAtStone("1", blinkCount - 1, cache)
            } else if (stone.length % 2 == 0) {
                val splitAt = stone.length / 2
                val stone1 = stone.substring(IntRange(0, splitAt - 1))
                var stone2 = stone.substring(IntRange(splitAt, stone.length - 1)).trimStart('0')
                if (stone2.isEmpty()) stone2 = "0"
                result = blinkAtStone(stone1, blinkCount - 1, cache) +
                        blinkAtStone(stone2, blinkCount - 1, cache)
            } else {
                result = blinkAtStone((stone.toLong() * 2024L).toString(), blinkCount - 1, cache)
            }
            cache[stone]!![blinkCount] = result
            return result
        }

        private fun blinkAtStonesOptimized(stones: List<String>, blinkCount: Int): Long {
            val cache = hashMapOf<String, HashMap<Int, Long>>() // stone value -> map of (blinks, score)
            var score = 0L
            for (stone in stones) {
                score += blinkAtStone(stone, blinkCount, cache)
            }
            return score
        }
    }
}