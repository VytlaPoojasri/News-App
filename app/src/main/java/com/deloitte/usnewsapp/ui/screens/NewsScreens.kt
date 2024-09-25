package com.deloitte.usnewsapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.deloitte.usnewsapp.ui.components.NewsListItem
import com.deloitte.usnewsapp.util.Resource
import com.deloitte.usnewsapp.viewmodel.NewsViewModel


@Composable
fun NewsScreen(navController: NavController, viewModel: NewsViewModel, category: String) {
    val newsState = viewModel.newsFlow.collectAsState()

    // Fetch news for the selected category
    LaunchedEffect(category) {
        if (category == "Top") {
            viewModel.fetchTopHeadlines() // Fetch top headlines when category is "Top"
        } else {
            viewModel.fetchCategoryHeadlines(category) // Fetch category headlines
        }
    }

    when (val state = newsState.value) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
        }
        is Resource.Success -> {
            val articles = state.data ?: emptyList()
            LazyColumn {
                items(articles) { article ->
                    NewsListItem(article = article) {
                        val encodedUrl = URLEncoder.encode(article.url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detailed_news_screen/$encodedUrl")
                    }
                }
            }

        }
        is Resource.Error -> {
            Text(text = "Error: ${state.message}")
        }

    }
}

