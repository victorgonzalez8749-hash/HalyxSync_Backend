package com.halyxsynck

import com.halyxsynck.backend.database.DatabaseFactory
import com.halyxsynck.backend.routes.authRoutes
import com.halyxsynck.backend.routes.pacienteRoutes
import com.halyxsynck.backend.routes.citaRoutes
import com.halyxsynck.backend.routes.doctorRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.halyxsynck.backend.routes.estudioRoutes
import com.halyxsynck.backend.routes.mensajeRoutes
import com.halyxsynck.backend.config.FirebaseConfig

fun Application.configureRouting() {

    DatabaseFactory.init()

    FirebaseConfig.init()

    routing {

        get("/") {
            call.respondText("HALYXSYNC BACKEND OK")
        }

        authRoutes()

        pacienteRoutes()

        citaRoutes()

        doctorRoutes()

        estudioRoutes()

        mensajeRoutes() // NUEVO


    }
}