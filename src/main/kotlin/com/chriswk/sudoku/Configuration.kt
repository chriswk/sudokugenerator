package com.chriswk.sudoku

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

private val localProperties = ConfigurationMap(
        mapOf(
                "username" to "sudoku",
                "password" to "sudoku",
                "databasename" to "sudokupuzzles",
                "port" to "8800"
        )
)

private val devProperties = ConfigurationMap(
        mapOf("databasename" to "sudokupuzzles", "port" to "8800")
)

private val prodProperties = ConfigurationMap(
        mapOf("databasename" to "sudokupuzzles", "port" to "8800")
)

data class Application(
    val httpPort: Int = config()[Key("port", intType)],
    val username: String = config()[Key("username", stringType)],
    val password: String = config()[Key("password", stringType)],
    val databaseName: String = config()[Key("databasename", stringType)]
)

fun getEnvOrProp(propName: String): String? {
    return System.getenv(propName) ?: System.getProperty(propName)
}

private fun config() = when (getEnvOrProp("ENV")) {
    "dev" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties
}
