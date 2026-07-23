package com.halyxsynck.backend.routes

import com.halyxsynck.backend.dto.SubirEstudioRequest
import com.halyxsynck.backend.repository.EstudioRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.estudioRoutes() {

    val repository = EstudioRepository()

    route("/estudios") {

        post("/subir") {

            val request = call.receive<SubirEstudioRequest>()

            val guardado = repository.subirEstudio(
                request.correoPaciente,
                request.imagenBase64,
                request.descripcion,
                request.fecha
            )

            if (guardado) {
                call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Estudio subido correctamente"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "No se pudo subir el estudio"))
            }

        }

        get("/paciente") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo"))
                return@get
            }

            call.respond(repository.obtenerEstudios(correo))

        }

    }

}