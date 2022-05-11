package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RemoteRepository
import com.aesuriagasalazar.arenillasturismo.model.network.NetworkStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class DataStatus {
    UPDATING,
    DOWNLOADING,
    ERROR,
    NO_NETWORK,
    SYNCHRONIZED,
    LOCAL
}

class SplashViewModel(
    private val remoteRepository: RemoteRepository,
    private val network: NetworkStatus
) : ViewModel() {

    private val _dataStatus = MutableLiveData<DataStatus>()
    val dataStatus: LiveData<DataStatus> = _dataStatus

    private val _navigateMenu = MutableLiveData<Boolean>()
    val navigateMenu: LiveData<Boolean> = _navigateMenu

    init {
        checkNetworkOnDevice()
    }

    /** Funcion que comprueba si existe una conexion a internet **/
    private fun checkNetworkOnDevice() {
        viewModelScope.launch {
            if (network.isNetworkAvailable()) {
                loadDataFromRemote()
            } else {
                loadDataFromLocal()
            }
        }
    }

    /** Funcion que comprueba los datos remotos **/
    private suspend fun loadDataFromRemote() {
        val countData = remoteRepository.getItemsCountFromLocal()
        when {
            /** Datos locales son iguales a los datos remotos, entonces estan sincronizados**/
            countData == remoteRepository.getItemsCountFromFirebase() -> {
                _dataStatus.value = DataStatus.SYNCHRONIZED
                delay(1000)
                _navigateMenu.value = true
            }
            /** Datos locales son diferentes a datos remotos entonces se actualizan **/
            countData > 0 || countData != remoteRepository.getItemsCountFromFirebase() -> {
                _dataStatus.value = DataStatus.UPDATING
                val response = remoteRepository.updateLocalDataBaseFromRemoteDataSource()
                delay(500)
                if (response.isNullOrEmpty()) {
                    _navigateMenu.value = true
                } else {
                    _dataStatus.value = DataStatus.ERROR
                }
            }
            /** Datos locales son 0 (no existen), entonces se descarga los datos de Firebase **/
            countData <= 0 -> {
                _dataStatus.value = DataStatus.DOWNLOADING
                val response = remoteRepository.loadPlacesFromDataSourceRemote()
                delay(500)
                if (response.isNullOrEmpty()) {
                    _navigateMenu.value = true
                } else {
                    _dataStatus.value = DataStatus.ERROR
                }
            }
        }
    }

    /** Funcion que comprueba si existen datos locales **/
    private suspend fun loadDataFromLocal() {
        if (remoteRepository.getItemsCountFromLocal() <=0) {
            _dataStatus.value = DataStatus.NO_NETWORK
        } else {
            _dataStatus.value = DataStatus.LOCAL
            delay(2000)
            _navigateMenu.value = true
        }
    }

    /** Funcion que resetea el observable de navegacion cuando el usuaio se diriga el menu **/
    fun onNavigateMenuDone() {
        _navigateMenu.value = false
    }
}

@Suppress("UNCHECKED_CAST")
class SplashViewModelFactory(private val remoteRepository: RemoteRepository, private val network: NetworkStatus) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(remoteRepository, network) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
