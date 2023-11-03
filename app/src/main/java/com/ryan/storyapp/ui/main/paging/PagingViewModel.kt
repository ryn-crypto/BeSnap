package com.ryan.storyapp.ui.main.paging

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.Stories

class PagingViewModel(quoteRepository: PagingRepository) : ViewModel() {

    val quote: LiveData<PagingData<ListStoryItem>> =
        quoteRepository.getQuote().cachedIn(viewModelScope)

}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PagingViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}