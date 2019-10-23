package com.chriswk.sudoku

import io.prometheus.client.Histogram
import kotlin.random.Random
import org.slf4j.LoggerFactory

class SudokuGenerator(val backtrackingSolver: SudokuSolver = SudokuSolver()) {
    companion object {
        val logger = LoggerFactory.getLogger(SudokuGenerator::class.java)
        val generateTimer = Histogram.Builder().name("generate_timer").help("time taken to generate a solution").register()
        val puzzleTimer = Histogram.Builder().name("puzzle_timer").help("time taken to generate puzzle").register()
    }

    suspend fun generateRandomDifficulty(): SudokuGame {
        val generate = generate(Difficulty.values().random())
        logger.info("Generated $generate")
        return generate
    }

    fun generate(difficulty: Difficulty): SudokuGame {
        val total = 81
        val numberOfEmptyCells = when (difficulty) {
            Difficulty.VERY_EASY -> Random.nextInt(total - 37, total - 34)
            Difficulty.EASY -> Random.nextInt(total - 33, total - 30)
            Difficulty.MEDIUM -> Random.nextInt(total - 29, total - 27)
            Difficulty.HARD -> Random.nextInt(total - 26, total - 23)
            Difficulty.VERY_HARD -> Random.nextInt(total - 22, total - 20)
            Difficulty.DIABOLICAL -> Random.nextInt(total - 19, total - 17)
        }
        return generate(numberOfEmptyCells, difficulty)
    }

    fun findDifficulty(quiz: SudokuGrid): Difficulty {
        val remaining = quiz.toIntArray().count { it != 0 }
        return when(remaining) {
            in 17..19 -> Difficulty.DIABOLICAL
            in 20..22 -> Difficulty.VERY_HARD
            in 23..26 -> Difficulty.HARD
            in 27..29 -> Difficulty.MEDIUM
            in 30..33 -> Difficulty.EASY
            else -> Difficulty.VERY_EASY
        }
    }

    fun generate(numberOfEmptyCells: Int, difficulty: Difficulty): SudokuGame {
        val solutionTimer = generateTimer.startTimer()
        val solution = SudokuSolver().solve(SudokuGrid.emptyGrid())
        val solutionTimeTaken = solutionTimer.observeDuration()
        val puzzleTimeTaker = puzzleTimer.startTimer()
        val quiz = eraseCells(solution = solution, numberOfEmptyCells = numberOfEmptyCells)
        val puzzleTimeTaken = puzzleTimeTaker.observeDuration()
        return SudokuGame(solution = solution, puzzle = quiz, difficulty = findDifficulty(quiz), solutionGenerationTime = solutionTimeTaken, puzzleGenerationTime = puzzleTimeTaken)
    }

    fun eraseCells(random: Random = Random.Default, solution: SudokuGrid, numberOfEmptyCells: Int): SudokuGrid {
        val grid = SudokuGrid.of(solution.toIntArray())
        var i = 0
        var attempts = 5
        while (i < numberOfEmptyCells && attempts > 0) {
            val randomRow = random.nextInt(9)
            val randomColumn = random.nextInt(9)
            val cell = grid.getCell(randomRow, randomColumn)
            if (!cell.isEmpty()) {
                val prevValue = cell.value
                cell.value = 0
                val solved = backtrackingSolver.solve(grid)
                if (!solved.toIntArray().contentEquals(solution.toIntArray())) {
                    cell.value = prevValue
                    attempts--
                    i--
                } else {
                    attempts = 5
                }
            } else {
                i--
            }
            i++
        }
        return grid
    }
}
