package com.example.tft.ui.newsDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.news.NewsViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(navController: NavHostController, newsId: String) {
    val newsDetailViewModel: NewsDetailViewModel = viewModel()
    val newsDetail by newsDetailViewModel.newsDetail.observeAsState()

    // Cargar los detalles de la noticia al entrar a la pantalla
    LaunchedEffect(newsId) {
        newsDetailViewModel.loadNewsDetail(newsId)
    }

    Scaffold(
        topBar = {
            // Utilizar el título de la noticia si está disponible
            BackTopBar(title = "Detalle de la Noticia", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            newsDetail?.let { news ->
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    item {
                        if (news.image.isNotEmpty()) {
                            Image(
                                painter = rememberImagePainter(news.image),
                                contentDescription = "Imagen Detallada de Noticia",
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        // Ajustar el texto por la izquierda y la derecha
                        Text(
                            text = news.description,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            textAlign = TextAlign.Justify // Alineación justificada
                        )
                    }
                }
            } ?: Text(
                "Cargando detalles de la noticia...",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}
