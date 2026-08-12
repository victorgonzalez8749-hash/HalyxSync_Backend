package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.CitaAgendaDto
import com.halyxsynck.backend.dto.MedicamentoDto
import com.halyxsynck.backend.dto.PacienteResumenDto
import com.halyxsynck.backend.dto.RecetaDto
import com.halyxsynck.backend.models.Citas
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Medicamentos
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class DoctorRepository {

    fun obtenerPacientes(correoDoctor: String): List<PacienteResumenDto> {

        return transaction {

            val doctor = Users
                .selectAll()
                .where { Users.correo eq correoDoctor }
                .singleOrNull() ?: return@transaction emptyList()

            val doctorId = doctor[Users.id]

            val historiales = HistorialMedico
                .selectAll()
                .where { HistorialMedico.doctorId eq doctorId }

            historiales.mapNotNull { fila ->

                val paciente = Users
                    .selectAll()
                    .where { Users.id eq fila[HistorialMedico.pacienteId] }
                    .singleOrNull() ?: return@mapNotNull null

                PacienteResumenDto(
                    correo = paciente[Users.correo],
                    nombreCompleto = "${paciente[Users.nombre]} ${paciente[Users.apellidoPaterno]}",
                    edad = fila[HistorialMedico.edad],
                    sexo = fila[HistorialMedico.sexo]
                )

            }

        }

    }

    fun obtenerCitasHoyDetalle(correoDoctor: String, fechaHoy: String): List<CitaAgendaDto> {

        return transaction {

            val doctor = Users
                .selectAll()
                .where { Users.correo eq correoDoctor }
                .singleOrNull() ?: return@transaction emptyList()

            val doctorId = doctor[Users.id]

            val citas = Citas
                .selectAll()
                .where { (Citas.doctorId eq doctorId) and (Citas.fecha eq fechaHoy) }

            citas.mapNotNull { fila ->

                val paciente = Users
                    .selectAll()
                    .where { Users.id eq fila[Citas.pacienteId] }
                    .singleOrNull() ?: return@mapNotNull null

                val historial = HistorialMedico
                    .selectAll()
                    .where { (HistorialMedico.pacienteId eq paciente[Users.id]) and (HistorialMedico.doctorId eq doctorId) }
                    .singleOrNull()

                CitaAgendaDto(
                    pacienteNombre = "${paciente[Users.nombre]} ${paciente[Users.apellidoPaterno]}",
                    edad = historial?.get(HistorialMedico.edad) ?: 0,
                    hora = fila[Citas.hora],
                    motivo = fila[Citas.motivo],
                    estado = fila[Citas.estado]
                )

            }.sortedBy { it.hora }

        }

    }

    // NUEVO: recetas agrupadas por paciente, con sus medicamentos recetados por este doctor
    fun obtenerRecetas(correoDoctor: String): List<RecetaDto> {

        return transaction {

            val doctor = Users
                .selectAll()
                .where { Users.correo eq correoDoctor }
                .singleOrNull() ?: return@transaction emptyList()

            val doctorId = doctor[Users.id]

            val historiales = HistorialMedico
                .selectAll()
                .where { HistorialMedico.doctorId eq doctorId }
                .toList()

            historiales.mapNotNull { fila ->

                val pacienteId = fila[HistorialMedico.pacienteId]

                val paciente = Users
                    .selectAll()
                    .where { Users.id eq pacienteId }
                    .singleOrNull() ?: return@mapNotNull null

                val medicamentos = Medicamentos
                    .selectAll()
                    .where { (Medicamentos.pacienteId eq pacienteId) and (Medicamentos.doctorId eq doctorId) }
                    .map {
                        MedicamentoDto(
                            nombre = it[Medicamentos.nombre],
                            dosis = it[Medicamentos.dosis],
                            horario = it[Medicamentos.horario],
                            padecimiento = it[Medicamentos.padecimiento],
                            observaciones = it[Medicamentos.observaciones]
                        )
                    }

                if (medicamentos.isEmpty()) return@mapNotNull null

                RecetaDto(
                    pacienteCorreo = paciente[Users.correo],
                    pacienteNombre = "${paciente[Users.nombre]} ${paciente[Users.apellidoPaterno]}",
                    edad = fila[HistorialMedico.edad],
                    medicamentos = medicamentos
                )

            }

        }

    }

}