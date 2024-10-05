package com.example.tft.data.api.youtube

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/"

object YouTubeApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl(YOUTUBE_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: YouTubeApiService = retrofit.create(YouTubeApiService::class.java)
}
