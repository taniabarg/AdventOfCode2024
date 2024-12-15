package com.example.adventofcode

import java.io.File
import java.io.InputStream
import kotlin.math.abs

class Main02 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val reports = mutableListOf<List<Int>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input02.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                val levels = it.split(Regex("\\s+"))
                reports.add(levels.map { level -> level.trim().toInt() })
            }

            // call method
//            println(getSafeReportsCount(reports))
            println(getSafeReportsCountWithDampener(reports))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        private fun isReportSafe(report: List<Int>): Boolean {
            var isIncreasing: Boolean? = null
            var isSafe = true
            for (i in 0..report.size - 2) {
                if (isIncreasing == true) {
                    if (report[i + 1] <= report[i] || !checkSafeDifference(report[i + 1], report[i])) {
                        // unsafe because the difference is out of range or the direction switched
                        isSafe = false
                        break
                    }
                } else if (isIncreasing == false) {
                    if (report[i + 1] >= report[i] || !checkSafeDifference(report[i + 1], report[i])) {
                        // unsafe because the difference is out of range or the direction switched
                        isSafe = false
                        break
                    }
                } else if (isIncreasing == null) {
                    // a direction is not set yet
                    if (report[i + 1] > report[i]) {
                        isIncreasing = true
                    } else if (report[i + 1] < report[i]) {
                        isIncreasing = false
                    } else {
                        // equal, not safe
                        isSafe = false
                        break
                    }
                    if (!checkSafeDifference(report[i+1], report[i])) {
                        isSafe = false
                        break
                    }
                }
            }
            if (isSafe) {
                return true
            }
            return false
        }

        private fun getSafeReportsCount(reports: List<List<Int>>): Int {
            var count = 0
            for (report in reports) {
                if (isReportSafe(report)) {
                    count++
                }
            }
            return count
        }

        private fun getSafeReportsCountWithDampener(reports: List<List<Int>>): Int {
            var count = 0
            for (report in reports) {
                var safeFound = false
                if (isReportSafe(report) || isReportSafe(report.subList(1, report.size)) || isReportSafe(report.subList(0, report.size - 1))) {
                    count++
                    continue
                }
                for (i in 1..report.size - 2) {
                    if (isReportSafe(report.subList(0, i) + report.subList(i + 1, report.size))) {
                        safeFound = true
                        break
                    }
                }
                if (safeFound) {
                    count++
                }
            }
            return count
        }

        private fun checkSafeDifference(value1: Int, value2: Int): Boolean {
            return abs(value1 - value2) in 1..3
        }

    }
}