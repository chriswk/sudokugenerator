package com.chriswk.sudoku

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SudokuCellTest {
    @Test
    fun `a cells row neighbours should all be within 8 of column`() {
        val cell = SudokuCell(4)
        assertThat(cell.rowNeighbours).isEqualTo(listOf(0, 1, 2, 3, 5, 6, 7, 8))
    }

    @Test
    fun `a cells column neighbours should all have the same result of % 9`() {
        val cell = SudokuCell(4)
        assertThat(cell.columnNeighbours).isEqualTo(listOf(13, 22, 31, 40, 49, 58, 67, 76))
    }

    @Test
    fun `a cells box neighbours should be within % 3 of x,y of cell`() {
        val cell = SudokuCell(4)
        assertThat(cell.boxNeighbours()).isEqualTo(listOf(3, 5, 12, 13, 14, 21, 22, 23))
        val cell2 = SudokuCell(23)
        assertThat(cell2.boxNeighbours()).isEqualTo(listOf(3, 4, 5, 12, 13, 14, 21, 22))
    }
    @Test
    fun `any cell should have 20 neighbours`() {
        val cell = SudokuCell(4)
        assertThat(cell.neighbours).hasSize(20)
    }
}
