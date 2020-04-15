package com.chriswk.sudoku

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hashids.Hashids
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer

internal object PostgresContainer {
    val instance by lazy {
        PostgreSQLContainer<Nothing>("postgres:12.2").apply {
            start()
        }
    }
}

internal object DataSource {
    val instance: HikariDataSource by lazy {
        HikariDataSource().apply {
            username = PostgresContainer.instance.username
            password = PostgresContainer.instance.password
            jdbcUrl = PostgresContainer.instance.jdbcUrl
            connectionTimeout = 1000L
        }
    }
}

internal fun withCleanDb(test: () -> Unit) = DataSource.instance.also { clean(it) }.run { test() }

internal fun withMigratedDb(test: () -> Unit) =
    DataSource.instance.also { clean(it) }.also { migrate(it) }.run { test() }

internal fun clean(dataSource: HikariDataSource) = Flyway.configure().dataSource(dataSource).load().clean()
internal fun migrate(dataSource: HikariDataSource) = Flyway.configure().dataSource(dataSource).load().migrate()

@Disabled
class PostgresSudokuStoreTest {

    @Test
    fun `Can insert a game and get a hash back and fetch the same back`() = withMigratedDb {
        runBlocking {
            val generator = SudokuGenerator()
            val sudokuStore = PostgresSudokuStore(dataSource = DataSource.instance, hashId = Hashids(salt = "test"))
            val game = generator.generateRandomDifficulty()
            val frontendVersion = sudokuStore.save(game)
            val byHash = sudokuStore.get(frontendVersion.hash)
            assertThat(byHash).isNotNull
            assertThat(byHash!!.puzzle).isEqualTo(game.puzzle.toString())
        }
    }

    @Test
    fun `Gets the correct count after inserting 5 puzzles`() = withMigratedDb {
        runBlocking {
            val generator = SudokuGenerator()
            val sudokuStore = PostgresSudokuStore(dataSource = DataSource.instance, hashId = Hashids(salt = "test"))
            for (i in 1..5) {
                val game = generator.generateRandomDifficulty()
                sudokuStore.save(game)
            }
            assertThat(sudokuStore.countPuzzles().count).isEqualTo(5L)
        }
    }
}
