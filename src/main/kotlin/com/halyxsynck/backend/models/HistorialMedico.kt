package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

// Guarda la info médica general de cada paciente
object HistorialMedico : Table("historial_medico") {

    val id = integer("id").autoIncrement()

    // Relación con el paciente (referencia a la tabla Users)
    val pacienteId = integer("paciente_id").references(Users.id)

    val edad = integer("edad")

    val padecimientos = text("padecimientos") // separados por coma, ej: "Diabetes, Hipertensión"

    val medicoAsignado = varchar("medico_asignado", 150)

    val especialidadMedico = varchar("especialidad_medico", 100)

    override val primaryKey = PrimaryKey(id)

}

// Guarda cada medicamento recetado (puede haber varios por paciente)
object Medicamentos : Table("medicamentos") {

    val id = integer("id").autoIncrement()

    val pacienteId = integer("paciente_id").references(Users.id)

    val nombre = varchar("nombre", 100)

    val dosis = varchar("dosis", 100) // ej: "500mg cada 8 horas"

    val horario = varchar("horario", 100) // NUEVO: ej. "8:00 AM, 2:00 PM, 8:00 PM"

    override val primaryKey = PrimaryKey(id)

}