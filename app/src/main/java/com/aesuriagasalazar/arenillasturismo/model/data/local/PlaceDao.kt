package com.aesuriagasalazar.arenillasturismo.model.data.local

import androidx.room.*

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
    suspend fun savePlaces(places: List<PlaceEntity>)

    @Query(value = "SELECT * FROM list_places_table WHERE id = :id")
    suspend fun getPlaceForId(id: Int): PlaceEntity

    @Update
    suspend fun updatePlaces(places: List<PlaceEntity>)
}