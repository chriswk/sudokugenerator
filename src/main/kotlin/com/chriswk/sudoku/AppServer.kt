package com.chriswk.sudoku

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.metrics.micrometer.MicrometerMetrics
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

    suspend fun startServer(port: Int): NettyApplicationEngine {
        DefaultExports.initialize()
        return embeddedServer(Netty, port = port) {
            install(DefaultHeaders)
            install(MicrometerMetrics) {
                registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT, CollectorRegistry.defaultRegistry, Clock.SYSTEM)
            }
            routing {
                get("/metrics") {
                    val names = call.request.queryParameters.getAll("name")?.toSet() ?: emptySet()
                    call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004), HttpStatusCode.OK) {
                        TextFormat.write004(this, CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(names))
                    }
                }
            }
        }
    }
}
