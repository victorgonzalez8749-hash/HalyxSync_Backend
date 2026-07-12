package com.halyxsynck.backend.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(

    val success: Boolean,

    val mensaje: String

)