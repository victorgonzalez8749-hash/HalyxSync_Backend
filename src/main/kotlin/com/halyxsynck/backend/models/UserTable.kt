package com.halyxsynck.backend.models

import org.jetbrains.exposed.sql.Table

object Users : Table("usuarios") {

    val id = integer("id").autoIncrement()

    val nombre = varchar("nombre",100)

    val apellidoPaterno = varchar("apellido_paterno",100)

    val apellidoMaterno = varchar("apellido_materno",100)

    val correo = varchar("correo",120).uniqueIndex()

    val telefono = varchar("telefono",20)

    val contrasena = varchar("contrasena",255)

    val rol = varchar("rol",20)

    val cedulaProfesional = varchar("cedula_profesional",30).nullable()

    val especialidad = varchar("especialidad",100).nullable()

    override val primaryKey = PrimaryKey(id)
}