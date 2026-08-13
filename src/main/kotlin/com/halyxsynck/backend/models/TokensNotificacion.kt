package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object TokensNotificacion : Table("tokens_notificacion") {

    val id = integer("id").autoIncrement()
    val usuarioId = integer("usuario_id").references(Users.id)
    val token = varchar("token", 500)

    override val primaryKey = PrimaryKey(id)

}
