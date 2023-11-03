package com.ryan.storyapp.ui.main.paging

import android.content.Context
import com.ryan.storyapp.BuildConfig
import com.ryan.storyapp.utils.SharedPreferencesManager

object Injection {
    fun provideRepository(context: Context): PagingRepository {
        val sharedPreferencesManager = SharedPreferencesManager(context)
        val apiKey = sharedPreferencesManager.getApiKey().toString()

        val database = PagingDatabase.getDatabase(context)
        val apiService = PagingApiConfig.getApiService(apiKey)
        return PagingRepository(database, apiService)
    }
}