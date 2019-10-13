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
            val grid = SudokuGrid.emptyGrid()
            var solved: SudokuGrid? = null
            val solver = SudokuSolver()
            val timeTaken = measureTimeMillis {
                solved = solver.solve(grid)
            }
            solved shouldNotBe null
            logger.info("Took $timeTaken ms")
            solved!!.isPerfect() shouldBe true
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
            val solver = SudokuSolver()
            forAll(puzzleToSolution) { (quiz, solution) ->
                val ourSolution = solver.solve(grid = quiz)
                ourSolution.toString() shouldBe solution.toString()
            }
        }
    }
}
