package com.halyxsynck

import com.halyxsynck.backend.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {

        get("/") {
            call.respondText("HALYXSYNC BACKEND OK")
        }

        authRoutes()
    }
}