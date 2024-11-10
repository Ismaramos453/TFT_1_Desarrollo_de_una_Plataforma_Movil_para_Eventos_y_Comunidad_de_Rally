package com.example.tft.data.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



//54d5717ca0mshe03649c5e5a404dp16a9cbjsn5bc3b11b9a96
//
//6b26eda071msh9f68a58e9d3b0b9p19e3e0jsnead88f3bbb4e
object RetrofitInstance {
    private const val BASE_URL = "https://motorsportapi.p.rapidapi.com/api/motorsport/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", "8259fd0dadmsh8b51dfecb9e47bbp14cdb7jsnea114e16d7d7")
                .addHeader("x-rapidapi-host", "motorsportapi.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: WrcApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WrcApiService::class.java)
    }
}



