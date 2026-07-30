package com.halyxsynck.backend.config

object CatalogoPadecimientos {

    // Palabra clave (en minúsculas) → especialidad correspondiente
    private val mapa = mapOf(
        "diabetes" to "Endocrinología",
        "tiroides" to "Endocrinología",
        "hipertension" to "Cardiología",
        "hipertensión" to "Cardiología",
        "corazon" to "Cardiología",
        "corazón" to "Cardiología",
        "arritmia" to "Cardiología",
        "asma" to "Neumología",
        "pulmon" to "Neumología",
        "pulmón" to "Neumología",
        "gastritis" to "Gastroenterología",
        "colitis" to "Gastroenterología",
        "estomago" to "Gastroenterología",
        "estómago" to "Gastroenterología",
        "artritis" to "Reumatología",
        "articulaciones" to "Reumatología",
        "fractura" to "Ortopedia y Traumatología",
        "hueso" to "Ortopedia y Traumatología",
        "embarazo" to "Ginecología y Obstetricia",
        "menstruacion" to "Ginecología y Obstetricia",
        "menstruación" to "Ginecología y Obstetricia",
        "piel" to "Dermatología",
        "acne" to "Dermatología",
        "acné" to "Dermatología",
        "ansiedad" to "Psiquiatría",
        "depresion" to "Psiquiatría",
        "depresión" to "Psiquiatría",
        "vision" to "Oftalmología",
        "visión" to "Oftalmología",
        "ojos" to "Oftalmología",
        "oido" to "Otorrinolaringología",
        "oído" to "Otorrinolaringología",
        "garganta" to "Otorrinolaringología",
        "rinon" to "Nefrología",
        "riñon" to "Nefrología",
        "riñón" to "Nefrología",
        "cancer" to "Oncología",
        "cáncer" to "Oncología",
        "anemia" to "Hematología",
        "infeccion" to "Infectología",
        "infección" to "Infectología"
    )

    // Regresa la especialidad más probable según los padecimientos escritos,
    // o "Medicina General" si no encuentra ninguna coincidencia
    fun sugerirEspecialidad(padecimientos: List<String>): String {

        for (padecimiento in padecimientos) {
            val texto = padecimiento.lowercase().trim()
            for ((clave, especialidad) in mapa) {
                if (texto.contains(clave)) {
                    return especialidad
                }
            }
        }

        return "Medicina General"

    }

}