package com.halyxsynck.backend.routes

import com.halyxsynck.backend.dto.AgregarMedicamentoRequest
import com.halyxsynck.backend.dto.RegistrarHistorialRequest
import com.halyxsynck.backend.repository.PacienteRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pacienteRoutes() {

    val repository = PacienteRepository()

    route("/paciente") {

        get("/info") {

            val correo = call.request.queryParameters["correo"]

            if (correo == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Falta el correo"))
                return@get
            }

            val info = repository.obtenerInfoPaciente(correo)

            if (info == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("mensaje" to "No se encontró información médica"))
            } else {
                call.respond(info)
            }

        }

        // NUEVO: solo la info que le corresponde a ese doctor
        get("/info-doctor") {

            val correoPaciente = call.request.queryParameters["correoPaciente"]
            val correoDoctor = call.request.queryParameters["correoDoctor"]

            if (correoPaciente == null || correoDoctor == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "Faltan parámetros"))
                return@get
            }

            val info = repository.obtenerInfoParaDoctor(correoPaciente, correoDoctor)

            if (info == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("mensaje" to "No se encontró información médica"))
            } else {
                call.respond(info)
            }

        }

        post("/historial") {

            val request = call.receive<RegistrarHistorialRequest>()

            val guardado = repository.registrarHistorial(request)

            if (guardado) {
                call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Historial guardado correctamente"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "No se pudo guardar, verifica el correo del paciente"))
            }

        }

        post("/medicamento") {

            val request = call.receive<AgregarMedicamentoRequest>()

            val guardado = repository.agregarMedicamento(request)

            if (guardado) {
                call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Medicamento agregado correctamente"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("mensaje" to "No se pudo agregar, verifica el correo"))
            }

        }

    }

}