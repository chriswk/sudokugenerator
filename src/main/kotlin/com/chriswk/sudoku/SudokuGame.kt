package com.chriswk.sudoku

data class SudokuGame(val puzzle: SudokuGrid, val solution: SudokuGrid, val difficulty: Difficulty, val solutionGenerationTime: Double, val puzzleGenerationTime: Double)
