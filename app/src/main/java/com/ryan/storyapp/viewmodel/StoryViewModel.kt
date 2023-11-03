package com.ryan.storyapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.repository.StoryRepository
import com.ryan.storyapp.di.Injection

class StoryViewModel(repository: StoryRepository) : ViewModel() {

    val result: LiveData<PagingData<ListStoryItem>> =
        repository.getStory().cachedIn(viewModelScope)


}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}