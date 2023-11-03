package com.ryan.storyapp.ui.main.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.Stories

class PagingRepository(private val pagingDatabase: PagingDatabase, private val apiService: ApiPaging) {
    fun getQuote(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                PagingSourcer(apiService)
            }
        ).liveData
    }
}