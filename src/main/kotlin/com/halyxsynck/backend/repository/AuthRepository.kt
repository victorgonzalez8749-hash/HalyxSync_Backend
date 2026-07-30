package com.halyxsynck.backend.repository

import com.halyxsynck.backend.config.CatalogoPadecimientos
import com.halyxsynck.backend.dto.RegisterRequest
import com.halyxsynck.backend.models.HistorialMedico
import com.halyxsynck.backend.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import com.halyxsynck.backend.dto.LoginRequest
import com.halyxsynck.backend.dto.LoginResponse
import org.jetbrains.exposed.sql.selectAll

class AuthRepository {

    fun registrarUsuario(request: RegisterRequest): Boolean {

        return try {

            transaction {

                val insertStatement = Users.insert {

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

                val nuevoId = insertStatement[Users.id]

                if (request.rol == "PACIENTE" && !request.padecimientos.isNullOrEmpty()) {

                    val especialidadSugerida = CatalogoPadecimientos.sugerirEspecialidad(request.padecimientos)

                    val doctoresConEspecialidad = Users
                        .selectAll()
                        .where { (Users.rol eq "DOCTOR") and (Users.especialidad eq especialidadSugerida) }
                        .toList()

                    val candidatos = if (doctoresConEspecialidad.isNotEmpty()) {
                        doctoresConEspecialidad
                    } else {
                        Users.selectAll().where { Users.rol eq "DOCTOR" }.toList()
                    }

                    if (candidatos.isNotEmpty()) {

                        val doctorElegido = candidatos.minByOrNull { fila ->
                            val doctorId = fila[Users.id]
                            HistorialMedico.selectAll().where { HistorialMedico.doctorId eq doctorId }.count()
                        }

                        if (doctorElegido != null) {

                            HistorialMedico.insert {
                                it[pacienteId] = nuevoId
                                it[doctorId] = doctorElegido[Users.id]
                                it[edad] = request.edad ?: 0
                                it[sexo] = request.sexo ?: "No especificado"
                                it[padecimientos] = request.padecimientos.joinToString(", ")
                                it[medicoAsignado] = "${doctorElegido[Users.nombre]} ${doctorElegido[Users.apellidoPaterno]}"
                                it[especialidadMedico] = doctorElegido[Users.especialidad] ?: especialidadSugerida
                            }

                        }

                    }

                }

                true

            }

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
                            rol = usuario[Users.rol],
                            especialidad = usuario[Users.especialidad],
                            cedulaProfesional = usuario[Users.cedulaProfesional]
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