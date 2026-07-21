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

            (HistorialMedico innerJoin Users)
                .selectAll()
                .where { HistorialMedico.doctorId eq doctorId }
                .map { fila ->
                    PacienteResumenDto(
                        correo = fila[Users.correo],
                        nombreCompleto = "${fila[Users.nombre]} ${fila[Users.apellidoPaterno]}",
                        edad = fila[HistorialMedico.edad],
                        sexo = fila[HistorialMedico.sexo]
                    )
                }

        }

    }

}