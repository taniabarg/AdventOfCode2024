package com.example.adventofcode

import java.io.File
import java.io.InputStream

class Main07 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val calibrations = hashMapOf<Long, List<Long>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input07.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                val testValueAndOperands = it.split(':')
                calibrations[testValueAndOperands[0].trim().toLong()] = testValueAndOperands[1].split(' ').mapNotNull {
                    value -> value.trim().toLongOrNull()
                }
            }

            // call method
            println(getValidOperationSum(calibrations, includeConcat = false))
            println(getValidOperationSum(calibrations, includeConcat = true))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun getValidOperationSum(calibrations: Map<Long, List<Long>>, includeConcat: Boolean): Long {
            var sum = 0L
            for (cal in calibrations) {
                if (testCalibration(cal.key, cal.value, includeConcat)) {
                    sum += cal.key
                }
            }
            return sum
        }

        private fun testCalibration(testValue: Long, operands: List<Long>, includeConcat: Boolean): Boolean {
            if (operands.size == 1) {
                return (testValue == operands[0])
            } else {
                val newListAdd = mutableListOf(operands[0] + operands[1]).apply { addAll(operands.subList(2, operands.size)) }
                val newListMultiply = mutableListOf(operands[0] * operands[1]).apply { addAll(operands.subList(2, operands.size)) }
                val isValid = testCalibration(testValue, newListAdd, includeConcat) || testCalibration(testValue, newListMultiply, includeConcat)
                if (includeConcat) {
                    val newListConcat = mutableListOf((operands[0].toString() + operands[1].toString()).toLong()).apply { addAll(operands.subList(2, operands.size)) }
                    return isValid || testCalibration(testValue, newListConcat, true)
                } else {
                    return isValid
                }
            }
        }
    }
}