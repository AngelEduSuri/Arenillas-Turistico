package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModelList
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val repository: LocalRepository
) : ViewModel() {

    private val _loadingBar = MutableLiveData<Boolean>()
    val loadingBar: LiveData<Boolean> = _loadingBar

    private val _categoryList = MutableLiveData<List<Place>>()
    val categoryList: LiveData<List<Place>> = _categoryList

    fun loadDataFromRepository(category: String) {
        viewModelScope.launch {
            _loadingBar.value = true
            _categoryList.value =
                repository.getPlacesCategory(category.lowercase()).asDomainModelList()
            _loadingBar.value = false
        }
    }
}

@Suppress("UNCHECKED_CAST")
class PlaceListViewModelFactory(
    private val repository: LocalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceListViewModel::class.java)) {
            return PlaceListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}