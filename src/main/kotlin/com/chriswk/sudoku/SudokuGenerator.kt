package com.chriswk.sudoku

import io.prometheus.client.Histogram
import kotlin.random.Random
import org.slf4j.LoggerFactory

class SudokuGenerator(val backtrackingSolver: SudokuSolver = SudokuSolver()) {
    companion object {
        val logger = LoggerFactory.getLogger(SudokuGenerator::class.java)
        val generateTimer = Histogram.Builder().name("generate_timer").help("time taken to generate a puzzle").register()
    }
    fun generate(difficulty: Difficulty): SudokuGame {
        val timer = generateTimer.startTimer()
        val game = when (difficulty) {
            Difficulty.VERY_EASY -> generate(Random.nextInt(42, 48))
            Difficulty.EASY -> generate(Random.nextInt(38, 42))
            Difficulty.MEDIUM -> generate(Random.nextInt(32, 37))
            Difficulty.HARD -> generate(Random.nextInt(28, 32))
            Difficulty.VERY_HARD -> generate(Random.nextInt(24, 27))
            Difficulty.DIABOLICAL -> generate(Random.nextInt(17, 24))
        }
        timer.observeDuration()
        return game
    }

    fun generate(numberOfEmptyCells: Int): SudokuGame {
        val solution = SudokuSolver().solve(SudokuGrid.emptyGrid())
        val quiz = eraseCells(solution = solution.toIntArray(), numberOfEmptyCells = numberOfEmptyCells)
        return SudokuGame(quiz, solution)
    }

    fun eraseCells(random: Random = Random.Default, solution: IntArray, numberOfEmptyCells: Int): SudokuGrid {
        val grid = SudokuGrid.of(solution)
        var i = 0
        while (i < numberOfEmptyCells) {
            val randomRow = random.nextInt(9)
            val randomColumn = random.nextInt(9)

            val cell = grid.getCell(randomRow, randomColumn)
            if (!cell.isEmpty()) {
                val prevValue = cell.value
                cell.value = 0
                val solved = backtrackingSolver.solve(grid)
                if (!solved.toIntArray().contentEquals(solution)) {
                    cell.value = prevValue
                    i--
                }
            } else {
                i--
            }
            i++
        }
        return grid
    }
}
