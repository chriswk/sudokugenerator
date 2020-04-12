package com.chriswk.sudoku

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

val mainLogger = LoggerFactory.getLogger("com.chriswk.sudoku.AppKt")
fun main(args: Array<String>) {
    val config = Application()
    runBlocking {
        AppServer.startServer(port = config.httpPort).start(wait = false)
        GlobalScope.launch {
            val generator = SudokuGenerator()
            while (true) {
                val puzzle = generator.generateRandomDifficulty()
                println(puzzle)
                delay(TimeUnit.SECONDS.toMillis(1))
            }
        }
    }
}
