package com.example.project_codego

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _newsState = mutableStateOf<NewsState>(NewsState.Loading)
    val newsState: State<NewsState> = _newsState

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    private val apiService = NewsApiService.create()
    private val apiKey = BuildConfig.NEWS_API_KEY

    init {
        fetchNews()
    }

    fun refreshNews() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                val query = "accident OR safety OR emergency OR disaster OR rescue OR \"safety tips\""
                val response = apiService.searchNews(query = query, apiKey = apiKey)
                _newsState.value = NewsState.Success(response.articles)
            } catch (e: Exception) {
                _newsState.value = NewsState.Error("Failed to fetch news: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun fetchNews() {
        viewModelScope.launch {
            try {
                _newsState.value = NewsState.Loading
                // Search query for accidents, safety tips, emergency, etc.
                // Using OR operator to broaden search
                val query = "accident OR safety OR emergency OR disaster OR rescue OR \"safety tips\""
                val response = apiService.searchNews(query = query, apiKey = apiKey)
                _newsState.value = NewsState.Success(response.articles)
            } catch (e: Exception) {
                _newsState.value = NewsState.Error("Failed to fetch news: ${e.message}")
            }
        }
    }
}

sealed class NewsState {
    object Loading : NewsState()
    data class Success(val articles: List<Article>) : NewsState()
    data class Error(val message: String) : NewsState()
}
