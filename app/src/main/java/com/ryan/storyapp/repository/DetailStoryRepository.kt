package com.ryan.storyapp.repository

import android.content.Context
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.data.model.StoriesDetailResponse
import com.ryan.storyapp.data.model.Story
import com.ryan.storyapp.data.remote.ApiConfig
import com.ryan.storyapp.utils.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryRepository(private val context: Context) {

    private val sharedPreferencesManager: SharedPreferencesManager = SharedPreferencesManager(context)
    private lateinit var apiKey: String

    fun fetchDetailStory(storyId: String, onResult: (ResultViewModel<Story>?) -> Unit) {
        getApiKey()
        val client = ApiConfig.getApiServiceWithAuth(apiKey).getDetailStories(storyId)
        client.enqueue(object : Callback<StoriesDetailResponse> {
            override fun onResponse(call: Call<StoriesDetailResponse>, response: Response<StoriesDetailResponse>) {
                if (response.isSuccessful) {
                    val detailStoriesResponse = response.body()
                    if (detailStoriesResponse?.story != null) {
                        val story = detailStoriesResponse.story
                        onResult(ResultViewModel.DetailSuccess(story))
                    } else {
                        onResult(ResultViewModel.Error(context.getString(R.string.storyNF)))
                    }
                } else {
                    onResult(ResultViewModel.Error("${context.getString(R.string.error)}: ${response.body()?.message}"))
                }
            }

            override fun onFailure(call: Call<StoriesDetailResponse>, t: Throwable) {
                onResult(ResultViewModel.Error("${context.getString(R.string.invalid)}: ${t.message}"))
            }
        })
    }

    private fun getApiKey() {
        apiKey = sharedPreferencesManager.getApiKey().toString()
    }
}