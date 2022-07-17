package com.aesuriagasalazar.arenillasturismo.model.data.local

import androidx.room.*
import com.aesuriagasalazar.arenillasturismo.model.domain.PlaceEntityDb

/** Interfaz DAO (Data Access Object) que representa las consultas a la base de datos local **/
@Dao
interface PlaceDao {

    @Query(value = "SELECT * FROM list_places_table")
    suspend fun getPlaces(): List<PlaceEntityDb>

    @Query(value = "SELECT * FROM list_places_table WHERE category = :category")
    suspend fun getPlacesForCategory(category: String): List<PlaceEntityDb>

    @Query(value = "SELECT COUNT(*) FROM list_places_table")
    suspend fun getListCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaces(places: List<PlaceEntityDb>)

    @Query(value = "SELECT * FROM list_places_table WHERE id = :id")
    suspend fun getPlaceForId(id: Int): PlaceEntityDb

    @Update
    suspend fun updatePlaces(places: List<PlaceEntityDb>)
}