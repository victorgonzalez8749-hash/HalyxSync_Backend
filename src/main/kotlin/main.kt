package com.halyxsynck

import com.halyxsynck.backend.database.DatabaseFactory

fun main(args: Array<String>) {

    DatabaseFactory.init()

    io.ktor.server.netty.EngineMain.main(args)

}