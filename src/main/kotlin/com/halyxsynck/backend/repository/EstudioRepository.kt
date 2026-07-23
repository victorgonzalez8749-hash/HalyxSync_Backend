package com.halyxsynck.backend.repository

import com.halyxsynck.backend.config.CloudinaryConfig
import com.halyxsynck.backend.dto.EstudioDto
import com.halyxsynck.backend.models.Estudios
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Base64

class EstudioRepository {

    fun subirEstudio(correoPaciente: String, imagenBase64: String, descripcion: String, fecha: String): Boolean {

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

                Estudios.insert {
                    it[pacienteId] = usuario[Users.id]
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