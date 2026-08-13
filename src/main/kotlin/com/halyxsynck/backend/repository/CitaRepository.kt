package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.AgendarCitaRequest
import com.halyxsynck.backend.dto.CancelarCitaRequest
import com.halyxsynck.backend.dto.CitaDto
import com.halyxsynck.backend.models.Citas
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class CitaRepository {

    private val notificaciones = NotificacionRepository()

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

            }.also { exito ->

                if (exito) {
                    notificaciones.enviarNotificacion(
                        request.correoPaciente,
                        "Cita agendada",
                        "Tu cita con ${request.medico} quedó registrada para el ${request.fecha} a las ${request.hora}"
                    )
                }

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

    // NUEVO: cancelar una cita con motivo
    fun cancelarCita(request: CancelarCitaRequest): Boolean {

        return try {

            var correoDoctorParaAvisar: String? = null
            var nombrePacienteParaAvisar: String = ""

            transaction {

                val cita = Citas.selectAll().where { Citas.id eq request.citaId }.singleOrNull()
                val doctorId = cita?.get(Citas.doctorId)
                val pacienteId = cita?.get(Citas.pacienteId)

                if (doctorId != null) {
                    correoDoctorParaAvisar = Users.selectAll().where { Users.id eq doctorId }.singleOrNull()?.get(Users.correo)
                }
                if (pacienteId != null) {
                    val pacienteFila = Users.selectAll().where { Users.id eq pacienteId }.singleOrNull()
                    nombrePacienteParaAvisar = pacienteFila?.get(Users.nombre) ?: ""
                }

                val filasActualizadas = Citas.update({ Citas.id eq request.citaId }) {
                    it[estado] = "Cancelada"
                    it[motivo] = "Cancelada: ${request.motivoCancelacion}"
                }

                filasActualizadas > 0

            }.also { exito ->

                if (exito && correoDoctorParaAvisar != null) {
                    notificaciones.enviarNotificacion(
                        correoDoctorParaAvisar!!,
                        "Cita cancelada",
                        "$nombrePacienteParaAvisar canceló su cita: ${request.motivoCancelacion}"
                    )
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

}