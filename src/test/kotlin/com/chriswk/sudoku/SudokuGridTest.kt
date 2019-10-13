package com.chriswk.sudoku

import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
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
            assertAll(Gen.choose(0, 80)) { size ->
                assertFailsWith<IllegalArgumentException>("Grid must be of size 81. it was $size") {
                    SudokuGrid.of(IntArray(size))
                }
            }
        }
        "A too large intArray should cause verify to fail" {
            assertAll(Gen.choose(82, 5000)) { size ->
                assertFailsWith<IllegalArgumentException>("Grid must be of size 81. it was $size") {
                    SudokuGrid.of(IntArray(size))
                }
            }
        }
        "Too small values should cause verify to fail" {
            assertAll(Gen.negativeIntegers()) { value ->
                assertFailsWith<IllegalArgumentException>("Only valid values is 0 (to signify empty) to 9") {
                    SudokuGrid.of(IntArray(81) { idx -> value })
                }
            }
        }
        "Too large values should cause verification of grid to fail" {
            assertAll(Gen.choose(10, 100)) { value ->
                assertFailsWith<IllegalArgumentException>("Only valid values is 0 (to signify empty) to 9") {
                    SudokuGrid.of(IntArray(81) { idx -> value })
                }
            }
        }
        "Roundtrips should be possible" {
            val grid = SudokuGrid.of(IntArray(81) { idx ->
                if (idx < 9) {
                    idx
                } else if (idx in 9..17) {
                    2
                } else if (idx in 18..26) {
                    1
                } else if (idx in 27..36) {
                    5
                } else {
                    0
                }
            })
            SudokuGrid.fromString(grid.toString()).toIntArray().contentEquals(grid.toIntArray())
        }
    }
}
