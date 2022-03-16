package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.Repository
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val repository: Repository,
    private val category: String
): ViewModel() {

    private val _loadingBar = MutableLiveData<Boolean>()
    val loadingBar: LiveData<Boolean> = _loadingBar

    private val _categoryList = MutableLiveData<List<Place>>()
    val categoryList: LiveData<List<Place>> = _categoryList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadDataFromRepository()
    }

    private fun loadDataFromRepository() {
        viewModelScope.launch {
            _loadingBar.value = true
            if (repository.getItemsCount() <= 0) {
                _error.value = repository.loadPlacesFromDataSourceRemote()
            }
            _categoryList.value = repository.getPlacesCategory(category.lowercase()).asDomainModel()
            _loadingBar.value = false
        }
    }

    fun clearError() {
        _error.value = ""
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