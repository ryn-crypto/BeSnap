package com.ryan.storyapp.ui.main.paging

import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.Stories
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiPaging {
//    @GET("list")
//    suspend fun getQuote(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): List<PagingResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Stories
}