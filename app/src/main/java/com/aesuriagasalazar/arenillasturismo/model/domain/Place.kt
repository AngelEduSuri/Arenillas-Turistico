package com.aesuriagasalazar.arenillasturismo.model.domain

import android.os.Parcelable
import androidx.lifecycle.Transformations.map
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlaceEntity
import com.mapbox.maps.extension.style.expressions.dsl.generated.max
import kotlinx.android.parcel.Parcelize

/** Data class que representa el dominio de la aplicacion, lugares turisticos **/
@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
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
) : Parcelable

/** Convierte el modelo de entidad de Room en una lista de dominio de lugares (Place) **/
fun List<PlaceEntity>.asDomainModelList(): List<Place> {
    return map {
        Place(
            id = it.id,
            nombre = it.nombre,
            categoria = it.categoria,
            descripcion = it.descripcion,
            direccion = it.direccion,
            longitud = it.longitud,
            latitud = it.latitud,
            altitud = it.altitud,
            miniatura = it.miniatura,
            imagenes = it.imagenes.split("*")
        )
    }
}

fun PlaceEntity.asDomainModel(): Place {
    return Place(
        id = this.id,
        nombre = this.nombre,
        categoria = this.categoria,
        descripcion = this.descripcion,
        direccion = this.direccion,
        longitud = this.longitud,
        latitud = this.latitud,
        altitud = this.altitud,
        miniatura = this.miniatura,
        imagenes = this.imagenes.split("*")
    )
}

