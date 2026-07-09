package com.halyxsynck.backend.database

import com.halyxsynck.backend.config.DatabaseConfig
import com.halyxsynck.backend.models.Users
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Medicamentos
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {

        val hikariConfig = HikariConfig().apply {

            jdbcUrl = "jdbc:mysql://${DatabaseConfig.HOST}:${DatabaseConfig.PORT}/${DatabaseConfig.DATABASE}"

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

        // Crea las 3 tablas si todavía no existen en MySQL
        transaction {
            SchemaUtils.create(Users, HistorialMedico, Medicamentos)
        }

        println("✅ Base de datos conectada correctamente.")

    }

}