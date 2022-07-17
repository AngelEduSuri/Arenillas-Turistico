package com.aesuriagasalazar.arenillasturismo.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Data class que representa la entidad de la base de datos local **/
@Entity(tableName = "list_places_table")
data class PlaceEntityDb(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val nombre: String = "",
    @ColumnInfo(name = "category")
    val categoria: String = "",
    @ColumnInfo(name = "description")
    val descripcion: String = "",
    @ColumnInfo(name = "address")
    val direccion: String = "",
    @ColumnInfo(name = "lon")
    val longitud: Double = 0.0,
    @ColumnInfo(name = "lat")
    val latitud: Double = 0.0,
    @ColumnInfo(name = "alt")
    val altitud: Int = 0,
    @ColumnInfo(name = "thumbnail")
    val miniatura: String = "",
    @ColumnInfo(name = "images")
    val imagenes: String = ""
)

/** Convierte la lista de datos en un modelo de entidad de base de datos Room **/
fun List<Place>.asEntityModelList(): List<PlaceEntityDb>{
    return map {
        PlaceEntityDb(
            id = it.id,
            nombre = it.nombre,
            categoria = it.categoria,
            descripcion = it.descripcion,
            direccion = it.direccion,
            longitud = it.longitud,
            latitud = it.latitud,
            altitud = it.altitud,
            miniatura = it.miniatura,
            imagenes = it.imagenes.joinToString("*")
        )
    }
}