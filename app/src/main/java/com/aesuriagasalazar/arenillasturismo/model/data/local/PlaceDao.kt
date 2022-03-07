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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePlaces( videos: List<PlaceEntity>)
}