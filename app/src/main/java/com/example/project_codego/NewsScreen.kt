package com.example.project_codego

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_codego.ui.theme.ProjectcodegoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    onBackClick: () -> Unit,
    viewModel: NewsViewModel = viewModel()
) {
    val newsState by viewModel.newsState
    val PrimaryBlue = Color(0xFF0088CC)
    val BackgroundGray = Color(0xFFF0F2F5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Latest News",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = newsState) {
                is NewsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NewsState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.articles) { article ->
                            NewsItemCard(article)
                        }
                    }
                }
                is NewsState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewsItemCard(article: Article) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            article.image?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Text(
                text = article.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            article.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.source.name,
                    fontSize = 12.sp,
                    color = Color(0xFF0088CC),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = article.publishedAt.take(10), // Simple date formatting
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    ProjectcodegoTheme {
        NewsScreen(onBackClick = {})
    }
}
