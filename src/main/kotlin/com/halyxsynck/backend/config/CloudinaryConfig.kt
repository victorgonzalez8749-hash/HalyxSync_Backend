package com.halyxsynck.backend.config

import com.cloudinary.Cloudinary

object CloudinaryConfig {

    const val CLOUD_NAME = "lohjgh6q"

    const val API_KEY = "766846139412475"

    const val API_SECRET = "u8b2jkhxtGPrvh6LOA7heit3umY"

    val cloudinary: Cloudinary by lazy {
        Cloudinary(
            mapOf(
                "cloud_name" to CLOUD_NAME,
                "api_key" to API_KEY,
                "api_secret" to API_SECRET
            )
        )
    }

}