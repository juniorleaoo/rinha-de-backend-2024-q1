package io.rinha

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

fun connectToDatabase(): HikariDataSource {
    val config = HikariConfig()
    config.jdbcUrl = System.getenv("DATASOURCE_URL")
    config.username = System.getenv("DATASOURCE_USERNAME")
    config.password = System.getenv("DATASOURCE_PASSWORD")
    config.maximumPoolSize = System.getenv("MAX_CONNECTION_POOL_SIZE")?.toInt() ?: 5
    config.transactionIsolation = "TRANSACTION_READ_COMMITTED"

    return HikariDataSource(config)
}