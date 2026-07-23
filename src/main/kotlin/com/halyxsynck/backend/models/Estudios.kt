package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object Estudios : Table("estudios") {

    val id = integer("id").autoIncrement()

    val pacienteId = integer("paciente_id").references(Users.id)

    val url = varchar("url", 500)

    val descripcion = varchar("descripcion", 300).default("")

    val fecha = varchar("fecha", 20)

    override val primaryKey = PrimaryKey(id)

}