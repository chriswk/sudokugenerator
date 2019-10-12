package com.chriswk.sudoku

import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.test.assertFailsWith

class SudokuGridTest : StringSpec() {
    init {
        "An empty grid to string should contain no numbers" {
            SudokuGrid.emptyGrid().toString() shouldNotContain ("\\d")
        }
        "For an empty grid the first empty cell should be at (0,0)" {
            SudokuGrid.emptyGrid().firstEmptyCell()!!.coord shouldBe Coord(0, 0)
        }
        "For a board filled with four numbers and the rest, first empty cell should be at (0, 4)" {
            SudokuGrid.of(IntArray(81) { idx ->
                if (idx < 4) {
                    1
                } else {
                    0
                }
            }).firstEmptyCell()!!.coord shouldBe (Coord(0, 4))
        }
        "A too small intArray should cause require to fail" {
            assertFailsWith<IllegalArgumentException>("Grid must be of size 81. it was 5") {
                SudokuGrid.of(IntArray(5))
            }
        }
    }
}
