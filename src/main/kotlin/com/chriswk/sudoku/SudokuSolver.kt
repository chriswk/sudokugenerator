package com.chriswk.sudoku

import kotlin.random.Random

class SudokuSolver(val random: Random = Random.Default, val values: IntArray = generateRandomValues(random)) {
    fun solve(grid: SudokuGrid): SudokuGrid {
        val toSolve = SudokuGrid.of(grid.toIntArray())
        solve_rec(toSolve)
        return toSolve
    }

    fun solve_rec(grid: SudokuGrid, cell: SudokuCell? = grid.firstEmptyCell()): Boolean {
        if (cell == null) {
            return true
        }
        values.forEach { v ->
            if (grid.isValidValueForCell(cell, value = v)) {
                cell.value = v
                if (solve_rec(grid, grid.nextEmptyCell(cell))) {
                    return true
                }
                cell.value = EMPTY
            }
        }
        return false
    }
}

val EMPTY: Int = 0

fun generateRandomValues(random: Random): IntArray {
    val list = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    list.shuffle(random)
    return list.toIntArray()
}
