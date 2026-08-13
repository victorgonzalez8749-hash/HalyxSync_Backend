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

                // 🔥 CORRECCIÓN: Borra el token exacto si ya existía para evitar duplicados,
                // pero NO borra los tokens de otros teléfonos/tablets del mismo usuario.
                TokensNotificacion.deleteWhere { TokensNotificacion.token eq request.token }

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
            // 🔥 CORRECCIÓN: Obtiene todos los tokens registrados de ese médico (por si usa Android e iOS)
            val tokens = transaction {
                val usuario = Users.selectAll().where { Users.correo eq correoDestinatario }.singleOrNull() ?: return@transaction emptyList()
                TokensNotificacion.selectAll()
                    .where { TokensNotificacion.usuarioId eq usuario[Users.id] }
                    .map { it[TokensNotificacion.token] }
            }

            if (tokens.isEmpty()) return

            // Envía la notificación a cada uno de sus dispositivos
            for (token in tokens) {
                val mensajeFcm = Message.builder()
                    .setToken(token)
                    .setNotification(
                        Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build()
                    )
                    // 🔥 CORRECCIÓN: Payload de datos necesario para despertar apps cerradas/segundo plano
                    .putAllData(mapOf(
                        "title" to titulo,
                        "body" to mensaje,
                        "click_action" to "FLUTTER_NOTIFICATION_CLICK"
                    ))
                    .build()

                FirebaseMessaging.getInstance().send(mensajeFcm)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
