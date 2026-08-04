package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.ConversacionResumenDto
import com.halyxsynck.backend.dto.EnviarMensajeRequest
import com.halyxsynck.backend.dto.MensajeDto
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Mensajes
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class MensajeRepository {

    fun enviarMensaje(request: EnviarMensajeRequest): Boolean {

        return try {

            transaction {

                val remitente = Users.selectAll().where { Users.correo eq request.correoRemitente }.singleOrNull() ?: return@transaction false
                val destinatario = Users.selectAll().where { Users.correo eq request.correoDestinatario }.singleOrNull() ?: return@transaction false

                Mensajes.insert {
                    it[remitenteId] = remitente[Users.id]
                    it[destinatarioId] = destinatario[Users.id]
                    it[texto] = request.texto
                    it[fecha] = request.fecha
                    it[hora] = request.hora
                    it[leido] = false
                }

                true

            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun obtenerConversacion(correoUsuario: String, correoOtro: String): List<MensajeDto> {

        return transaction {

            val usuario = Users.selectAll().where { Users.correo eq correoUsuario }.singleOrNull() ?: return@transaction emptyList()
            val otro = Users.selectAll().where { Users.correo eq correoOtro }.singleOrNull() ?: return@transaction emptyList()

            val usuarioId = usuario[Users.id]
            val otroId = otro[Users.id]

            Mensajes.update({ (Mensajes.remitenteId eq otroId) and (Mensajes.destinatarioId eq usuarioId) }) {
                it[leido] = true
            }

            Mensajes
                .selectAll()
                .where {
                    ((Mensajes.remitenteId eq usuarioId) and (Mensajes.destinatarioId eq otroId)) or
                            ((Mensajes.remitenteId eq otroId) and (Mensajes.destinatarioId eq usuarioId))
                }
                .map { fila ->
                    val esMio = fila[Mensajes.remitenteId] == usuarioId
                    val remitenteInfo = if (esMio) usuario else otro
                    MensajeDto(
                        id = fila[Mensajes.id],
                        remitenteCorreo = remitenteInfo[Users.correo],
                        remitenteNombre = remitenteInfo[Users.nombre],
                        texto = fila[Mensajes.texto],
                        fecha = fila[Mensajes.fecha],
                        hora = fila[Mensajes.hora],
                        esMio = esMio
                    )
                }
                .sortedWith(compareBy({ it.fecha }, { it.hora }))

        }

    }

    fun obtenerConversacionesPaciente(correoPaciente: String): List<ConversacionResumenDto> {

        return transaction {

            val paciente = Users.selectAll().where { Users.correo eq correoPaciente }.singleOrNull() ?: return@transaction emptyList()
            val pacienteId = paciente[Users.id]

            val doctorIds = HistorialMedico
                .selectAll()
                .where { HistorialMedico.pacienteId eq pacienteId }
                .mapNotNull { it[HistorialMedico.doctorId] }
                .distinct()

            doctorIds.mapNotNull { docId ->
                val doctor = Users.selectAll().where { Users.id eq docId }.singleOrNull() ?: return@mapNotNull null
                construirResumen(pacienteId, docId, doctor[Users.correo], doctor[Users.nombre])
            }

        }

    }

    fun obtenerConversacionesDoctor(correoDoctor: String): List<ConversacionResumenDto> {

        return transaction {

            val doctor = Users.selectAll().where { Users.correo eq correoDoctor }.singleOrNull() ?: return@transaction emptyList()
            val doctorId = doctor[Users.id]

            val pacienteIds = HistorialMedico
                .selectAll()
                .where { HistorialMedico.doctorId eq doctorId }
                .map { it[HistorialMedico.pacienteId] }
                .distinct()

            pacienteIds.mapNotNull { pacId ->
                val paciente = Users.selectAll().where { Users.id eq pacId }.singleOrNull() ?: return@mapNotNull null
                construirResumen(doctorId, pacId, paciente[Users.correo], "${paciente[Users.nombre]} ${paciente[Users.apellidoPaterno]}")
            }

        }

    }

    private fun construirResumen(miId: Int, otroId: Int, otroCorreo: String, otroNombre: String): ConversacionResumenDto {

        val mensajes = Mensajes
            .selectAll()
            .where {
                ((Mensajes.remitenteId eq miId) and (Mensajes.destinatarioId eq otroId)) or
                        ((Mensajes.remitenteId eq otroId) and (Mensajes.destinatarioId eq miId))
            }
            .sortedWith(compareBy({ it[Mensajes.fecha] }, { it[Mensajes.hora] }))

        val ultimo = mensajes.lastOrNull()

        val noLeidos = mensajes.count { it[Mensajes.destinatarioId] == miId && !it[Mensajes.leido] }

        return ConversacionResumenDto(
            correo = otroCorreo,
            nombre = otroNombre,
            ultimoMensaje = ultimo?.get(Mensajes.texto) ?: "Sin mensajes todavía",
            fecha = ultimo?.get(Mensajes.fecha) ?: "",
            hora = ultimo?.get(Mensajes.hora) ?: "",
            noLeidos = noLeidos
        )

    }

}