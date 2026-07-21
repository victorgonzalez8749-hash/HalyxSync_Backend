package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object HistorialMedico : Table("historial_medico") {

    val id = integer("id").autoIncrement()

    val pacienteId = integer("paciente_id").references(Users.id)

    val doctorId = integer("doctor_id").references(Users.id).nullable()

    val edad = integer("edad")

    val sexo = varchar("sexo", 20).default("No especificado")

    val padecimientos = text("padecimientos")

    val medicoAsignado = varchar("medico_asignado", 150)

    val especialidadMedico = varchar("especialidad_medico", 100)

    override val primaryKey = PrimaryKey(id)

}

object Medicamentos : Table("medicamentos") {

    val id = integer("id").autoIncrement()

    val pacienteId = integer("paciente_id").references(Users.id)

    val nombre = varchar("nombre", 100)

    val dosis = varchar("dosis", 100)

    val horario = varchar("horario", 100)

    override val primaryKey = PrimaryKey(id)

}