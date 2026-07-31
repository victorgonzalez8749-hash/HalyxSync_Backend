package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.AgregarMedicamentoRequest
import com.halyxsynck.backend.dto.MedicamentoDto
import com.halyxsynck.backend.dto.MedicoAsignadoDto
import com.halyxsynck.backend.dto.PacienteInfoResponse
import com.halyxsynck.backend.dto.RegistrarHistorialRequest
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Medicamentos
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
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

            val historiales = HistorialMedico
                .selectAll()
                .where { HistorialMedico.pacienteId eq pacienteId }
                .toList()

            if (historiales.isEmpty()) return@transaction null

            val medicos = historiales.map { fila ->
                MedicoAsignadoDto(
                    nombre = fila[HistorialMedico.medicoAsignado],
                    especialidad = fila[HistorialMedico.especialidadMedico],
                    padecimientos = fila[HistorialMedico.padecimientos].split(",").map { it.trim() }
                )
            }

            val primero = historiales.first()

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
                edad = primero[HistorialMedico.edad],
                sexo = primero[HistorialMedico.sexo],
                medicos = medicos,
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
                val doctorIdActual = doctor?.get(Users.id)

                // Solo borramos el historial que corresponde a ESTE doctor,
                // sin tocar los registros de otros doctores asignados al mismo paciente
                if (doctorIdActual != null) {
                    HistorialMedico.deleteWhere {
                        (HistorialMedico.pacienteId eq pacienteId) and (HistorialMedico.doctorId eq doctorIdActual)
                    }
                }

                HistorialMedico.insert {
                    it[HistorialMedico.pacienteId] = pacienteId
                    it[doctorId] = doctorIdActual
                    it[edad] = request.edad
                    it[sexo] = request.sexo
                    it[padecimientos] = request.padecimientos.joinToString(", ")
                    it[medicoAsignado] = request.medicoAsignado
                    it[especialidadMedico] = request.especialidadMedico
                }

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