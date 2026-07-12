package com.halyxsynck.backend.repository

import com.halyxsynck.backend.dto.RegisterRequest
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import com.halyxsynck.backend.dto.LoginRequest
import com.halyxsynck.backend.dto.LoginResponse
import org.jetbrains.exposed.sql.selectAll

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

            println("===================================")
            println("ERROR AL REGISTRAR USUARIO")
            println("Mensaje: ${e.message}")
            e.printStackTrace()
            println("===================================")

            false

        }
    }

    fun login(request: LoginRequest): LoginResponse {

        return try {

            transaction {

                val usuario = Users
                    .selectAll()
                    .where { Users.correo eq request.correo }
                    .singleOrNull()

                if (usuario == null) {

                    LoginResponse(
                        success = false,
                        mensaje = "Correo o contraseña incorrectos"
                    )

                } else {

                    val password = usuario[Users.contrasena]

                    if (password != request.contrasena) {

                        LoginResponse(
                            success = false,
                            mensaje = "Correo o contraseña incorrectos"
                        )

                    } else {

                        LoginResponse(
                            success = true,
                            mensaje = "Bienvenido",
                            nombre = usuario[Users.nombre],
                            rol = usuario[Users.rol]
                        )

                    }

                }

            }

        } catch (e: Exception) {

            e.printStackTrace()

            LoginResponse(
                success = false,
                mensaje = "Error del servidor"
            )

        }

    }

}