package com.chriswk.sudoku

import io.kotlintest.shouldBe
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
            val timeTaken = measureTimeMillis {
                SudokuSolver().solve(grid = grid) shouldBe true
            }
            logger.info("Took $timeTaken ms")
            grid.firstEmptyCell() shouldBe null
            logger.info(grid.toString())
        }

    }
}
