package com.chriswk.sudoku

import de.huxhorn.sulky.ulid.ULID
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import org.hashids.Hashids
import javax.sql.DataSource
import kotlin.math.roundToLong
import kotlin.random.Random

interface HealthCheck {
    fun isHealthy(): Boolean
}

interface SudokuStore : HealthCheck {
    fun save(puzzle: SudokuGame): SudokuDto
    fun get(hash: String): SudokuDto?
    fun randomPuzzle(): SudokuDto?
    fun countPuzzles(): PuzzleCount
}

class PostgresSudokuStore(val dataSource: DataSource, val ulid: ULID = ULID(), val hashId: Hashids) : SudokuStore {
    override fun save(puzzle: SudokuGame): SudokuDto {
        return using(sessionOf(dataSource)) { session ->
            val unique = ulid.nextULID()
            val modifiedRows = session.run(
                queryOf(
                    """
                       INSERT INTO puzzle(ulid, puzzle, puzzle_gen_ms, solution, solution_gen_ms, difficulty) VALUES (:ulid, :puzzle, :puzzleGenMs, :solution, :solutionGenMs, :difficulty) 
                    """.trimIndent(), mapOf(
                        "ulid" to unique,
                        "puzzle" to puzzle.puzzle.toString(),
                        "solution" to puzzle.solution.toString(),
                        "difficulty" to puzzle.difficulty.name,
                        "puzzleGenMs" to (puzzle.puzzleGenerationTime * 1000).roundToLong(),
                        "solutionGenMs" to (puzzle.solutionGenerationTime * 1000).roundToLong()
                    )
                ).asUpdate
            )
            if (modifiedRows == 1) {
                session.run(
                    queryOf("""SELECT id, puzzle, difficulty FROM puzzle WHERE ulid = :ulid""", mapOf("ulid" to unique))
                        .map {
                            SudokuDto(
                                puzzle = it.string("puzzle"),
                                difficulty = Difficulty.valueOf(it.string("difficulty")),
                                hash = hashId.encode(it.long("id"))
                            )
                        }.asSingle
                ) ?: throw IllegalStateException("Could not found just inserted row")
            } else {
                throw IllegalStateException("Could not insert row")
            }
        }
    }

    override fun get(hash: String): SudokuDto? {
        val id = hashId.decode(hash)
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    "SELECT * FROM puzzle WHERE id = :id", mapOf("id" to id.first())
                ).map {
                    dtoFromRow(it)
                }.asSingle
            )
        }
    }

    private fun dtoFromRow(it: Row): SudokuDto {
        return SudokuDto(
            puzzle = it.string("puzzle"),
            hash = hashId.encode(it.long("id")),
            difficulty = Difficulty.valueOf(it.string("difficulty"))
        )
    }

    override fun countPuzzles(): PuzzleCount {
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    "SELECT count(*) FROM puzzle as count",
                    emptyMap()
                ).map { PuzzleCount(it.long("count")) }.asSingle
            )
        } ?: PuzzleCount(0L)
    }

    override fun randomPuzzle(): SudokuDto? {
        return using(sessionOf(dataSource)) { session ->
            val maxId = session.run(
                queryOf("SELECT max(id) FROM puzzle as max", emptyMap()).map { it.long("max") }.asSingle
            ) ?: 0
            val randomId = Random.nextLong(0, maxId)
            session.run(
                queryOf("SELECT * FROM puzzle where id >= :rnd", mapOf("rnd" to randomId)).map {
                    dtoFromRow(it)
                }.asSingle
            )
        }
    }

    override fun isHealthy(): Boolean {
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf("SELECT 1 AS up").map { it.boolean("up") }.asSingle
            ) ?: false
        }
    }
}
