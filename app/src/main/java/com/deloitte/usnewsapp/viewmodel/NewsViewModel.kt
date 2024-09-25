package com.deloitte.usnewsapp.viewmodel

import com.deloitte.usnewsapp.data.model.Article
import com.deloitte.usnewsapp.data.repository.NewsRepository
import com.deloitte.usnewsapp.util.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _newsFlow = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val newsFlow: StateFlow<Resource<List<Article>>> get() = _newsFlow

    init {
        fetchTopHeadlines() // Fetch top headlines from the US by default
    }

    fun fetchTopHeadlines(country: String = "us") {
        viewModelScope.launch {
            _newsFlow.value = Resource.Loading()
            try {
                val response = repository.getTopHeadlines("41133f412b244374a7010800a28c1dac", country)
                _newsFlow.value = Resource.Success(response.articles)
            } catch (e: Exception) {
                _newsFlow.value = Resource.Error(e.message ?: "Error fetching news")
            }
        }
    }

    fun fetchCategoryHeadlines(category: String) {
        viewModelScope.launch {
            _newsFlow.value = Resource.Loading()
            try {
                val response = repository.getCategoryHeadlines(category, "41133f412b244374a7010800a28c1dac")
                _newsFlow.value = Resource.Success(response.articles)
            } catch (e: Exception) {
                _newsFlow.value = Resource.Error(e.message ?: "Error fetching news")
            }
        }
    }

    fun getArticleById(articleId: String): Article? {
        return _newsFlow.value.data?.find { it.url == articleId }
    }
}

