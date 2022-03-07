package com.aesuriagasalazar.arenillasturismo.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaceEntity::class], version = 1, exportSchema = false)
abstract class PlacesDatabase: RoomDatabase() {

    abstract val videoDao: PlaceDao

    companion object{

        @Volatile
        private lateinit var INSTANCE: PlacesDatabase

        fun getDatabase(context: Context): PlacesDatabase {
            synchronized(PlacesDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        PlacesDatabase::class.java,
                        "places").build()
                }
            }
            return INSTANCE
        }
    }
}

