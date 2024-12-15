package com.example.adventofcode

import java.io.File
import java.io.InputStream
import java.util.LinkedList

class Main12 {
    companion object {
        const val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            // parse input
            val plot = mutableListOf<List<Char>>()

            val inputStream: InputStream = File(
                "/Users/taniabarg/AndroidStudioProjects/AdventOfCode/app/src/main/res/raw/input12.txt"
            ).inputStream()

            inputStream.bufferedReader().forEachLine {
                plot.add(it.toCharArray().toList())
            }

            // call result method
            println(getPrice(plot))
        }

        private fun debugPrint(message: Any?) {
            if (debug) {
                println(message)
            }
        }

        /**
         * Gets the price of the garden plot as a pair: one price based on perimeter (part 1), the other based on
         * number of sides (part 2).
         */
        private fun getPrice(plot: List<List<Char>>): Pair<Long, Long> {
            val visited = MutableList(plot.size) { MutableList(plot[0].size) { false } }
            var priceBasedOnPerimeter = 0L
            var priceBasedOnSides = 0L
            for (row in plot.indices) {
                for (col in plot[row].indices) {
                    if (visited[row][col]) {
                        continue
                    }
                    // visit this region
                    val queue = LinkedList<Pair<Int, Int>>()
                    queue.add(Pair(row, col))
                    debugPrint("Examining ($row, $col) with plant type ${plot[row][col]}")
                    var perim = 0
                    var sides = 0
                    var area = 0
                    while (queue.isNotEmpty()) {
                        val currentPos = queue.pop()
                        if (visited[currentPos.first][currentPos.second]) continue
                        debugPrint("visiting (${currentPos.first}, ${currentPos.second})")
                        area++
                        val values = visitPlant(plot, currentPos.first, currentPos.second, queue)
                        perim += values.first
                        sides += values.second
                        visited[currentPos.first][currentPos.second] = true
                    }
                    debugPrint("Area of this region: $area")
                    debugPrint("Perim of this region: $perim")
                    debugPrint("Sides of this region: $sides")
                    priceBasedOnPerimeter += perim * area
                    priceBasedOnSides += sides * area
                }
            }
            return Pair(priceBasedOnPerimeter, priceBasedOnSides)
        }

        /**
         * Visits the plant and updates the queue with more plants to be visited.
         * Returns the required updates to both the perimeter and the number of sides.
         *
         * Note: number of sides = number of corners.
         * You have a corner if the corner-adjacent (e.g. top and right, right and bottom, bottom and left, left and top)
         * plants are both of a different type ("outie" corner); OR if they are the same but the diagonal in that direction
         * is different ("innie" corner). See below:
         *
         * AAAC
         * ABAA
         * XXXX
         *
         * The "A" at (1, 2) has 4 possible places to investigate for corners. In the top-right direction, we can see that there is a corner
         * since the top adjacent cell also has an A, and so does the right cell, but the diagonal is different (C) - this matches
         * the pattern for an "innie" corner.
         * The A at (1, 3) has two "outie" corners, at the top right and the bottom right. (If looking directly next to a cell takes you
         * out of bounds, that counts as it having a "different" plant type)
         */
        private fun visitPlant(
            plot: List<List<Char>>,
            row: Int,
            col: Int,
            queue: LinkedList<Pair<Int, Int>>
        ): Pair<Int, Int> {
            val currentPlant = plot[row][col]
            var sides = 0
            var perim = 0
            // evaluate whether or not bordering plants are same or different (true = same)
            val adj1 = row - 1 >= 0 && plot[row - 1][col] == currentPlant // top
            val adj2 = row - 1 >= 0 && col + 1 < plot[0].size && plot[row - 1][col + 1] == currentPlant // top right
            val adj3 = col + 1 < plot[0].size && plot[row][col + 1] == currentPlant // right
            val adj4 = row + 1 < plot.size && col + 1 < plot[0].size && plot[row + 1][col + 1] == currentPlant // bottom right
            val adj5 = row + 1 < plot.size && plot[row + 1][col] == currentPlant // bottom
            val adj6 = row + 1 < plot.size && col - 1 >= 0 && plot[row + 1][col - 1] == currentPlant // bottom left
            val adj7 = col - 1 >= 0 && plot[row][col - 1] == currentPlant // left
            val adj8 = row - 1 >= 0 && col - 1 >= 0 && plot[row - 1][col - 1] == currentPlant // top left
            debugPrint("From top clockwise: $adj1, $adj2, $adj3, $adj4, $adj5, $adj6, $adj7, $adj8")
            // add sides according to above analysis
            if ((!adj1 && !adj3) || (adj1 && !adj2 && adj3)) { debugPrint("Corner 1 exists"); sides++ }
            if ((!adj3 && !adj5) || (adj3 && !adj4 && adj5)) { debugPrint("Corner 2 exists"); sides++ }
            if ((!adj5 && !adj7) || (adj5 && !adj6 && adj7)) { debugPrint("Corner 3 exists"); sides++ }
            if ((!adj7 && !adj1) || (adj7 && !adj8 && adj1)) { debugPrint("Corner 4 exists"); sides++ }
            // add to queue for same plants on right, top, bottom, left
            if (adj1) queue.add(Pair(row - 1, col)) else perim++
            if (adj3) queue.add(Pair(row, col + 1)) else perim++
            if (adj5) queue.add(Pair(row + 1, col)) else perim++
            if (adj7) queue.add(Pair(row, col - 1)) else perim++
            return Pair(perim, sides)
        }
    }
}