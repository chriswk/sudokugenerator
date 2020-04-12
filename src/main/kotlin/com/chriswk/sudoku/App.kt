package com.chriswk.sudoku

import kotlin.system.measureTimeMillis
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

val mainLogger = LoggerFactory.getLogger("com.chriswk.sudoku.AppKt")
fun main(args: Array<String>) = runBlocking {
    val generator = SudokuGenerator()
    val puzzlesWithSolutions = mutableMapOf<String, String>()
    val numberToGenerate = if (args.size == 1) {
        args[0].toInt()
    } else {
        10
    }
    val timeToCreate = measureTimeMillis {
        0.until(numberToGenerate).forEach { _ ->
            val game = generator.generate(Difficulty.VERY_HARD)
            puzzlesWithSolutions += (game.puzzle.toString() to game.solution.toString())
        }
    }
    mainLogger.info("Used $timeToCreate ms to create $numberToGenerate puzzles")
    println(puzzlesWithSolutions)
}
