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
        val numberOfEmptyCells = when (difficulty) {
            Difficulty.VERY_EASY -> Random.nextInt(33, 39)
            Difficulty.EASY -> Random.nextInt(39, 44)
            Difficulty.MEDIUM -> Random.nextInt(44, 48)
            Difficulty.HARD -> Random.nextInt(49, 53)
            Difficulty.VERY_HARD -> Random.nextInt(54, 57)
            Difficulty.DIABOLICAL -> Random.nextInt(57, 64)
        }
        return generate(numberOfEmptyCells, difficulty)
    }

    fun generate(numberOfEmptyCells: Int, difficulty: Difficulty): SudokuGame {
        val solutionTimer = generateTimer.startTimer()
        val solution = SudokuSolver().solve(SudokuGrid.emptyGrid())
        val solutionTimeTaken = solutionTimer.observeDuration()
        val puzzleTimeTaker = puzzleTimer.startTimer()
        val (quiz, solvedTo) = eraseCells(solution = solution, numberOfEmptyCells = numberOfEmptyCells)
        val puzzleTimeTaken = puzzleTimeTaker.observeDuration()
        return SudokuGame(solution = solvedTo, puzzle = quiz, difficulty = difficulty, solutionGenerationTime = solutionTimeTaken, puzzleGenerationTime = puzzleTimeTaken)
    }

    fun eraseCells(random: Random = Random.Default, solution: SudokuGrid, numberOfEmptyCells: Int): Pair<SudokuGrid, SudokuGrid> {
        val grid = SudokuGrid.of(solution.toIntArray())
        var i = 0
        while (i < numberOfEmptyCells) {
            val randomRow = random.nextInt(9)
            val randomColumn = random.nextInt(9)
            val cell = grid.getCell(randomRow, randomColumn)
            if (!cell.isEmpty()) {
                cell.value = 0
            } else {
                i--
            }
            i++
        }
        return (grid to backtrackingSolver.solve(grid))
    }
}
