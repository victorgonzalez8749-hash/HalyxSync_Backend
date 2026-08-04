package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object Mensajes : Table("mensajes") {

    val id = integer("id").autoIncrement()

    val remitenteId = integer("remitente_id").references(Users.id)

    val destinatarioId = integer("destinatario_id").references(Users.id)

    val texto = varchar("texto", 1000)

    val fecha = varchar("fecha", 20)

    val hora = varchar("hora", 10)

    val leido = bool("leido").default(false)

    override val primaryKey = PrimaryKey(id)

}