package com.deloitte.usnewsapp.data.news.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)
