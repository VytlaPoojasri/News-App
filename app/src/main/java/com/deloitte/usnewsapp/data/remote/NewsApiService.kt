package com.deloitte.usnewsapp.data.remote

import com.deloitte.usnewsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String
    ): NewsResponse

    @GET("top-headlines")
    suspend fun getCategoryHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}