package com.example.project_codego

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("country") country: String = "ph",
        @Query("lang") lang: String = "en",
        @Query("apikey") apiKey: String
    ): NewsResponse

    companion object {
        private const val BASE_URL = "https://gnews.io/api/v4/"

        fun create(): NewsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApiService::class.java)
        }
    }
}
