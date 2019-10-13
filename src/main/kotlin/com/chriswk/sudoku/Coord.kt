package com.chriswk.sudoku

data class Coord(val x: Int, val y: Int) {
    fun toIndex(): Int = x * 9 + y
}
