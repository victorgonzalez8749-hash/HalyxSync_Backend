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

        get("/estadisticas") {

            val correo = call.request.queryParameters["correo"]
            val fechaHoy = call.request.queryParameters["fecha"]

            if (correo == null || fechaHoy == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Faltan parámetros"))
                return@get
            }

            val totalPacientes = repository.obtenerPacientes(correo).size
            val citasHoy = repository.obtenerCitasHoyDetalle(correo, fechaHoy).size

            call.respond(mapOf("totalPacientes" to totalPacientes, "citasHoy" to citasHoy))

        }

        get("/citas-hoy") {

            val correo = call.request.queryParameters["correo"]
            val fechaHoy = call.request.queryParameters["fecha"]

            if (correo == null || fechaHoy == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Faltan parámetros"))
                return@get
            }

            call.respond(repository.obtenerCitasHoyDetalle(correo, fechaHoy))

        }

    }

}