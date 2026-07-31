package com.halyxsynck.backend.config

object CatalogoPadecimientos {

    private fun normalizar(texto: String): String {
        return texto.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
    }

    private val mapa = mapOf(
        // Respiratorio → Neumología
        "gripe" to "Neumología", "resfriado" to "Neumología", "asma" to "Neumología",
        "bronquitis" to "Neumología", "neumonia" to "Neumología", "sinusitis" to "Neumología",
        "epoc" to "Neumología", "congestion nasal" to "Neumología",
        "fibrosis pulmonar" to "Neumología", "tuberculosis" to "Neumología",
        "apnea" to "Neumología", "bronquiolitis" to "Neumología", "enfisema" to "Neumología",

        // ORL → Otorrinolaringología
        "faringitis" to "Otorrinolaringología", "amigdalitis" to "Otorrinolaringología",
        "dolor de oido" to "Otorrinolaringología", "infeccion de garganta" to "Otorrinolaringología",
        "dolor de garganta" to "Otorrinolaringología", "otitis" to "Otorrinolaringología",
        "vertigo posicional" to "Otorrinolaringología", "perdida de audicion" to "Otorrinolaringología",
        "zumbido" to "Otorrinolaringología", "tabique" to "Otorrinolaringología",
        "rinitis" to "Otorrinolaringología",

        // Cardiovascular → Cardiología
        "hipertension" to "Cardiología", "taquicardia" to "Cardiología", "arritmia" to "Cardiología",
        "insuficiencia cardiaca" to "Cardiología", "colesterol" to "Cardiología",
        "trigliceridos" to "Cardiología", "varices" to "Cardiología",
        "angina" to "Cardiología", "soplo" to "Cardiología", "trombosis" to "Cardiología",
        "aneurisma" to "Cardiología",

        // Endocrino → Endocrinología
        "diabetes" to "Endocrinología", "hipotiroidismo" to "Endocrinología",
        "hipertiroidismo" to "Endocrinología", "obesidad" to "Endocrinología",
        "resistencia a la insulina" to "Endocrinología", "bocio" to "Endocrinología",
        "sindrome metabolico" to "Endocrinología", "suprarrenal" to "Endocrinología",
        "hipoglucemia" to "Endocrinología",

        // Gastrointestinal → Gastroenterología
        "gastritis" to "Gastroenterología", "colitis" to "Gastroenterología",
        "reflujo" to "Gastroenterología", "estreñimiento" to "Gastroenterología",
        "estrenimiento" to "Gastroenterología", "diarrea" to "Gastroenterología",
        "intestino irritable" to "Gastroenterología", "ulcera" to "Gastroenterología",
        "hemorroides" to "Gastroenterología", "higado graso" to "Gastroenterología",
        "nauseas" to "Gastroenterología", "pancreatitis" to "Gastroenterología",
        "calculos biliares" to "Gastroenterología", "hepatitis" to "Gastroenterología",
        "crohn" to "Gastroenterología", "lactosa" to "Gastroenterología",
        "celiaquia" to "Gastroenterología",

        // Reumatología
        "artritis" to "Reumatología", "artrosis" to "Reumatología",
        "fibromialgia" to "Reumatología", "lupus" to "Reumatología", "gota" to "Reumatología",
        "espondilitis" to "Reumatología", "sjogren" to "Reumatología",

        // Ortopedia y Traumatología
        "osteoporosis" to "Ortopedia y Traumatología", "espalda" to "Ortopedia y Traumatología",
        "ciatica" to "Ortopedia y Traumatología", "tendinitis" to "Ortopedia y Traumatología",
        "fractura" to "Ortopedia y Traumatología", "esguince" to "Ortopedia y Traumatología",
        "escoliosis" to "Ortopedia y Traumatología", "hernia discal" to "Ortopedia y Traumatología",
        "bursitis" to "Ortopedia y Traumatología", "tunel carpiano" to "Ortopedia y Traumatología",

        // Dermatología
        "acne" to "Dermatología", "psoriasis" to "Dermatología", "vitiligo" to "Dermatología",
        "dermatitis" to "Dermatología", "eczema" to "Dermatología", "urticaria" to "Dermatología",
        "hongos" to "Dermatología", "rosacea" to "Dermatología", "caida del cabello" to "Dermatología",
        "verrugas" to "Dermatología", "melasma" to "Dermatología",

        // Neurología
        "migraña" to "Neurología", "epilepsia" to "Neurología", "vertigo" to "Neurología",
        "dolor de cabeza" to "Neurología", "paralisis facial" to "Neurología",
        "temblor" to "Neurología", "neuropatia" to "Neurología", "alzheimer" to "Neurología",
        "parkinson" to "Neurología", "esclerosis multiple" to "Neurología",

        // Psiquiatría
        "ansiedad" to "Psiquiatría", "depresion" to "Psiquiatría", "insomnio" to "Psiquiatría",
        "estres" to "Psiquiatría", "bipolar" to "Psiquiatría", "panico" to "Psiquiatría",
        "obsesivo compulsivo" to "Psiquiatría", "fobia" to "Psiquiatría",

        // Ginecología y Obstetricia
        "embarazo" to "Ginecología y Obstetricia", "menstruacion" to "Ginecología y Obstetricia",
        "sindrome premenstrual" to "Ginecología y Obstetricia", "menopausia" to "Ginecología y Obstetricia",
        "quistes ovaricos" to "Ginecología y Obstetricia", "endometriosis" to "Ginecología y Obstetricia",
        "miomas" to "Ginecología y Obstetricia", "infertilidad" to "Ginecología y Obstetricia",

        // Oftalmología
        "conjuntivitis" to "Oftalmología", "miopia" to "Oftalmología",
        "vista cansada" to "Oftalmología", "cataratas" to "Oftalmología",
        "glaucoma" to "Oftalmología", "ojo seco" to "Oftalmología", "astigmatismo" to "Oftalmología",

        // Nefrología
        "renal" to "Nefrología", "calculos renales" to "Nefrología",
        "insuficiencia renal" to "Nefrología",

        // Urología
        "infeccion urinaria" to "Urología", "prostatitis" to "Urología",
        "disfuncion erectil" to "Urología", "incontinencia urinaria" to "Urología",

        // Hematología / Oncología
        "anemia" to "Hematología", "leucemia" to "Oncología",
        "trombocitopenia" to "Hematología", "hemofilia" to "Hematología",
        "cancer" to "Oncología",

        // Infectología
        "infeccion viral" to "Infectología", "infeccion bacteriana" to "Infectología",
        "covid" to "Infectología", "dengue" to "Infectología",
        "mononucleosis" to "Infectología", "herpes" to "Infectología",

        // Alergología
        "alergia" to "Alergología", "dermatitis atopica" to "Alergología",

        // Odontología
        "caries" to "Odontología", "gingivitis" to "Odontología",
        "dolor de muela" to "Odontología", "sensibilidad dental" to "Odontología",

        // Medicina General
        "fiebre" to "Medicina General", "fatiga" to "Medicina General",
        "mareo" to "Medicina General", "deshidratacion" to "Medicina General",

        // Nutrición
        "desnutricion" to "Nutrición", "sobrepeso" to "Nutrición"
    )

    fun sugerirEspecialidad(padecimiento: String): String {

        val texto = normalizar(padecimiento.trim())

        for ((clave, especialidad) in mapa) {
            if (texto.contains(normalizar(clave))) {
                return especialidad
            }
        }

        return "Medicina General"

    }

    fun agruparPorEspecialidad(padecimientos: List<String>): Map<String, List<String>> {
        return padecimientos.groupBy { sugerirEspecialidad(it) }
    }

}