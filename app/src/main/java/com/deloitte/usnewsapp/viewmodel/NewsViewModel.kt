package com.deloitte.usnewsapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deloitte.usnewsapp.data.local.entity.ArticleEntity
import com.deloitte.usnewsapp.data.repository.NewsRepository
import com.deloitte.usnewsapp.util.NetworkUtils
import com.deloitte.usnewsapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository, private val context: Context) : ViewModel() {
    private val _newsFlow = MutableStateFlow<Resource<List<ArticleEntity>>>(Resource.Loading())
    val newsFlow: StateFlow<Resource<List<ArticleEntity>>> get() = _newsFlow

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    init {
        fetchTopHeadlines()
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        val networkUtils = NetworkUtils(context)
        networkUtils.observeForever { isConnected ->
            _networkStatus.postValue(isConnected)
            if (isConnected) {
                refreshAllNews()
            }
        }
    }

    fun fetchTopHeadlines(country: String = "us") {
        viewModelScope.launch {
            _newsFlow.value = Resource.Loading()
            try {
                val articles = repository.getTopHeadlines("41133f412b244374a7010800a28c1dac", country)
                _newsFlow.value = Resource.Success(articles)
            } catch (e: Exception) {
                _newsFlow.value = Resource.Error(e.message ?: "Error fetching news")
            }
        }
    }

    fun fetchCategoryHeadlines(category: String) {
        viewModelScope.launch {
            _newsFlow.value = Resource.Loading()
            try {
                val articles = repository.getCategoryHeadlines(category, "41133f412b244374a7010800a28c1dac")
                _newsFlow.value = Resource.Success(articles)
            } catch (e: Exception) {
                _newsFlow.value = Resource.Error(e.message ?: "Error fetching news")
            }
        }
    }

    fun getArticleById(articleId: String): ArticleEntity? {
        return _newsFlow.value.data?.find { it.url == articleId }
    }

    fun refreshAllNews() {
        val categories = listOf("Top", "Business", "Entertainment", "Health", "Science", "Sports", "Technology")
        categories.forEach { category ->
            if (category == "Top") {
                fetchTopHeadlines()
            } else {
                fetchCategoryHeadlines(category)
            }
        }
    }
}
