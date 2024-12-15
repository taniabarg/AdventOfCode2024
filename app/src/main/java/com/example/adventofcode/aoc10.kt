package com.example.adventofcode

import java.io.File
import java.io.InputStream

class Main10 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val trailMap = mutableListOf<List<Int>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input10.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                trailMap.add(it.toCharArray().map { ch -> ch.toString().toInt() })
            }

            // call method
            println(getTrailheadScoreSum(trailMap))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        // Returns answer for both part 1 and 2
        private fun getTrailheadScoreSum(trailMap: List<List<Int>>): Pair<Int, Int> {
            var score = 0
            var rating = 0
            for (i in trailMap.indices) {
                for (j in trailMap[i].indices) {
                    if (trailMap[i][j] == 0) {
                        val paths = searchForPath(trailMap, Pair(i, j), 0)
                        rating += paths.size
                        score += paths.toSet().size
                    }
                }
            }
            return Pair(score, rating)
        }

        // Returns list of 9s that are reachable from this point (pos). Will include duplicates, i.e. diff paths that lead to the same point
        // Size of the list returned gets the rating, converting to a set to remove duplicates and getting size will return the score
        private fun searchForPath(
            trailMap: List<List<Int>>,
            pos: Pair<Int, Int>,
            searchHeight: Int, // height we are currently searching for
        ): List<Pair<Int, Int>> {
            if (pos.first < 0 || pos.first >= trailMap.size || pos.second < 0 || pos.second >= trailMap[0].size) {
                return listOf()
            }
            if (trailMap[pos.first][pos.second] == searchHeight) {
                if (searchHeight == 9) {
                    return listOf(pos)
                }
                return searchForPath(trailMap, Pair(pos.first - 1, pos.second), searchHeight + 1) +
                        searchForPath(trailMap, Pair(pos.first + 1, pos.second), searchHeight + 1) +
                        searchForPath(trailMap, Pair(pos.first, pos.second - 1), searchHeight + 1) +
                        searchForPath(trailMap, Pair(pos.first, pos.second + 1), searchHeight + 1)
            }
            return listOf()
        }
    }
}