package com.ryan.storyapp.repository

import android.content.Context
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.AuthRegisterResponse
import com.ryan.storyapp.data.model.RegisterRequest
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.data.remote.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(private val context: Context) {

    private val apiService = ApiConfig.getApiService()

    fun registerUser(name: String, email: String, password: String, onResult: (ResultViewModel<AuthRegisterResponse>?) -> Unit) {
        val request = RegisterRequest(name, email, password)
        val call = apiService.registerUser(request)

        call.enqueue(object : Callback<AuthRegisterResponse> {
            override fun onResponse(call: Call<AuthRegisterResponse>, response: Response<AuthRegisterResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        onResult(ResultViewModel.Success(responseBody))
                    } ?: run {
                        val errorMessage = "${context.getString(R.string.invalid)}: ${context.getString(R.string.bodyN)}"
                        onResult(ResultViewModel.Error(errorMessage))
                    }
                } else {
                    val errorMessage = context.getString(R.string.regis_error)
                    onResult(ResultViewModel.Error(errorMessage))
                }
            }

            override fun onFailure(call: Call<AuthRegisterResponse>, t: Throwable) {
                val errorMessage = "${context.getString(R.string.invalid)}: ${t.message}"
                onResult(ResultViewModel.Error(errorMessage))
            }
        })
    }
}