package com.example.tft.data.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.request.CachePolicy

@Composable
fun rememberImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember {
        ImageLoader.Builder(context)
            .okHttpClient {
                RetrofitInstance.client
            }
            .diskCachePolicy(CachePolicy.ENABLED) // Habilita la caché de disco
            .memoryCachePolicy(CachePolicy.ENABLED) // Habilita la caché en memoria
            .build()
    }
}