package com.aesuriagasalazar.arenillasturismo.model.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaceDao {

    @Query(value = "SELECT * FROM list_places_table")
    fun getPlaces(): LiveData<List<PlaceEntity>>

    @Query(value = "SELECT * FROM list_places_table WHERE category = :category")
    suspend fun getPlacesForCategory(category: String): List<PlaceEntity>

    @Query(value = "SELECT COUNT(*) FROM list_places_table")
    suspend fun getListCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaces( videos: List<PlaceEntity>)
}