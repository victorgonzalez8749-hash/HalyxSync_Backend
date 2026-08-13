package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.RegistrarTokenRequest
import com.halyxsynck.backend.models.TokensNotificacion
import com.halyxsynck.backend.models.Users
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class NotificacionRepository {

    fun registrarToken(request: RegistrarTokenRequest): Boolean {

        return try {

            transaction {

                val usuario = Users.selectAll().where { Users.correo eq request.correo }.singleOrNull() ?: return@transaction false

                TokensNotificacion.deleteWhere { TokensNotificacion.usuarioId eq usuario[Users.id] }

                TokensNotificacion.insert {
                    it[usuarioId] = usuario[Users.id]
                    it[token] = request.token
                }

                true

            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun enviarNotificacion(correoDestinatario: String, titulo: String, mensaje: String) {

        try {

            val token = transaction {
                val usuario = Users.selectAll().where { Users.correo eq correoDestinatario }.singleOrNull() ?: return@transaction null
                TokensNotificacion.selectAll().where { TokensNotificacion.usuarioId eq usuario[Users.id] }.singleOrNull()?.get(TokensNotificacion.token)
            } ?: return

            val mensajeFcm = Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle(titulo)
                        .setBody(mensaje)
                        .build()
                )
                .build()

            FirebaseMessaging.getInstance().send(mensajeFcm)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}