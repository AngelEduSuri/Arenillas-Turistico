package com.aesuriagasalazar.arenillasturismo.model.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalRepository(private val localDataBase: PlaceDao) {

    /** Funcion de suspension que obtiene los lugares por categoria
     * @param category String que llega desde el ViewModel
     **/
    suspend fun getPlacesCategory(category: String) = withContext(Dispatchers.IO) {
        localDataBase.getPlacesForCategory(category)
    }

    suspend fun getAllPlaces() = withContext(Dispatchers.IO) {
        localDataBase.getPlaces()
    }

    suspend fun getPlaceForId(placeId: Int) = withContext(Dispatchers.IO) {
        localDataBase.getPlaceForId(placeId)
    }
}