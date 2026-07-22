package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object Citas : Table("citas") {

    val id = integer("id").autoIncrement()

    val pacienteId = integer("paciente_id").references(Users.id)

    val doctorId = integer("doctor_id").references(Users.id).nullable()

    val medico = varchar("medico", 150)

    val especialidad = varchar("especialidad", 100)

    val fecha = varchar("fecha", 20) // "2026-07-25"

    val hora = varchar("hora", 20) // "10:30 AM"

    val motivo = varchar("motivo", 200)

    val estado = varchar("estado", 20).default("Pendiente") // Pendiente, Confirmada, Cancelada

    override val primaryKey = PrimaryKey(id)

}