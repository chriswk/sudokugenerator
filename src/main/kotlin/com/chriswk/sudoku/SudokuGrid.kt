package com.chriswk.sudoku

import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import org.slf4j.LoggerFactory

class SudokuGrid(val grid: Array<SudokuCell>) {
    companion object {
        val logger = LoggerFactory.getLogger(SudokuGrid::class.java)
        fun of(grid: IntArray): SudokuGrid {
            verifyGrid(grid)
            return SudokuGrid(grid.mapIndexed { idx, value -> SudokuCell(idx, value) }.toTypedArray())
        }

        fun emptyGrid(): SudokuGrid {
            val empty = IntArray(81) { 0 }
            return of(empty)
        }

        @Throws(IllegalArgumentException::class)
        fun verifyGrid(grid: IntArray) {
            require(grid.size == 81) { "Grid must be of size 81. it was ${grid.size}" }
            require(grid.all { it in 0..9 }) { "Only valid values is 0 (to signify empty) to 9" }
        }
    }

    fun logState() {
        logger.debug(this.toString())
    }

    fun getCell(idx: Int): SudokuCell = grid[idx]
    fun isValidValueForCell(cell: SudokuCell, value: Int): Boolean {
        return cell.neighbours
                .map { nIdx -> getCell(nIdx) }
                .none { neighbour -> neighbour.value == value }
    }

    fun firstEmptyCell(): SudokuCell? {
        return grid.firstOrNull { it.value == 0 }
    }

    fun nextEmptyCell(cell: SudokuCell): SudokuCell? {
        return cell.idx.until(grid.size).map { getCell(it) }.firstOrNull { it.value == 0 }
    }

    override fun toString(): String {
        return GridConverter.toString(this)
    }
}

object GridConverter {
    fun toString(grid: SudokuGrid): String {
        val builder = StringBuilder()
        topBorder(builder)
        grid.grid.toList().chunked(9).forEachIndexed { idx, row ->
            rowBorder(builder)
            row.forEachIndexed { colIdx, cell ->
                value(builder, cell)
                rightColumnBorder(builder, colIdx + 1)
            }
            rowBorder(builder)
            builder.append("\n")
            bottowRowBorder(builder, idx + 1)
        }
        bottomBorder(builder)
        return builder.toString()
    }

    private fun bottomBorder(builder: StringBuilder) {
        builder.append("╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝\n")
    }

    private fun topBorder(builder: StringBuilder) {
        builder.append("╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗\n")
    }

    private fun rowBorder(builder: StringBuilder) {
        builder.append("║")
    }

    private fun value(builder: StringBuilder, cell: SudokuCell) {
        val output = when (cell.value) {
            0 -> " "
            else -> cell.value.toString()
        }
        builder.append(" $output ")
    }

    private fun rightColumnBorder(builder: StringBuilder, column: Int) {
        if (column == 9) {
            return
        }

        if (column % 3 == 0) {
            builder.append("║")
        } else {
            builder.append("|")
        }
    }

    private fun bottowRowBorder(builder: StringBuilder, row: Int) {
        if (row == 9) {
            return
        }
        if (row % 3 == 0) {
            builder.append("╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣\n")
        } else {
            builder.append("╟───┼───┼───╫───┼───┼───╫───┼───┼───╢\n")
        }
    }
}
