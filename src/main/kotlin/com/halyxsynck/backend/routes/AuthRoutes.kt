package com.halyxsynck.backend.routes

import com.halyxsynck.backend.dto.RegisterRequest
import com.halyxsynck.backend.repository.AuthRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {

    val repository = AuthRepository()

    route("/auth") {

        post("/register") {

            val request = call.receive<RegisterRequest>()

            val registrado = repository.registrarUsuario(request)

            if (registrado) {

                call.respond(
                    HttpStatusCode.Created,
                    mapOf(
                        "success" to true,
                        "mensaje" to "Usuario registrado correctamente"
                    )
                )

            } else {

                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "success" to false,
                        "mensaje" to "No se pudo registrar el usuario"
                    )
                )

            }

        }

    }

}