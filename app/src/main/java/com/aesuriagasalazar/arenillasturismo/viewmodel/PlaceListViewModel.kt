package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class PlaceListViewModel(
    private val repository: Repository,
    private val category: String
): ViewModel() {

    private val _loadingBar = MutableLiveData<Boolean>()
    val loadingBar: LiveData<Boolean> = _loadingBar

    val places = repository.places

    init {
        loadDataFromRepository()
    }

    private fun loadDataFromRepository() {
        viewModelScope.launch {
            _loadingBar.value = true
            repository.getListPlaces()
            _loadingBar.value = false
        }
    }
}

class PlaceListViewModelFactory(
    private val repository: Repository,
    private val category: String
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceListViewModel::class.java)) {
            return PlaceListViewModel(repository, category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}