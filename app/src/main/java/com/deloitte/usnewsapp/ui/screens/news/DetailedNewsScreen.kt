package com.deloitte.usnewsapp.ui.screens.news


import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deloitte.usnewsapp.viewmodel.NewsViewModel

@Composable
fun DetailedNewsScreen(articleUrl: String, viewModel: NewsViewModel,navController: NavController) {
    val article = viewModel.getArticleById(articleUrl)
    val customColor = Color(0xFF6495ED)


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        if (article != null) {
            AsyncImage(
                model = article.urlToImage ?: "https://salonlfc.com/wp-content/uploads/2018/01/image-not-found-1-scaled-1150x647.png",
                contentDescription = article.title,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop // Crop the image to fit
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), // Bold title
                modifier = Modifier.padding(bottom = 8.dp) // Space below title
            )
            Text(
                text = "Source: ${article.sourceName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp) // Space below source
            )
            Text(
                text = "Published At: ${article.publishedAt}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Description",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = article.description ?: "No description available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = article.content ?: "No content available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    val encodedUrl = Uri.encode(article.url)
                    navController.navigate("webview_screen/$encodedUrl")
                }, modifier = Modifier.padding(top = 16.dp),

            ) {
                Text(text = "See More")
            }

        } else {
            Text(text = "Article not found.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
