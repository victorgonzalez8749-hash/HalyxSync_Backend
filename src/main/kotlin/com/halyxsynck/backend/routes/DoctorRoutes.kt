package com.halyxsynck.backend.routes

import com.halyxsynck.backend.repository.DoctorRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.doctorRoutes() {

    val repository = DoctorRepository()

    route("/doctor") {

        get("/pacientes") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo del doctor"))
                return@get
            }

            val pacientes = repository.obtenerPacientes(correo)

            call.respond(pacientes)

        }

    }

}