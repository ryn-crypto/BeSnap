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

class StoryRepository(private val context: Context) {

    private val sharedPreferencesManager: SharedPreferencesManager = SharedPreferencesManager(context)
    private lateinit var apiKey: String

    fun fetchStories(onResult: (ResultViewModel<List<ListStoryItem>>) -> Unit) {
        getApiKey()
        if (apiKey != null) {
            val client = ApiConfig.getApiServiceWithAuth(apiKey).getStories(1, 50, 0)
            client.enqueue(object : Callback<Stories> {
                override fun onResponse(
                    call: Call<Stories>,
                    response: Response<Stories>
                ) {
                    if (response.isSuccessful) {
                        val storiesResponse = response.body()
                        val storyItems = storiesResponse?.listStory.orEmpty().filterNotNull()
                        onResult(ResultViewModel.Success(storyItems))
                    } else {
                        onResult(
                            ResultViewModel.Error(
                                "${context.getString(R.string.error)}: ${response.body()?.message}"
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<Stories>, t: Throwable) {
                    onResult(ResultViewModel.Error("${context.getString(R.string.invalid)}: ${t.message}"))
                }
            })
        } else {
            onResult(ResultViewModel.Error(context.getString(R.string.noAvailable)))
        }
    }

    fun isLogin(): Boolean {
        getApiKey()
        return apiKey.length >= 5
    }

    private fun getApiKey() {
        apiKey = sharedPreferencesManager.getApiKey().toString()
    }
}