package com.example.adventofcode

import java.io.File
import java.io.InputStream
import java.util.LinkedList
import kotlin.math.floor

class Main05 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val rules = hashMapOf<Int, MutableList<Int>>() // page -> pages that should come after
            val reversedRules = hashMapOf<Int, MutableList<Int>>() // page -> pages that should come before
            val updates = mutableListOf<List<Int>>()

            val inputStream1: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input05_1.txt"
            ).inputStream()

            inputStream1.bufferedReader().forEachLine {
                // transform input 1 into maps (before->after, after->before)
                val beforeAndAfter = it.split('|').map { num -> num.trim().toInt() }
                if (!rules.containsKey(beforeAndAfter[0])) {
                    rules[beforeAndAfter[0]] = mutableListOf()
                }
                rules[beforeAndAfter[0]]!!.add(beforeAndAfter[1])
                if (!reversedRules.containsKey(beforeAndAfter[1])) {
                    reversedRules[beforeAndAfter[1]] = mutableListOf()
                }
                reversedRules[beforeAndAfter[1]]!!.add(beforeAndAfter[0])
            }

            val inputStream2: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input05_2.txt"
            ).inputStream()

            inputStream2.bufferedReader().forEachLine {
                // list of updates with pages in each update
                updates.add(it.split(',').map { page -> page.trim().toInt() })
            }

            // call method
            println(getValidUpdateMiddleSum(updates, rules))
            println(getCorrectedUpdateMiddleSum(updates, rules, reversedRules))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun getValidUpdateMiddleSum(updates: List<List<Int>>, rules: Map<Int, MutableList<Int>>): Int {
            var sum = 0
            for (update in updates) {
                val reverseIndexUpdate = hashMapOf<Int, Int>() // page number to index
                var middleNumber = 0
                for (i in update.indices) {
                    reverseIndexUpdate[update[i]] = i
                    if (i == floor(update.size.toDouble() / 2).toInt()) {
                        middleNumber = update[i]
                    }
                }
                var rulesBroken = false
                for (page in update) {
                    val afterList = rules[page] ?: listOf()
                    for (afterPage in afterList) {
                        val valid = (reverseIndexUpdate.containsKey(afterPage) && reverseIndexUpdate[afterPage]!! > reverseIndexUpdate[page]!!) || !reverseIndexUpdate.containsKey(afterPage)
                        if (!valid) {
                            rulesBroken = true
                            break
                        }
                    }
                    if (rulesBroken) {
                        break
                    }
                }
                if (rulesBroken) {
                    continue
                } else {
                    sum += middleNumber
                }
            }
            return sum
        }

        private fun getCorrectedUpdateMiddleSum(
            updates: List<List<Int>>,
            rules: Map<Int, MutableList<Int>>,
            reversedRules: Map<Int, MutableList<Int>>
        ): Int {
            var sum = 0
            for (update in updates) {
                // I compromised on efficiency because I was lazy (oops). The more efficient way would be to check if it's valid
                // before attempting to re-sort it, and also to pass in the reverseIndexMap so lookup in the update list would be faster
                val newUpdate = sortAndMergeListChunks(update, rules, reversedRules)
                if (newUpdate != update) {
                    sum += newUpdate[floor(newUpdate.size.toDouble() / 2).toInt()]
                }
            }
            return sum
        }

        private fun sortAndMergeListChunks(
            update: List<Int>,
            rules: Map<Int, MutableList<Int>>,
            reversedRules: Map<Int, MutableList<Int>>
        ): List<Int> {
            if (update.size == 1) {
                return update
            } else if (update.isEmpty()) {
                return mutableListOf()
            }
            val page = update[0] // pick first element as the "anchor" / middle point
            val afterList = mutableListOf<Int>()
            val beforeList = mutableListOf<Int>()
            for (pageIndex in 1 ..< update.size) {
                if (rules[page]?.contains(update[pageIndex]) == true) {
                    // all these must go before the anchor point
                    afterList.add(update[pageIndex])
                } else if (reversedRules[page]?.contains(update[pageIndex]) == true) {
                    // all these must go before the before point
                    beforeList.add(update[pageIndex])
                } else {
                    // doesn't matter, just stick it in the afterList - this case never happens anyway
                    afterList.add(update[pageIndex])
                }
            }
            // recurse function on the after/before lists and combine
            return sortAndMergeListChunks(beforeList, rules, reversedRules) + update[0] + sortAndMergeListChunks(afterList, rules, reversedRules)
        }
    }
}