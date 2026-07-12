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

                val usuario = Users
                    .selectAll()
                    .where { Users.correo eq request.correoPaciente }
                    .singleOrNull() ?: return@transaction false

                Citas.insert {
                    it[pacienteId] = usuario[Users.id]
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

    fun obtenerCitas(correo: String): List<CitaDto> {

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
                        medico = it[Citas.medico],
                        especialidad = it[Citas.especialidad],
                        fecha = it[Citas.fecha],
                        hora = it[Citas.hora],
                        motivo = it[Citas.motivo]
                    )
                }

        }

    }

}