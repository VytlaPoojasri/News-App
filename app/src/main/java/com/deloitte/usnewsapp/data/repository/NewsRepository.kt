package com.deloitte.usnewsapp.data.repository

import com.deloitte.usnewsapp.data.model.NewsResponse
import com.deloitte.usnewsapp.data.remote.NewsApiService

class NewsRepository(private val apiService: NewsApiService) {

    suspend fun getTopHeadlines(apiKey: String, country: String = "us"): NewsResponse {
        return apiService.getTopHeadlines(country = country, apiKey = apiKey)
    }

    suspend fun getCategoryHeadlines(category: String, apiKey: String, country: String = "us"): NewsResponse {
        return apiService.getCategoryHeadlines(category = category, apiKey = apiKey, country = country)
    }
}