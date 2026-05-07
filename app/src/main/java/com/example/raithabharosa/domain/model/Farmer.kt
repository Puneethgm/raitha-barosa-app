package com.example.raitha_bharosa.domain.model

data class Farmer(
    val id: Int = 0,
    val name: String,
    val village: String,
    val primaryCrop: String,
    val language: String, // "en" or "kn"
    val createdAt: Long = System.currentTimeMillis()
)
