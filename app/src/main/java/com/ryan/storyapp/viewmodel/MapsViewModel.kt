package com.ryan.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.repository.MapsRepository

class MapsViewModel(private val repository: MapsRepository) : ViewModel() {

    private val _result = MutableLiveData<ResultViewModel<List<ListStoryItem>>>()
    val result: LiveData<ResultViewModel<List<ListStoryItem>>> = _result

    fun fetchData() {
        repository.fetchMapStories { result ->
            _result.value = result
        }
    }
}

class MapsViewModelFactory(private val repository: MapsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}