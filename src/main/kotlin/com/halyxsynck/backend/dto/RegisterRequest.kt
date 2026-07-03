package com.halyxsynck.backend.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(

    val nombre: String,

    val apellidoPaterno: String,

    val apellidoMaterno: String,

    val correo: String,

    val telefono: String,

    val contrasena: String,

    val rol: String,

    val cedulaProfesional: String? = null,

    val especialidad: String? = null

)