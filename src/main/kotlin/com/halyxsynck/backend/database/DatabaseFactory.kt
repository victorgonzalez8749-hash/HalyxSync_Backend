package com.halyxsynck.backend.database

import com.halyxsynck.backend.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.halyxsynck.backend.models.Users

object DatabaseFactory {

    fun init() {

        val hikariConfig = HikariConfig().apply {

            jdbcUrl =
                "jdbc:mysql://${DatabaseConfig.HOST}:${DatabaseConfig.PORT}/${DatabaseConfig.DATABASE}"

            driverClassName = "com.mysql.cj.jdbc.Driver"

            username = DatabaseConfig.USER

            password = DatabaseConfig.PASSWORD

            maximumPoolSize = 10

            isAutoCommit = false

            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()

        }

        val dataSource = HikariDataSource(hikariConfig)

        Database.connect(dataSource)

        transaction {

            SchemaUtils.create(Users)

        }

        println(" Base de datos conectada correctamente.")

    }

}