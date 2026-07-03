package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.RegisterRequest
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class AuthRepository {

    fun registrarUsuario(request: RegisterRequest): Boolean {

        return try {

            transaction {

                Users.insert {

                    it[nombre] = request.nombre

                    it[apellidoPaterno] = request.apellidoPaterno

                    it[apellidoMaterno] = request.apellidoMaterno

                    it[correo] = request.correo

                    it[telefono] = request.telefono

                    it[contrasena] = request.contrasena

                    it[rol] = request.rol

                    it[cedulaProfesional] = request.cedulaProfesional

                    it[especialidad] = request.especialidad

                }

            }

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false

        }

    }

}