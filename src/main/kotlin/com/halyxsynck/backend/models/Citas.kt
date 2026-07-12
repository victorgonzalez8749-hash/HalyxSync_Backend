package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object Citas : Table("citas") {

    val id = integer("id").autoIncrement()

    val pacienteId = integer("paciente_id").references(Users.id)

    val medico = varchar("medico", 150)

    val especialidad = varchar("especialidad", 100)

    val fecha = varchar("fecha", 20) // ej: "2026-07-15"

    val hora = varchar("hora", 20) // ej: "10:30 AM"

    val motivo = varchar("motivo", 200)

    override val primaryKey = PrimaryKey(id)

}