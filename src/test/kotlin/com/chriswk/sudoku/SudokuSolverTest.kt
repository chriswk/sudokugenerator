package com.chriswk.sudoku

import io.kotlintest.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import kotlin.system.measureTimeMillis
import org.slf4j.LoggerFactory

class SudokuSolverTest : StringSpec() {
    companion object {
        val logger = LoggerFactory.getLogger(SudokuSolverTest::class.java)
    }
    init {
        "An empty table should find a solution" {
            val grid = IntArray(81) { 0 }
            var solved: IntArray? = null
            val timeTaken = measureTimeMillis {
                solved = SudokuSolver(grid = grid).solve()
            }
            solved shouldNotBe null
            logger.info("Took $timeTaken ms")
            SudokuSolver.isPerfect(solved!!) shouldBe true
            logger.info(grid.toString())
        }
        "Should be able to solve all 500 puzzles" {
            val puzzleToSolution = SudokuSolverTest::class.java.classLoader.getResource("500sudoku.csv")!!.readText(Charsets.UTF_8).lines().mapNotNull {
                if (it.contains(',')) {
                    val (quizString, solutionString) = it.split(",")
                    val quiz = SudokuGrid.fromString(quizString)
                    val solution = SudokuGrid.fromString(solutionString)
                    quiz to solution
                } else {
                    null
                }
            }.toList()
            forAll(puzzleToSolution) { (quiz, solution) ->
                val ourSolution = SudokuSolver(grid = quiz.toIntArray()).solve()
                solution.toIntArray().contentEquals(ourSolution)
            }
        }
    }
}