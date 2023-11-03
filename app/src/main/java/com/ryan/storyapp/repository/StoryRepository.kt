package com.ryan.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.local.PagingDatabase
import com.ryan.storyapp.data.remote.ApiService
import com.ryan.storyapp.data.remote.StoryPagingSource

class StoryRepository(private val pagingDatabase: PagingDatabase, private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }
}