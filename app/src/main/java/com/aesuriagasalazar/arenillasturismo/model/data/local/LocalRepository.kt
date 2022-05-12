package com.aesuriagasalazar.arenillasturismo.model.data.local

import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModelList
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

    suspend fun getImagePlaces() = withContext(Dispatchers.IO) {
        val imageList = mutableListOf<String>()
        val placeList = localDataBase.getPlaces().asDomainModelList()
        placeList.forEach {
            imageList.add(it.miniatura)
            it.imagenes.forEach { image ->
                imageList.add(image)
            }
        }
        return@withContext imageList
    }
}