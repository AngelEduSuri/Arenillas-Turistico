package com.aesuriagasalazar.arenillasturismo.model.domain

data class Place(
    val id: Int = 0,
    val nombre: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val longitud: Double = 0.0,
    val latitud: Double = 0.0,
    val altitud: Int = 0,
    val miniatura: String = "",
    val imagenes: List<String> = listOf()
)

