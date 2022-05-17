package com.aesuriagasalazar.arenillasturismo.model.data.remote

import com.aesuriagasalazar.arenillasturismo.model.data.local.PlaceDao
import com.aesuriagasalazar.arenillasturismo.model.data.local.asEntityModelList
import com.aesuriagasalazar.arenillasturismo.viewmodel.MessageBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Clase repositorio para obtener los datos de la base de datos remota y almacenarlo en local
 * @param realTimeDataBase Obtiene la base de datos Firebase
 * @param localDataBase Obtiene la base de datos Room
 */
class RemoteRepository(
    private val realTimeDataBase: RealTimeDataBase?,
    private val localDataBase: PlaceDao?,
    private val firestoreDataBase: FirestoreDataBase?
) {

    /** Funcion de suspension que consulta si la base de datos Room contiene informacion **/
    suspend fun getItemsCountFromLocal() = localDataBase?.getListCount()

    /** Funcion de suspension que obtiene la lista de Firebase y se guarda en local **/
    suspend fun loadPlacesFromDataSourceRemote(): String? {
        /** Se ejecuta en una corrutina en otro hilo Dispatchers.IO **/
        val error = withContext(Dispatchers.IO) {
            val response = realTimeDataBase?.getDataFromFirebaseOnCoroutine()
            response?.let {
                localDataBase?.savePlaces(response.places.asEntityModelList())
                return@withContext response.error
            }
        }
        return error
    }

    /** Funcion de suspension que borra la base local y la actualiza con los datos remotos **/
    suspend fun updateLocalDataBaseFromRemoteDataSource(): String? {
        val error = withContext(Dispatchers.IO) {
            localDataBase?.deleteAllPlaces()
            val response = realTimeDataBase?.getDataFromFirebaseOnCoroutine()
            response?.let {
                localDataBase?.savePlaces(response.places.asEntityModelList())
                return@withContext response.error
            }
        }
        return error
    }

    /** Funcion de suspension que obtiene la cantidad de lugares guardados en remoto**/
    suspend fun getItemsCountFromFirebase() = withContext(Dispatchers.IO) {
        return@withContext realTimeDataBase?.getChildCountFromFirebaseOnCoroutine()
    }

    /** Funcion de suspension que obtiene la respuesta del envio del mensaje de contaco a firestore **/
    suspend fun sendMessageFromUser(message: MessageBody) = withContext(Dispatchers.IO) {
        return@withContext firestoreDataBase?.setMessageOnFirestore(message)
    }
}
