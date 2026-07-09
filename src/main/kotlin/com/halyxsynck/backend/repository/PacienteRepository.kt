package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.MedicamentoDto
import com.halyxsynck.backend.dto.PacienteInfoResponse
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Medicamentos
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PacienteRepository {

    fun obtenerInfoPaciente(correo: String): PacienteInfoResponse? {

        return transaction {

            // Buscamos al usuario por correo para obtener su id y nombre
            val usuario = Users
                .selectAll()
                .where { Users.correo eq correo }
                .singleOrNull() ?: return@transaction null

            val pacienteId = usuario[Users.id]

            // Buscamos su historial médico
            val historial = HistorialMedico
                .selectAll()
                .where { HistorialMedico.pacienteId eq pacienteId }
                .singleOrNull() ?: return@transaction null

            // Buscamos todos sus medicamentos recetados
            val medicamentos = Medicamentos
                .selectAll()
                .where { Medicamentos.pacienteId eq pacienteId }
                .map {
                    MedicamentoDto(
                        nombre = it[Medicamentos.nombre],
                        dosis = it[Medicamentos.dosis]
                    )
                }

            PacienteInfoResponse(
                nombreCompleto = "${usuario[Users.nombre]} ${usuario[Users.apellidoPaterno]} ${usuario[Users.apellidoMaterno]}",
                edad = historial[HistorialMedico.edad],
                padecimientos = historial[HistorialMedico.padecimientos].split(",").map { it.trim() },
                medicoAsignado = historial[HistorialMedico.medicoAsignado],
                especialidadMedico = historial[HistorialMedico.especialidadMedico],
                medicamentos = medicamentos
            )

        }

    }

}