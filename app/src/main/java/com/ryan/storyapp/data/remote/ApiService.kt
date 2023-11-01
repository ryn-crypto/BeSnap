package com.ryan.storyapp.data.remote

import com.ryan.storyapp.data.model.LoginRequest
import com.ryan.storyapp.data.model.RegisterRequest
import com.ryan.storyapp.data.model.AuthLoginResponse
import com.ryan.storyapp.data.model.AuthRegisterResponse
import com.ryan.storyapp.data.model.CreateStoryResponse
import com.ryan.storyapp.data.model.Stories
import com.ryan.storyapp.data.model.StoriesDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("stories")
    fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): Call<Stories>

    @GET("stories/{id}")
    fun getDetailStories(
        @Path("id") id: String
    ): Call<StoriesDetailResponse>


    @POST("register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<AuthRegisterResponse>

    @POST("login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<AuthLoginResponse>

    @Multipart
    @POST("stories")
    fun createStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<CreateStoryResponse>

    @Multipart
    @POST("stories")
    fun createStoryLocation(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lon") lon: RequestBody,
        @Part("lat") lat: RequestBody
    ): Call<CreateStoryResponse>

    @GET("stories")
    fun getMapsStory(
        @Query("location") location: Int
    ): Call<Stories>
}