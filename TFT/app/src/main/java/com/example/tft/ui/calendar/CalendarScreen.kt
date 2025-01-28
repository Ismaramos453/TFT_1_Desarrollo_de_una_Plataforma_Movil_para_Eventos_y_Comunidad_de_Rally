package com.example.tft.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.example.tft.model.SavedEvent
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.events.EventViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(navController: NavHostController, viewModel: CalendarViewModel = viewModel(), eventViewModel: EventViewModel = viewModel()) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val daysOfMonth by viewModel.daysOfMonth.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val eventDates by eventViewModel.eventDates.collectAsState()
    val eventTypes by eventViewModel.eventTypes.observeAsState(emptyMap())
    val user = AuthenticationServices.getCurrentUser()

    LaunchedEffect(user) {
        user?.uid?.let { userId ->
            eventViewModel.loadSavedEvents(userId)
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Calendario", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarHeader(
                    currentMonth = currentMonth,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                CalendarView(
                    daysOfMonth = daysOfMonth,
                    currentMonth = currentMonth,
                    selectedDay = selectedDay,
                    eventDates = eventDates,
                    eventTypes = eventTypes,
                    onDaySelected = { day ->
                        viewModel.selectDay(day)
                    }
                )
                selectedDay?.let { day ->
                    val eventsOnDay = eventViewModel.getEventsByDate(day)
                    if (eventsOnDay.isNotEmpty()) {
                        LazyColumn {
                            items(eventsOnDay) { event ->
                                EventItem(event, navController)
                            }
                        }
                    } else {
                        Text("No hay eventos para este día")
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(event: SavedEvent, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(AppScreens.EventDetailScreen.route + "/${event.id}")
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Text(text = event.title)
                Text(text = "${event.date}, ${event.time}")
                Text(text = event.location)
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(currentMonth: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    // Fila para organizar los elementos horizontalmente
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Mes Anterior")
        }
        // Mostrar el mes y el año actuales
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Mes Siguiente")
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    daysOfMonth: List<LocalDate>,
    currentMonth: YearMonth,
    selectedDay: LocalDate?,
    eventDates: List<LocalDate>,
    onDaySelected: (LocalDate) -> Unit,
    eventTypes: Map<LocalDate, String>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        val daysOfWeek = listOf("L", "M", "M", "J", "V", "S", "D")
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        val firstDayOfMonth = currentMonth.atDay(1)
        val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)
        val firstDayIndex = (firstDayOfMonth.get(weekFields.dayOfWeek()) - 1) % 7

        val days = mutableListOf<LocalDate?>()
        for (i in 0 until firstDayIndex) {
            days.add(null)
        }
        days.addAll(daysOfMonth)

        while (days.size % 7 != 0) {
            days.add(null)
        }

        days.chunked(7).forEach { week ->
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                week.forEach { day ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = when {
                                    day == null -> Color.Transparent
                                    eventTypes[day] == "WRC Event" -> Color(0xFF2196F3) // Azul para WRC
                                    eventTypes[day] == "ERC Event" -> Color(0xFF4CAF50) // Verde para ERC
                                    eventTypes[day] == "Amateur" -> Color(0xFF46208B) // Morado para Amateur
                                    day == selectedDay -> Color(0xFFA32902) // Rojo día seleccionado
                                    else -> Color(0xFFF58A26) // Naranja días normales
                                },
                                shape = CircleShape
                            )
                            .clickable(enabled = day != null) {
                                day?.let { onDaySelected(it) }
                            }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = day?.dayOfMonth?.toString() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (day != null) Color.Black else Color.Transparent
                        )
                    }
                }
            }
        }

        // Leyenda mostrada en una sola fila, con los ítems separados como columnas
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            LegendItem(Color(0xFF2196F3), "Evento WRC")
            LegendItem(Color(0xFF4CAF50), "Evento ERC")
            LegendItem(Color(0xFF46208B), "Amateur")
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
