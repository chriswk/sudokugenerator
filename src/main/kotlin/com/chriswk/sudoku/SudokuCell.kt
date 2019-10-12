package com.chriswk.sudoku

data class SudokuCell(
    val idx: Int,
    var value: Int = 0,
    val gridSide: Int = 9
) {
    fun coordFromIdx(idx: Int): Coord = Coord(x = idx / gridSide, y = idx % gridSide)
    fun idxFromCoord(c: Coord): Int = c.x * gridSide + c.y
    val coord: Coord = coordFromIdx(idx)
    val validIdx: Boolean = idx in 0..(gridSide * gridSide)
    val validValue: Boolean = value in 0..gridSide
    val valid = validIdx && validValue
    val rowNeighbours: List<Int> = 0.until(gridSide).filter { it != coord.y }.map { Coord(coord.x, it) }.map { idxFromCoord(it) }
    val columnNeighbours: List<Int> = 0.until(gridSide).filter { it != coord.x }.map { Coord(it, coord.y) }.map { idxFromCoord(it) }
    fun boxCoords(): List<Coord> {
        val gridSideSquareRoot = Math.sqrt(gridSide.toDouble()).toInt()
        val boxMinX = (coord.x / gridSideSquareRoot) * gridSideSquareRoot
        val boxMaxX = ((coord.x / gridSideSquareRoot) + 1) * gridSideSquareRoot
        val boxMinY = (coord.y / gridSideSquareRoot) * gridSideSquareRoot
        val boxMaxY = ((coord.y / gridSideSquareRoot) + 1) * gridSideSquareRoot
        return (boxMinX.until(boxMaxX)).flatMap { x -> (boxMinY.until(boxMaxY)).map { y -> Coord(x, y) } }.minus(coord)
    }
    fun nextCellIdx(): Int? = when (idx >= (gridSide * gridSide - 1)) {
        true -> null
        false -> idx + 1
    }
    fun boxNeighbours(): List<Int> {
        return boxCoords().map { idxFromCoord(it) }
    }

    val neighbours = rowNeighbours union columnNeighbours union boxNeighbours()
}

data class Coord(val x: Int, val y: Int)
