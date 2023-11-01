package com.ryan.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.repository.StoryRepository

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _result = MutableLiveData<ResultViewModel<List<ListStoryItem>>>()
    val result: LiveData<ResultViewModel<List<ListStoryItem>>> = _result

    fun fetchStories() {
        repository.fetchStories { result ->
            _result.value = result
        }
    }

    fun isLogin(): Boolean {
        return repository.isLogin()
    }
}

class StoryViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}