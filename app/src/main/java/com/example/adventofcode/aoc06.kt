package com.example.adventofcode

import java.io.File
import java.io.InputStream
import java.util.LinkedList
import java.util.Queue

class Main06 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val labPositions = mutableListOf<List<Char>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input06.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                labPositions.add(it.toCharArray().toList())
            }

            // call method
            println(findDistinctVisitedPositions(labPositions))
            println(getNewObstructionCountBruteForce(labPositions))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun findDistinctVisitedPositions(labPositions: List<List<Char>>): Int {
            var guardPos = Pair(0, 0)
            var guardDirection = 0 // 0 up, 1 right, 2 down, 3 left
            val distinctPosList = mutableSetOf<Int>()
            for (row in labPositions.indices) {
                for (col in labPositions[row].indices) {
                    if (labPositions[row][col] == '^') {
                        guardPos = Pair(row, col)
                    }
                }
            }
            while (guardPos.first >= 0 && guardPos.first < labPositions.size && guardPos.second >=0 && guardPos.second < labPositions[0].size) {
                val checkPos = getNextPosBasedOnDirection(guardDirection, guardPos, labPositions) ?: break
                if (labPositions[checkPos.first][checkPos.second] == '#') {
                    guardDirection = (guardDirection + 1) % 4
                    continue
                } else {
                    distinctPosList.add((checkPos.first * labPositions[0].size) + checkPos.second)
                    guardPos = checkPos
                }
            }

            return distinctPosList.size
        }

        private fun getNextPosBasedOnDirection(dir: Int, originalPos: Pair<Int, Int>, labPositions: List<List<Char>>): Pair<Int,Int>? {
            return when (dir) {
                0 -> {
                    if (originalPos.first - 1 < 0) return null
                    Pair(originalPos.first - 1, originalPos.second)
                }
                1 -> {
                    if (originalPos.second + 1 >= labPositions[originalPos.first].size) return null
                    Pair(originalPos.first, originalPos.second + 1)
                }
                2 -> {
                    if (originalPos.first + 1 >= labPositions.size) return null
                    Pair(originalPos.first + 1, originalPos.second)
                }
                3 -> {
                    if (originalPos.second - 1 < 0) return null
                    Pair(originalPos.first, originalPos.second - 1)
                }
                else -> {
                    println("Major error. idk what happened")
                    null
                }
            }
        }

        private fun getNewObstructionCountBruteForce(labPositions: List<List<Char>>): Int {
            // I brute forced this one, I got lazy
            var loopCount = 0
            var initialGuardPos = Pair(0, 0)
            for (row in labPositions.indices) {
                for (col in labPositions[row].indices) {
                    if (labPositions[row][col] == '^') {
                        initialGuardPos = Pair(row, col)
                    }
                }
            }
            for (newObstructionRow in labPositions.indices) {
                for (newObstructionCol in labPositions[newObstructionRow].indices) {
                    debugPrint("Testing position ($newObstructionRow, $newObstructionCol)")
                    var guardPos = initialGuardPos
                    var guardDirection = 0 // 0 up, 1 right, 2 down, 3 left
                    val distinctPosList = hashMapOf<Int, MutableList<Int>>() // pos to direction
                    while (guardPos.first >= 0 && guardPos.first < labPositions.size && guardPos.second >= 0 && guardPos.second < labPositions[0].size) {
                        val checkPos = getNextPosBasedOnDirection(guardDirection, guardPos, labPositions) ?: break
                        debugPrint("Checking position (${checkPos.first}, ${checkPos.second})")
                        if (labPositions[checkPos.first][checkPos.second] == '#' || checkPos.first == newObstructionRow && checkPos.second == newObstructionCol) {
                            guardDirection = (guardDirection + 1) % 4
                            debugPrint("Hit obstruction, turning direction to $guardDirection")
                            continue
                        } else {
                            val posValue = (checkPos.first * labPositions[0].size) + checkPos.second
                            if (distinctPosList.contains(posValue)) {
                                debugPrint("Position was visited: ${distinctPosList[posValue]!!})")
                                if (distinctPosList[posValue]!!.contains(guardDirection)) {
                                    // this has already been visited in this direction, this causes an infinite loop
                                    debugPrint("Infinite loop detected!!")
                                    loopCount++
                                    break
                                }
                                distinctPosList[posValue]!!.add(guardDirection)
                            } else {
                                distinctPosList[posValue] = mutableListOf(guardDirection)
                            }
                            guardPos = checkPos
                        }
                    }
                }
            }
            return loopCount
        }




        /**
         * This doesn't work, ignore.
         */
        private fun getNewObstructionCount(labPositions: List<List<Char>>): Int {
            var guardPos = Pair(0, 0)
            var guardDirection = 0 // 0 up, 1 right, 2 down, 3 left
            val obstructionsQueue: Queue<Pair<Int, Int>> = LinkedList()
            var fourthCorner = Pair(-1, -1)
            var distinctNewObstaclePositions = mutableSetOf<Pair<Int, Int>>()
            for (row in labPositions.indices) {
                for (col in labPositions[row].indices) {
                    if (labPositions[row][col] == '^') {
                        guardPos = Pair(row, col)
                    }
                }
            }
            while (guardPos.first >= 0 && guardPos.first < labPositions.size && guardPos.second >= 0 && guardPos.second < labPositions[0].size) {
                val checkPos = getNextPosBasedOnDirection(guardDirection, guardPos, labPositions) ?: break
                debugPrint("Walking to $checkPos")
                if (labPositions[checkPos.first][checkPos.second] == '#') {
                    guardDirection = (guardDirection + 1) % 4
                    // we don't add the pos of the obstruction, we add the last place the guard was before they turned
                    obstructionsQueue.add(guardPos)
                    debugPrint("Adding position $guardPos to queue")
                    if (obstructionsQueue.size == 4) {
                        obstructionsQueue.remove()
                    }
                    if (obstructionsQueue.size == 3) {
                        // now we want to calculate the "4th corner" that completes the square of obstructions.
                        // if the guard passes this corner, then we know that we can place an obstruction there to cause a loop
                        if ((obstructionsQueue as LinkedList)[0].first == obstructionsQueue[1].first) {
                            // first two coordinate rows are the same
                            fourthCorner = Pair(obstructionsQueue[2].first, obstructionsQueue[0].second)
                        } else if (obstructionsQueue[0].second == obstructionsQueue[1].second) {
                            // cols the same
                            fourthCorner = Pair(obstructionsQueue[0].first, obstructionsQueue[2].second)
                        }
                        debugPrint("4th corner is now $fourthCorner")
                    }
                    continue
                } else {
                    if (checkPos == fourthCorner) {
                        val newObstaclePos = getNextPosBasedOnDirection(guardDirection, checkPos, labPositions)
                        if (newObstaclePos != null) {
                            distinctNewObstaclePositions.add(newObstaclePos)
                        }
                    }
                    guardPos = checkPos
                }
            }

            debugPrint(distinctNewObstaclePositions)
            return distinctNewObstaclePositions.size
        }
    }
}