package com.ryan.storyapp.repository

import android.content.Context
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.data.model.Stories
import com.ryan.storyapp.data.remote.ApiConfig
import com.ryan.storyapp.utils.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityRepository(private val context: Context) {

    private val sharedPreferencesManager: SharedPreferencesManager = SharedPreferencesManager(context)
    private lateinit var apiKey: String
    fun isLogin(): Boolean {
        getApiKey()
        return apiKey.length >= 5
    }

    private fun getApiKey() {
        apiKey = sharedPreferencesManager.getApiKey().toString()
    }
}