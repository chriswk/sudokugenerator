package com.chriswk.sudoku

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

class SudokuCellTest : StringSpec() {
    init {
        "Row neighbours of a valid cell should give same result when divided by row size" {
            forAll(Gen.choose(0, 80)) { idx: Int ->
                SudokuCell(idx).rowNeighbours.all { it / 9 == idx / 9 }
            }
        }
        "Column neighbours of a valid cell should give same result for modulo division" {
            forAll(Gen.choose(0, 80)) { idx: Int ->
                SudokuCell(idx).columnNeighbours.all { it % 9 == idx % 9 }
            }
        }
        "Box neighbours should share same y or x and be no more than 2 away from valid cell" {
            forAll(Gen.choose(0, 80)) { idx: Int ->
                val cell = SudokuCell(idx)
                cell.boxCoords().all { neighbour ->
                    neighbour.x % 3 == cell.coord.x % 3 ||
                    neighbour.y / 3 == cell.coord.y / 3
                }
            }
        }
        "Any valid cell should have 20 neighbours" {
            forAll(Gen.choose(0, 80)) { idx: Int ->
                SudokuCell(idx).neighbours.size == 20
            }
        }

        "Any cell with idx inside 0 and 80 has valid index" {
            forAll(Gen.choose(0, 80)) { idx ->
                SudokuCell(idx).validIdx
            }
        }
        "Any cell with idx outside 0-80 has invalid index" {
            forAll(Gen.oneOf(Gen.negativeIntegers(), Gen.choose(81, 2000))) { idx ->
                !SudokuCell(idx).validIdx
            }
        }

        "Any cell with value inside 0-9 has valid value" {
            forAll(Gen.choose(0, 9)) { value ->
                SudokuCell(0, value = value).validValue
            }
        }
        "Any cell with value outside 0-9 has invalid value" {
            forAll(Gen.oneOf(Gen.negativeIntegers(), Gen.choose(10, 2000))) { value ->
                !SudokuCell(0, value = value).validValue
            }
        }
    }
}
