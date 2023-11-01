package com.ryan.storyapp.repository

import android.content.Context
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.AuthLoginResponse
import com.ryan.storyapp.data.remote.ApiConfig
import com.ryan.storyapp.data.model.LoginRequest
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.utils.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val context: Context) {

    private val apiService = ApiConfig.getApiService()
    private val sharedPreferencesManager: SharedPreferencesManager = SharedPreferencesManager(context)

    fun loginUser(
        email: String,
        password: String,
        onResult: (ResultViewModel<AuthLoginResponse>?) -> Unit
    ) {
        val request = LoginRequest(email, password)

        val call = apiService.loginUser(request)

        call.enqueue(object : Callback<AuthLoginResponse> {
            override fun onResponse(call: Call<AuthLoginResponse>, response: Response<AuthLoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        onResult(ResultViewModel.Success(responseBody))
                    } ?: run {
                        val errorMessage = "${context.getString(R.string.invalid)}: ${R.string.bodyN}"
                        onResult(ResultViewModel.Error(errorMessage))
                    }
                } else if (response.code() == 401) {
                    val errorMessage = "${context.getString(R.string.invalid)}: ${context.getString(R.string.auth_error)}"
                    onResult(ResultViewModel.Error(errorMessage))
                } else {
                    val errorMessage = "${context.getString(R.string.error)}: ${response.body()?.message}"
                    onResult(ResultViewModel.Error(errorMessage))
                }
            }

            override fun onFailure(call: Call<AuthLoginResponse>, t: Throwable) {
                val errorMessage = "${context.getString(R.string.invalid)}: ${t.message}"
                onResult(ResultViewModel.Error(errorMessage))
            }
        })
    }

    fun saveApiKey(apiKey: String) {
        sharedPreferencesManager.saveApiKey(apiKey)
    }
}
