package com.example.adventofcode

import java.io.File
import java.io.InputStream
import kotlin.math.abs

class Main01 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val list1 = mutableListOf<Int>()
            val list2 = mutableListOf<Int>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input01.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                val listVals = it.split(Regex("\\s+"))
                list1.add(listVals[0].trim().toInt())
                list2.add(listVals[1].trim().toInt())
            }

            // call method
            //println(findDistanceSum(list1, list2))
            println(findSimilarityScore(list1, list2))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun findDistanceSum(list1: List<Int>, list2: List<Int>): Int {
            if (list1.size != list2.size) {
                println("Size error")
                return -1
            }
            val sortedList1 = list1.sorted()
            val sortedList2 = list2.sorted()
            var totalDistance = 0
            for (i in sortedList1.indices) {
                totalDistance += abs(sortedList1[i] - sortedList2[i])
            }
            return totalDistance
        }

        private fun findSimilarityScore(list1: List<Int>, list2: List<Int>): Int {
            var score = 0
            var index1 = 0
            var index2 = 0
            val sortedList1 = list1.sorted()
            val sortedList2 = list2.sorted()
            debugPrint(sortedList1)
            debugPrint(sortedList2)
            while (index1 < sortedList1.size && index2 < sortedList2.size) {
                val a = sortedList1[index1]
                val b = sortedList2[index2]
                debugPrint("a is $a, b is $b")
                if (a < b) {
                    // a is not in list2, otherwise we would have seen it already
                    debugPrint("incrementing a")
                    index1++
                } else if (a > b) {
                    // b is not in list1
                    debugPrint("incrementing b")
                    index2++
                } else {
                    // they are equal, so add to the similarity score
                    debugPrint("they are equal")
                    val currentNum = a // or b, doesn't matter
                    var list1Count = 0
                    var list2Count = 0
                    while (sortedList2[index2] == currentNum) {
                        list2Count++
                        index2++
                        if (index2 >= sortedList2.size) {
                            break
                        }
                    }
                    while(sortedList1[index1] == currentNum) {
                        list1Count++
                        index1++
                        if (index1 >= sortedList1.size) {
                            break
                        }
                    }
                    debugPrint("this number appears $list1Count times in list1, $list2Count times in list 2")
                    debugPrint("add ${(list2Count * currentNum) * list1Count} to score")
                    score += (list2Count * currentNum) * list1Count
                    debugPrint("NEW SCORE: $score")
                }
            }
            return score
        }
    }
}