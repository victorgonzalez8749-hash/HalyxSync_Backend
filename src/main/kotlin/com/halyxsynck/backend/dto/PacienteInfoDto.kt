package com.halyxsynck.backend.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicamentoDto(
    val nombre: String,
    val dosis: String
)

@Serializable
data class PacienteInfoResponse(
    val nombreCompleto: String,
    val edad: Int,
    val padecimientos: List<String>,
    val medicoAsignado: String,
    val especialidadMedico: String,
    val medicamentos: List<MedicamentoDto>
)