package com.halyxsynck.backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

object FirebaseConfig {

    fun init() {

        try {

            val archivoCredenciales = FileInputStream("src/main/resources/firebase-service-account.json")

            val credenciales = GoogleCredentials.fromStream(archivoCredenciales)

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