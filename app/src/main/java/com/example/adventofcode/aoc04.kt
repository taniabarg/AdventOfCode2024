package com.example.adventofcode

import java.io.File
import java.io.InputStream

class Main04 {
    companion object {
        const val debug = true

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val wordSearch = mutableListOf<List<Char>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input04.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                wordSearch.add(it.toCharArray().toList())
            }

            // call method
            println(getXmasCount(wordSearch))
            println(getXmas2Count(wordSearch))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun getXmasCount(wordSearch: List<List<Char>>): Int {
            var row = 0
            var col = 0
            var count = 0
            while (row < wordSearch.size) {
                while (col < wordSearch[row].size) {
                    if (wordSearch[row][col] == 'X') {
                        // search for "MAS" in all directions
                        count += if (isXmasInDirection(wordSearch, row, col, 0, 1)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, 0, -1)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, 1, 0)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, -1, 0)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, 1, 1)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, 1, -1)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, -1, 1)) 1 else 0
                        count += if (isXmasInDirection(wordSearch, row, col, -1, -1)) 1 else 0
                    }
                    col++
                }
                col = 0
                row++
            }
            return count
        }

        // Given (row, col) of "X", find "MAS" in a common direction
        private fun isXmasInDirection(
            wordSearch: List<List<Char>>,
            xRow: Int,
            xCol: Int,
            rowModifier: Int, // +1, 0, -1 depending on which direction we are searching in
            colModifier: Int // same as rowModifier
        ): Boolean {
            val searchStr = arrayOf('X', 'M', 'A', 'S')
            var i = 1 // used to index into XMAS array above and for a multiplier on the modifier
            while (i < searchStr.size) {
                val searchRow = xRow + (i * rowModifier)
                val searchCol = xCol + (i * colModifier)
                if (searchRow >= wordSearch.size || searchRow < 0 || searchCol >= wordSearch[searchRow].size || searchCol < 0) {
                    // hit a wall
                    return false
                } else if (wordSearch[searchRow][searchCol] == searchStr[i]) {
                    i++
                } else {
                    // failed to find XMAS in this direction
                    return false
                }
            }
            return true
        }

        private fun getXmas2Count(wordSearch: List<List<Char>>): Int {
            // centered around the 'A', see if M/S or S/M exists in one diagonal, then the other
            var row = 0
            var col = 0
            var count = 0
            while (row < wordSearch.size) {
                while (col < wordSearch[row].size) {
                    if (wordSearch[row][col] == 'A') {
                        // (below line assumes each row is the same size)
                        if (row + 1 >= wordSearch.size || row - 1 < 0 || col + 1 >= wordSearch[row].size || col - 1 < 0) {
                            col++
                            continue
                        }
                        val diag1_1 = wordSearch[row + 1][col + 1]
                        val diag1_2 = wordSearch[row - 1][col - 1]
                        val diag2_1 = wordSearch[row - 1][col + 1]
                        val diag2_2 = wordSearch[row + 1][col - 1]
                        if ((diag1_1 == 'M' && diag1_2 == 'S' || diag1_1 == 'S' && diag1_2 == 'M')
                            && (diag2_1 == 'M' && diag2_2 == 'S' || diag2_1 == 'S' && diag2_2 == 'M')) {
                            count++
                        }
                    }
                    col++
                }
                col = 0
                row++
            }
            return count
        }
    }
}