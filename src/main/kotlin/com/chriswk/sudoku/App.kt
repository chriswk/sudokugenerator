package com.chriswk.sudoku

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.prometheus.client.Counter
import java.util.concurrent.TimeUnit
import javax.sql.DataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.flywaydb.core.Flyway
import org.hashids.Hashids
import org.slf4j.LoggerFactory

val mainLogger = LoggerFactory.getLogger("com.chriswk.sudoku.AppKt")
val puzzlecount = Counter.build("number_of_puzzles_generated", "Puzzles generated since startup").register()
fun main(args: Array<String>) {
    val config = Application()
    val flyway = Flyway.configure().dataSource(config.database.url, config.database.username, config.database.password).load()
    flyway.migrate()
    val dataSource = getHikariConfig(config)
    val hashIds = Hashids(salt = config.salt)
    val sudokuStore = PostgresSudokuStore(dataSource = dataSource, hashId = hashIds)
    runBlocking {
        AppServer.startServer(port = config.httpPort, sudokuStore = sudokuStore).start(wait = false)
        GlobalScope.launch {
            val generator = SudokuGenerator()
            while (true) {
                val puzzle = generator.generateRandomDifficulty()
                println(sudokuStore.save(puzzle))
                puzzlecount.inc()
                delay(TimeUnit.SECONDS.toMillis(1))
            }
        }
    }
}

fun getHikariConfig(app: Application): DataSource {
    val config = HikariConfig()
    config.jdbcUrl = app.database.url
    config.username = app.database.username
    config.password = app.database.password
    config.minimumIdle = 1
    config.maximumPoolSize = 5
    return HikariDataSource(config)
}
