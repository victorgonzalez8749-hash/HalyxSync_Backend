package com.halyxsynck.backend.routes

import com.halyxsynck.backend.dto.AgendarCitaRequest
import com.halyxsynck.backend.repository.CitaRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.citaRoutes() {

    val repository = CitaRepository()

    route("/citas") {

        post("/agendar") {

            val request = call.receive<AgendarCitaRequest>()
            val guardada = repository.agendarCita(request)

            if (guardada) {
                call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Cita agendada correctamente"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "No se pudo agendar, verifica los correos"))
            }

        }

        get("/paciente") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo"))
                return@get
            }

            call.respond(repository.obtenerCitasPaciente(correo))

        }

        get("/doctor") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo"))
                return@get
            }

            call.respond(repository.obtenerCitasDoctor(correo))

        }

    }

}