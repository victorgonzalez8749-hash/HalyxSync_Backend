package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.PacienteResumenDto
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Users
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

}