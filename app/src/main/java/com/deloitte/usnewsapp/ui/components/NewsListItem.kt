package com.deloitte.usnewsapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.deloitte.usnewsapp.data.news.local.entity.ArticleEntity

@Composable
fun NewsListItem(article: ArticleEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, // Trigger onClick for navigation
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Increased padding
        ) {
            AsyncImage(
                model = article.urlToImage ?: "https://salonlfc.com/wp-content/uploads/2018/01/image-not-found-1-scaled-1150x647.png", // Use default image if null
                contentDescription = article.title,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop // Crop the image to fit
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Bold title
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Source: ${article.sourceName}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 2.dp) // Space below source
            )
            Text(
                text = "Published at: ${article.publishedAt}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
