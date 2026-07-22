package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.AgendarCitaRequest
import com.halyxsynck.backend.dto.CitaDto
import com.halyxsynck.backend.models.Citas
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CitaRepository {

    fun agendarCita(request: AgendarCitaRequest): Boolean {

        return try {

            transaction {

                val paciente = Users
                    .selectAll()
                    .where { Users.correo eq request.correoPaciente }
                    .singleOrNull() ?: return@transaction false

                val doctor = Users
                    .selectAll()
                    .where { Users.correo eq request.correoDoctor }
                    .singleOrNull()

                Citas.insert {
                    it[pacienteId] = paciente[Users.id]
                    it[doctorId] = doctor?.get(Users.id)
                    it[medico] = request.medico
                    it[especialidad] = request.especialidad
                    it[fecha] = request.fecha
                    it[hora] = request.hora
                    it[motivo] = request.motivo
                }

                true

            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun obtenerCitasPaciente(correo: String): List<CitaDto> {

        return transaction {

            val usuario = Users
                .selectAll()
                .where { Users.correo eq correo }
                .singleOrNull() ?: return@transaction emptyList()

            Citas
                .selectAll()
                .where { Citas.pacienteId eq usuario[Users.id] }
                .map {
                    CitaDto(
                        id = it[Citas.id],
                        pacienteNombre = "${usuario[Users.nombre]} ${usuario[Users.apellidoPaterno]}",
                        medico = it[Citas.medico],
                        especialidad = it[Citas.especialidad],
                        fecha = it[Citas.fecha],
                        hora = it[Citas.hora],
                        motivo = it[Citas.motivo],
                        estado = it[Citas.estado]
                    )
                }

        }

    }

    fun obtenerCitasDoctor(correoDoctor: String): List<CitaDto> {

        return transaction {

            val doctor = Users
                .selectAll()
                .where { Users.correo eq correoDoctor }
                .singleOrNull() ?: return@transaction emptyList()

            val citas = Citas
                .selectAll()
                .where { Citas.doctorId eq doctor[Users.id] }

            citas.mapNotNull { fila ->

                val paciente = Users
                    .selectAll()
                    .where { Users.id eq fila[Citas.pacienteId] }
                    .singleOrNull() ?: return@mapNotNull null

                CitaDto(
                    id = fila[Citas.id],
                    pacienteNombre = "${paciente[Users.nombre]} ${paciente[Users.apellidoPaterno]}",
                    medico = fila[Citas.medico],
                    especialidad = fila[Citas.especialidad],
                    fecha = fila[Citas.fecha],
                    hora = fila[Citas.hora],
                    motivo = fila[Citas.motivo],
                    estado = fila[Citas.estado]
                )

            }

        }

    }

}