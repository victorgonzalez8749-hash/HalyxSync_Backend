package com.halyxsynck.backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.ByteArrayInputStream
import java.io.File

object FirebaseConfig {

    fun init() {

        try {

            val credencialesJson = System.getenv("FIREBASE_CREDENTIALS_JSON")

            val credenciales = if (!credencialesJson.isNullOrBlank()) {
                // En Railway (producción): lee desde la variable de entorno
                GoogleCredentials.fromStream(ByteArrayInputStream(credencialesJson.toByteArray()))
            } else {
                // En tu computadora (local): lee desde el archivo
                val archivo = File("src/main/resources/firebase-service-account.json")
                if (!archivo.exists()) {
                    println("⚠️ No se encontró el archivo de credenciales de Firebase ni la variable de entorno.")
                    return
                }
                GoogleCredentials.fromStream(archivo.inputStream())
            }

            val opciones = FirebaseOptions.builder()
                .setCredentials(credenciales)
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(opciones)
                println("✅ Firebase Admin conectado correctamente.")
            }

        } catch (e: Exception) {
            println("⚠️ No se pudo inicializar Firebase: ${e.message}")
        }

    }

}