package com.aesuriagasalazar.arenillasturismo.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** Interfaz DAO (Data Access Object) que representa las consultas a la base de datos local **/
@Dao
interface PlaceDao {

    @Query(value = "SELECT * FROM list_places_table")
    suspend fun getPlaces(): List<PlaceEntity>

    @Query(value = "SELECT * FROM list_places_table WHERE category = :category")
    suspend fun getPlacesForCategory(category: String): List<PlaceEntity>

    @Query(value = "SELECT COUNT(*) FROM list_places_table")
    suspend fun getListCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaces( videos: List<PlaceEntity>)

    @Query(value = "SELECT * FROM list_places_table WHERE id = :id")
    suspend fun getPlaceForId(id: Int): PlaceEntity

    @Query(value = "DELETE FROM list_places_table")
    suspend fun deleteAllPlaces()

    @Query(value = "SELECT images FROM list_places_table")
    suspend fun getImagePlaces(): List<String>
}