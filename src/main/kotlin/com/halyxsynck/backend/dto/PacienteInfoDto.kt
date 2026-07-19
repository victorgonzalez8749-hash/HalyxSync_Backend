package com.halyxsynck.backend.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicamentoDto(
    val nombre: String,
    val dosis: String,
    val horario: String
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

@Serializable
data class CitaDto(
    val medico: String,
    val especialidad: String,
    val fecha: String,
    val hora: String,
    val motivo: String
)

@Serializable
data class AgendarCitaRequest(
    val correoPaciente: String,
    val medico: String,
    val especialidad: String,
    val fecha: String,
    val hora: String,
    val motivo: String
)

@Serializable
data class RegistrarHistorialRequest(
    val correoPaciente: String,
    val correoDoctor: String,
    val edad: Int,
    val padecimientos: List<String>,
    val medicoAsignado: String,
    val especialidadMedico: String,
    val medicamentos: List<MedicamentoDto>
)

@Serializable
data class PacienteResumenDto(
    val correo: String,
    val nombreCompleto: String,
    val edad: Int
)