package com.ryan.storyapp.di

import android.content.Context
import com.ryan.storyapp.data.local.PagingDatabase
import com.ryan.storyapp.data.remote.ApiConfig
import com.ryan.storyapp.repository.StoryRepository
import com.ryan.storyapp.utils.SharedPreferencesManager

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val sharedPreferencesManager = SharedPreferencesManager(context)
        val apiKey = sharedPreferencesManager.getApiKey().toString()

        val database = PagingDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiServiceWithAuth(apiKey)
        return StoryRepository(database, apiService)
    }
}