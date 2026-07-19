package com.halyxsynck

import com.halyxsynck.backend.database.DatabaseFactory
import com.halyxsynck.backend.routes.authRoutes
import com.halyxsynck.backend.routes.pacienteRoutes
import com.halyxsynck.backend.routes.citaRoutes
import com.halyxsynck.backend.routes.doctorRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    DatabaseFactory.init()

    routing {

        get("/") {
            call.respondText("HALYXSYNC BACKEND OK")
        }

        authRoutes()

        pacienteRoutes()

        citaRoutes() // NUEVO

        doctorRoutes() // NUEVO

    }
}