package com.deloitte.usnewsapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deloitte.usnewsapp.data.remote.NewsApiService
import com.deloitte.usnewsapp.data.repository.NewsRepository

class NewsViewModelFactory(
    private val apiService: NewsApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            val repository = NewsRepository(apiService, context)
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
