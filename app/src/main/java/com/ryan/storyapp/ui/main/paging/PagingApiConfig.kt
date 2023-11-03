package com.ryan.storyapp.ui.main.paging

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PagingApiConfig {
    companion object {
        fun getApiService(token: String): ApiPaging {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val authorizationInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder: Request.Builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authorizationInterceptor) // Menambahkan interceptor baru
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiPaging::class.java)
        }
    }
}