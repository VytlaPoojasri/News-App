package com.deloitte.usnewsapp.data.news.repository

import android.content.Context
import com.deloitte.usnewsapp.data.news.local.db.AppDatabase
import com.deloitte.usnewsapp.data.news.local.entity.ArticleEntity
import com.deloitte.usnewsapp.data.news.remote.NewsApiService
import com.deloitte.usnewsapp.util.NetworkUtils

class NewsRepository(private val apiService: NewsApiService, private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val articleDao = db.articleDao()

    suspend fun getTopHeadlines(apiKey: String, country: String = "us"): List<ArticleEntity> {
        return getArticlesByCategory("Top", apiKey, country)
    }

    suspend fun getCategoryHeadlines(category: String, apiKey: String, country: String = "us"): List<ArticleEntity> {
        return getArticlesByCategory(category, apiKey, country)
    }

    private suspend fun getArticlesByCategory(category: String, apiKey: String, country: String): List<ArticleEntity> {
        return if (NetworkUtils.isNetworkAvailable(context)) {
            val response = if (category == "Top") {
                apiService.getTopHeadlines(country = country, apiKey = apiKey)
            } else {
                apiService.getCategoryHeadlines(category = category, apiKey = apiKey, country = country)
            }
            val articles = response.articles.map { article ->
                ArticleEntity(
                    url = article.url,
                    title = article.title,
                    description = article.description,
                    urlToImage = article.urlToImage,
                    publishedAt = article.publishedAt,
                    content = article.content,
                    sourceName = article.source.name,
                    category = category // Set the category
                )
            }
            articleDao.deleteArticlesByCategory(category) //  For Deleting old articles
            articleDao.insertArticles(articles) // For Inserting new articles
            articles
        } else {
            articleDao.getArticlesByCategory(category)         // For fetching articles from localDB
        }
    }
}
