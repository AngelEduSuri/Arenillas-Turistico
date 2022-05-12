package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import kotlinx.coroutines.launch

class GalleryViewModel(private val repository: LocalRepository): ViewModel() {

    private val _loadingBar = MutableLiveData<Boolean>()
    val loadingBar: LiveData<Boolean> = _loadingBar

    private val _imageGalleryList = MutableLiveData<List<String>>()
    val imageGalleryList: LiveData<List<String>> = _imageGalleryList

    private val _navigateImageFullScreen = MutableLiveData<String>()
    val navigateImageFullScreen: LiveData<String> = _navigateImageFullScreen

    init {
        getGalleryPlaces()
    }

    private fun getGalleryPlaces() {
        viewModelScope.launch {
            _loadingBar.value = true
            _imageGalleryList.value = repository.getImagePlaces()
            _loadingBar.value = false
        }
    }

    fun onNavigateFullScreen(image: String) {
        _navigateImageFullScreen.value = image
    }

    fun onNavigateFullScreenDone() {
        _navigateImageFullScreen.value = ""
    }
}

@Suppress("UNCHECKED_CAST")
class GalleryViewModelFactory(private val repository: LocalRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}