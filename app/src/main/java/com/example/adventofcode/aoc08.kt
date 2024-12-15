package com.example.adventofcode

import java.io.File
import java.io.InputStream

class Main08 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val positions = mutableListOf<List<Char>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input08.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                positions.add(it.toCharArray().toList())
            }

            // call method
            println(countUniqueAntinodes(positions, includeFrequencies = false))
            println(countUniqueAntinodes(positions, includeFrequencies = true))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun countUniqueAntinodes(
            positions: List<List<Char>>,
            includeFrequencies: Boolean
        ): Int {
            val uniqueAntinodes = mutableSetOf<Pair<Int, Int>>()
            val antennaPositions = hashMapOf<Char, MutableList<Pair<Int, Int>>>()
            // Parse the list and populate antenna map
            for (row in positions.indices) {
                for (col in positions[row].indices) {
                    val ch = positions[row][col]
                    if (ch != '.') {
                        if (!antennaPositions.containsKey(ch)) {
                            antennaPositions[ch] = mutableListOf()
                        }
                        antennaPositions[ch]!!.add(Pair(row, col))
                    }
                }
            }
            for (entry in antennaPositions) {
                val positionList = entry.value
                for (i in positionList.indices) {
                    var j = i + 1
                    while (j < positionList.size) {
                        val rowDistance = positionList[j].first - positionList[i].first
                        val colDistance = positionList[j].second - positionList[i].second
                        var antiPos1 = Pair(positionList[j].first + rowDistance, positionList[j].second + colDistance)
                        while (antiPos1.first >= 0 && antiPos1.first < positions.size && antiPos1.second >= 0 && antiPos1.second < positions[0].size) {
                            uniqueAntinodes.add(antiPos1)
                            if (!includeFrequencies) break
                            antiPos1 = Pair(antiPos1.first + rowDistance, antiPos1.second + colDistance)
                        }
                        var antiPos2 = Pair(positionList[i].first - rowDistance, positionList[i].second - colDistance)
                        while (antiPos2.first >= 0 && antiPos2.first < positions.size && antiPos2.second >= 0 && antiPos2.second < positions[0].size) {
                            uniqueAntinodes.add(antiPos2)
                            if (!includeFrequencies) break
                            antiPos2 = Pair(antiPos2.first - rowDistance, antiPos2.second - colDistance)
                        }
                        j++
                    }
                }
                if (includeFrequencies && positionList.size > 1) {
                    uniqueAntinodes.addAll(positionList)
                }
            }

            return uniqueAntinodes.size
        }
    }
}