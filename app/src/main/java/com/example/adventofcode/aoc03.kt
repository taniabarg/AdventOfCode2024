package com.example.adventofcode

import java.io.File
import java.io.InputStream

class Main03 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            var input = ""

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input03.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                input += it
            }

            // call method
//            println(addMulResults(lines))
            println(addMulResultsDoDont(input))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun addMulResults(input: List<String>): Int {
            var result = 0
            val regex = Regex("mul\\((\\d+),(\\d+)\\)")
            for (line in input) {
                val matches = regex.findAll(line)
                for (match in matches) {
                    result += match.groupValues[1].toInt() * match.groupValues[2].toInt()
                }
            }
            return result
        }

        private fun addMulResultsDoDont(input: String): Int {
            var result = 0
            val regex = Regex("mul\\((\\d+),(\\d+)\\)")
            val regexDo = Regex("do\\(\\)")
            val regexDont = Regex("don\'t\\(\\)")
            val dos = regexDo.findAll(input)
            val donts = regexDont.findAll(input)
            val ranges = mutableListOf<DoDont>()
            for (doMatch in dos) {
                ranges.add(DoDont(isDo = true, startIndex = doMatch.range.first))
            }
            for (dontMatch in donts) {
                ranges.add(DoDont(isDo = false, startIndex = dontMatch.range.first))
            }
            ranges.sortBy { it.startIndex }

            // get matches and add result if in the right range
            val matches = regex.findAll(input)
            for (match in matches) {
                var doStatus = true
                for (range in ranges) {
                    if (match.range.first < range.startIndex) {
                        break
                    }
                    doStatus = range.isDo
                }
                result += if (doStatus) match.groupValues[1].toInt() * match.groupValues[2].toInt() else 0
            }
            return result
        }

        data class DoDont(
            val isDo: Boolean,
            val startIndex: Int
        )
    }
}