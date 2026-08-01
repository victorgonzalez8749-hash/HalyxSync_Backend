package com.halyxsynck.backend.repository

import com.halyxsynck.backend.config.CloudinaryConfig
import com.halyxsynck.backend.dto.EstudioDto
import com.halyxsynck.backend.models.Estudios
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Base64

class EstudioRepository {

    fun subirEstudio(correoPaciente: String, correoDoctor: String, imagenBase64: String, descripcion: String, fecha: String): Boolean {

        return try {

            val bytes = Base64.getDecoder().decode(imagenBase64)

            val resultado = CloudinaryConfig.cloudinary.uploader().upload(
                bytes,
                mapOf("folder" to "halyxsync_estudios")
            )

            val url = resultado["secure_url"] as String

            transaction {

                val usuario = Users
                    .selectAll()
                    .where { Users.correo eq correoPaciente }
                    .singleOrNull() ?: return@transaction false

                val doctor = Users
                    .selectAll()
                    .where { Users.correo eq correoDoctor }
                    .singleOrNull()

                Estudios.insert {
                    it[pacienteId] = usuario[Users.id]
                    it[doctorId] = doctor?.get(Users.id)
                    it[Estudios.url] = url
                    it[Estudios.descripcion] = descripcion
                    it[Estudios.fecha] = fecha
                }

                true

            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    // Usado por el propio paciente: ve TODOS sus estudios, sin importar a quién se los mandó
    fun obtenerEstudios(correoPaciente: String): List<EstudioDto> {

        return transaction {

            val usuario = Users
                .selectAll()
                .where { Users.correo eq correoPaciente }
                .singleOrNull() ?: return@transaction emptyList()

            Estudios
                .selectAll()
                .where { Estudios.pacienteId eq usuario[Users.id] }
                .map {
                    val doctorNombre = it[Estudios.doctorId]?.let { docId ->
                        Users.selectAll().where { Users.id eq docId }.singleOrNull()?.get(Users.nombre)
                    } ?: ""
                    EstudioDto(
                        id = it[Estudios.id],
                        url = it[Estudios.url],
                        descripcion = it[Estudios.descripcion],
                        fecha = it[Estudios.fecha],
                        doctorNombre = doctorNombre
                    )
                }

        }

    }

    // NUEVO: usado por el doctor — solo los estudios que ESE paciente le mandó a él
    fun obtenerEstudiosParaDoctor(correoPaciente: String, correoDoctor: String): List<EstudioDto> {

        return transaction {

            val paciente = Users
                .selectAll()
                .where { Users.correo eq correoPaciente }
                .singleOrNull() ?: return@transaction emptyList()

            val doctor = Users
                .selectAll()
                .where { Users.correo eq correoDoctor }
                .singleOrNull() ?: return@transaction emptyList()

            Estudios
                .selectAll()
                .where { (Estudios.pacienteId eq paciente[Users.id]) and (Estudios.doctorId eq doctor[Users.id]) }
                .map {
                    EstudioDto(
                        id = it[Estudios.id],
                        url = it[Estudios.url],
                        descripcion = it[Estudios.descripcion],
                        fecha = it[Estudios.fecha]
                    )
                }

        }

    }

}