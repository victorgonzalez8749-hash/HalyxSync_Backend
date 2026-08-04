package com.halyxsynck.backend.routes

import com.halyxsynck.backend.dto.EnviarMensajeRequest
import com.halyxsynck.backend.repository.MensajeRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mensajeRoutes() {

    val repository = MensajeRepository()

    route("/mensajes") {

        post("/enviar") {

            val request = call.receive<EnviarMensajeRequest>()
            val enviado = repository.enviarMensaje(request)

            if (enviado) {
                call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Mensaje enviado"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "No se pudo enviar"))
            }

        }

        get("/conversacion") {

            val correoUsuario = call.request.queryParameters["correoUsuario"]
            val correoOtro = call.request.queryParameters["correoOtro"]

            if (correoUsuario == null || correoOtro == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Faltan parámetros"))
                return@get
            }

            call.respond(repository.obtenerConversacion(correoUsuario, correoOtro))

        }

        get("/conversaciones-paciente") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo"))
                return@get
            }

            call.respond(repository.obtenerConversacionesPaciente(correo))

        }

        get("/conversaciones-doctor") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo"))
                return@get
            }

            call.respond(repository.obtenerConversacionesDoctor(correo))

        }

    }

}