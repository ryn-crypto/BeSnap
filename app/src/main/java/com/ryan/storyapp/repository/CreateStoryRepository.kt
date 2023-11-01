package com.ryan.storyapp.repository

import android.content.Context
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.CreateStoryResponse
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.data.remote.ApiConfig
import com.ryan.storyapp.utils.SharedPreferencesManager
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateStoryRepository(private val context: Context) {

    private val sharedPreferencesManager: SharedPreferencesManager = SharedPreferencesManager(context)
    private lateinit var apiKey: String

    fun createStory(
        description: RequestBody,
        imageUri: MultipartBody.Part,
        onResult: (ResultViewModel<CreateStoryResponse>?) -> Unit
    ) {
        getApiKey()
        val apiService = ApiConfig.getApiServiceWithAuth(apiKey)
        val call = apiService.createStory(description, imageUri)

        call.enqueue(object : Callback<CreateStoryResponse> {
            override fun onResponse(
                call: Call<CreateStoryResponse>,
                response: Response<CreateStoryResponse>
            ) {
                val result = if (response.isSuccessful) {
                    response.body()?.let { ResultViewModel.Success(it) }
                } else {
                    val errorText =
                        "${context.getString(R.string.error)} : ${response.body()?.message}"
                    ResultViewModel.Error(errorText)
                }

                onResult(result)
            }

            override fun onFailure(call: Call<CreateStoryResponse>, t: Throwable) {
                val errorMessage = "${context.getString(R.string.invalid)}: ${t.message}"
                onResult(ResultViewModel.Error(errorMessage))
            }
        })
    }

    fun createStoryWithLocation(
        description: RequestBody,
        imageUri: MultipartBody.Part,
        longitude: RequestBody,
        latitude: RequestBody,
        onResult: (ResultViewModel<CreateStoryResponse>?) -> Unit
    ) {
        getApiKey()
        val apiService = ApiConfig.getApiServiceWithAuth(apiKey)
        val call = apiService.createStoryLocation(description, imageUri, longitude, latitude)

        call.enqueue(object : Callback<CreateStoryResponse> {
            override fun onResponse(
                call: Call<CreateStoryResponse>,
                response: Response<CreateStoryResponse>
            ) {
                val result = if (response.isSuccessful) {
                    response.body()?.let { ResultViewModel.Success(it) }
                } else {
                    val errorText =
                        "${context.getString(R.string.error)} : ${response.body()?.message}"
                    ResultViewModel.Error(errorText)
                }

                onResult(result)
            }

            override fun onFailure(call: Call<CreateStoryResponse>, t: Throwable) {
                val errorMessage = "${context.getString(R.string.invalid)}: ${t.message}"
                onResult(ResultViewModel.Error(errorMessage))
            }
        })
    }

    private fun getApiKey() {
        apiKey = sharedPreferencesManager.getApiKey().toString()
    }
}