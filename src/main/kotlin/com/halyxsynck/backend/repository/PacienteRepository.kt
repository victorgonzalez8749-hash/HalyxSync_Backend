package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.AgregarMedicamentoRequest
import com.halyxsynck.backend.dto.MedicamentoDto
import com.halyxsynck.backend.dto.PacienteInfoResponse
import com.halyxsynck.backend.dto.RegistrarHistorialRequest
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Medicamentos
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PacienteRepository {

    fun obtenerInfoPaciente(correo: String): PacienteInfoResponse? {

        return transaction {

            val usuario = Users
                .selectAll()
                .where { Users.correo eq correo }
                .singleOrNull() ?: return@transaction null

            val pacienteId = usuario[Users.id]

            val historial = HistorialMedico
                .selectAll()
                .where { HistorialMedico.pacienteId eq pacienteId }
                .singleOrNull() ?: return@transaction null

            val medicamentos = Medicamentos
                .selectAll()
                .where { Medicamentos.pacienteId eq pacienteId }
                .map {
                    MedicamentoDto(
                        nombre = it[Medicamentos.nombre],
                        dosis = it[Medicamentos.dosis],
                        horario = it[Medicamentos.horario],
                        padecimiento = it[Medicamentos.padecimiento],
                        observaciones = it[Medicamentos.observaciones]
                    )
                }

            PacienteInfoResponse(
                nombreCompleto = "${usuario[Users.nombre]} ${usuario[Users.apellidoPaterno]} ${usuario[Users.apellidoMaterno]}",
                edad = historial[HistorialMedico.edad],
                sexo = historial[HistorialMedico.sexo],
                padecimientos = historial[HistorialMedico.padecimientos].split(",").map { it.trim() },
                medicoAsignado = historial[HistorialMedico.medicoAsignado],
                especialidadMedico = historial[HistorialMedico.especialidadMedico],
                medicamentos = medicamentos
            )

        }

    }

    fun registrarHistorial(request: RegistrarHistorialRequest): Boolean {

        return try {

            transaction {

                val usuario = Users
                    .selectAll()
                    .where { Users.correo eq request.correoPaciente }
                    .singleOrNull() ?: return@transaction false

                val doctor = Users
                    .selectAll()
                    .where { Users.correo eq request.correoDoctor }
                    .singleOrNull()

                val pacienteId = usuario[Users.id]

                // Ya NO borramos Medicamentos aquí — solo se actualizan los datos generales
                HistorialMedico.deleteWhere { HistorialMedico.pacienteId eq pacienteId }

                HistorialMedico.insert {
                    it[HistorialMedico.pacienteId] = pacienteId
                    it[doctorId] = doctor?.get(Users.id)
                    it[edad] = request.edad
                    it[sexo] = request.sexo
                    it[padecimientos] = request.padecimientos.joinToString(", ")
                    it[medicoAsignado] = request.medicoAsignado
                    it[especialidadMedico] = request.especialidadMedico
                }

                // Si vienen medicamentos en el registro inicial, se agregan (no se borra nada previo)
                request.medicamentos.forEach { med ->
                    Medicamentos.insert {
                        it[Medicamentos.pacienteId] = pacienteId
                        it[nombre] = med.nombre
                        it[dosis] = med.dosis
                        it[horario] = med.horario
                        it[padecimiento] = med.padecimiento
                        it[observaciones] = med.observaciones
                    }
                }

                true

            }

        } catch (e: Exception) {

            e.printStackTrace()

            false

        }

    }

    // NUEVO: agrega un medicamento sin borrar los existentes
    fun agregarMedicamento(request: AgregarMedicamentoRequest): Boolean {

        return try {

            transaction {

                val usuario = Users
                    .selectAll()
                    .where { Users.correo eq request.correoPaciente }
                    .singleOrNull() ?: return@transaction false

                Medicamentos.insert {
                    it[pacienteId] = usuario[Users.id]
                    it[nombre] = request.medicamento.nombre
                    it[dosis] = request.medicamento.dosis
                    it[horario] = request.medicamento.horario
                    it[padecimiento] = request.medicamento.padecimiento
                    it[observaciones] = request.medicamento.observaciones
                }

                true

            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

}