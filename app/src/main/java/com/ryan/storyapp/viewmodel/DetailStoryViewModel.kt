package com.ryan.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.data.model.Story
import com.ryan.storyapp.repository.DetailStoryRepository
import com.ryan.storyapp.repository.LoginRepository

class DetailStoryViewModel(private val repository: DetailStoryRepository) : ViewModel() {

    private val _detailStory = MutableLiveData<ResultViewModel<Story>>()
    val detailStory: LiveData<ResultViewModel<Story>?> = _detailStory

    fun fetchDetailStory(storyId: String) {
        repository.fetchDetailStory(storyId) { result ->
            _detailStory.postValue(result)
        }
    }
}

class DetailStoryViewModelFactory(private val repository: DetailStoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            return DetailStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}