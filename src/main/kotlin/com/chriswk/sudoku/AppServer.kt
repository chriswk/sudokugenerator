package com.chriswk.sudoku

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.micrometer.core.instrument.Clock
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.DefaultExports

object AppServer {

    suspend fun startServer(port: Int, sudokuStore: SudokuStore): NettyApplicationEngine {
        DefaultExports.initialize()
        return embeddedServer(Netty, port = port) {
            install(DefaultHeaders)
            install(MicrometerMetrics) {
                registry =
                    PrometheusMeterRegistry(PrometheusConfig.DEFAULT, CollectorRegistry.defaultRegistry, Clock.SYSTEM)
            }
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                }
            }
            install(CallLogging)
            install(StatusPages) {
                exception<IllegalArgumentException> { _ ->
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            routing {
                get("/metrics") {
                    val names = call.request.queryParameters.getAll("name")?.toSet() ?: emptySet()
                    call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004), HttpStatusCode.OK) {
                        TextFormat.write004(this, CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(names))
                    }
                }
                get("/isReady") {
                    when (sudokuStore.isHealthy()) {
                        true -> call.respondText("UP")
                        false -> call.respond(HttpStatusCode.InternalServerError, "Database connection down")
                    }
                }
                get("/isAlive") {
                    call.respondText("ALIVE")
                }
                get("/puzzle/{hash}") {
                    val hash = call.parameters["hash"]
                    when (hash) {
                        null -> call.respond(404)
                        else -> {
                            val puzzle = sudokuStore.get(hash)
                            when (puzzle) {
                                null -> call.respond(404)
                                else -> call.respond(puzzle)
                            }
                        }
                    }
                }
                get("/puzzlecount") {
                    call.respond(sudokuStore.countPuzzles())
                }
            }
        }
    }
}

data class PuzzleCount(val count: Long)
