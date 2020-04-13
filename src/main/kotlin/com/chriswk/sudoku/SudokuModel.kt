package com.chriswk.sudoku

data class SudokuGame(val puzzle: SudokuGrid, val solution: SudokuGrid, val difficulty: Difficulty, val solutionGenerationTime: Double, val puzzleGenerationTime: Double)

data class SudokuDto(val puzzle: String, val difficulty: Difficulty, val hash: String)

data class Coord(val x: Int, val y: Int) {
    fun toIndex(): Int = x * 9 + y
}

data class SudokuCell(
    val idx: Int,
    var value: Int = 0,
    val gridSide: Int = 9
) {
    fun coordFromIdx(idx: Int): Coord = Coord(x = idx / gridSide, y = idx % gridSide)
    fun idxFromCoord(c: Coord): Int = c.x * gridSide + c.y
    val coord: Coord = coordFromIdx(idx)
    val validIdx: Boolean = idx in 0 until gridSide * gridSide
    val validValue: Boolean = value in 0..gridSide
    val valid = validIdx && validValue
    fun isEmpty() = value == 0
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

    fun boxNeighbours(): List<Int> {
        return boxCoords().map { idxFromCoord(it) }
    }

    val neighbours = rowNeighbours union columnNeighbours union boxNeighbours()
}
enum class Difficulty {
    VERY_EASY, EASY, MEDIUM, HARD, VERY_HARD, DIABOLICAL
}
