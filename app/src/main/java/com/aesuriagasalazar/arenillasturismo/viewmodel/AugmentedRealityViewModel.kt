package com.aesuriagasalazar.arenillasturismo.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.location.UserLocationOnLiveData
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModel
import kotlinx.coroutines.launch

class AugmentedRealityViewModel(private val repository: LocalRepository, application: Application) :
    ViewModel() {
    val userLocation = UserLocationOnLiveData(application)

    private val _categoryList = MutableLiveData<List<Place>>()
    val categoryList: LiveData<List<Place>> = _categoryList

    init {
        loadDataFromRepository()
    }

    private fun loadDataFromRepository() {
        viewModelScope.launch {
            _categoryList.value = repository.getAllPlaces().asDomainModel()
        }
    }
}

class AugmentedRealityViewModelFactory(
    private val repository: LocalRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AugmentedRealityViewModel::class.java)) {
            return AugmentedRealityViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

