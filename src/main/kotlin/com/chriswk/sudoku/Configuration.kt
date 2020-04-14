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
                "database.url" to "jdbc:postgresql://sudoku:sudoku@localhost:5432/sudokupuzzles",
                "desiredPuzzles" to "1000000",
                "salt" to "sudokuIsFun",
                "http.port" to "8800"
        )
)

private val devProperties = ConfigurationMap(
        mapOf("databasename" to "sudokupuzzles", "http.port" to "8800")
)

private val prodProperties = ConfigurationMap(
        mapOf("databasename" to "sudokupuzzles", "http.port" to "8800")
)
data class Database(
    val url: String = config()[Key("database.url", stringType)]
) {
    fun password(): String {
        return url.substringBefore("@").substringAfterLast(":")
    }
    fun username(): String {
        return url.substringBefore("@").substringBeforeLast(":").substringAfterLast("/")
    }
    fun cleanUrl(): String {
        if (url.startsWith("jdbc:")) {
            return url.substringBeforeLast("@").substringBeforeLast("/") + "/" + url.substringAfterLast("@")
        } else {
            return "jdbc:" + url.substringBeforeLast("@").substringBeforeLast("/") + url.substringAfterLast("@")
        }
    }
}

data class Application(
    val httpPort: Int = config()[Key("http.port", intType)],
    val database: Database = Database(),
    val desiredPuzzles: Int = config()[Key("desiredPuzzles", intType)],
    val salt: String = config()[Key("salt", stringType)]
)

fun getEnvOrProp(propName: String): String? {
    return System.getenv(propName) ?: System.getProperty(propName)
}

private fun config() = when (getEnvOrProp("ENV")) {
    "dev" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties
}
