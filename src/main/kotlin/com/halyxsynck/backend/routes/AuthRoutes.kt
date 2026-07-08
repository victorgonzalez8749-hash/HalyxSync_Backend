package com.halyxsynck.backend.routes

import com.halyxsynck.backend.dto.LoginRequest
import com.halyxsynck.backend.dto.RegisterRequest
import com.halyxsynck.backend.repository.AuthRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.halyxsynck.backend.dto.RegisterResponse

fun Route.authRoutes() {

    val repository = AuthRepository()

    route("/auth") {

        post("/login") {

            val request = call.receive<LoginRequest>()

            val respuesta = repository.login(request)

            call.respond(respuesta)
        }

        post("/register") {

            val request = call.receive<RegisterRequest>()

            val registrado = repository.registrarUsuario(request)

            if (registrado) {


                        call.respond(
                            HttpStatusCode.Created,
                            RegisterResponse(
                                success = true,
                                mensaje = "Usuario registrado correctamente"
                            )
                        )

            } else {

                call.respond(
                    HttpStatusCode.BadRequest,
                    RegisterResponse(
                        success = false,
                        mensaje = "No se pudo registrar el usuario"
                    )
                )

            }

        }

    }

}