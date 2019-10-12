package com.chriswk.sudoku

data class SudokuCell(
    val idx: Int,
    val value: Int = 0
) {
    fun coordFromIdx(idx: Int): Coord = Coord(x = idx / 9, y = idx % 9)
    fun idxFromCoord(c: Coord): Int = c.x * 9 + c.y
    val coord: Coord = coordFromIdx(idx)
    val validIdx: Boolean = idx in 0..80
    val validValue: Boolean = idx in 0..9
    val valid = validIdx && validValue
    val rowNeighbours: List<Int> = 0.until(9).filter { it != coord.y }.map { Coord(coord.x, it) }.map { idxFromCoord(it) }
    val columnNeighbours: List<Int> = 0.until(9).filter { it != coord.x }.map { Coord(it, coord.y) }.map { idxFromCoord(it) }
    fun boxNeighbours(): List<Int> {
        val boxMinX = (coord.x / 3) * 3
        val boxMaxX = ((coord.x / 3) + 1) * 3
        val boxMinY = (coord.y / 3) * 3
        val boxMaxY = ((coord.y / 3) + 1) * 3
        return (boxMinX.until(boxMaxX)).flatMap { x -> (boxMinY.until(boxMaxY)).map { y -> Coord(x, y) } }.minus(coord).map { idxFromCoord(it) }
    }

    val neighbours = rowNeighbours union columnNeighbours union boxNeighbours()
}

data class Coord(val x: Int, val y: Int)
