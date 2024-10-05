package com.example.tft.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel : ViewModel() {
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _daysOfMonth = MutableStateFlow<List<LocalDate>>(emptyList())
    val daysOfMonth: StateFlow<List<LocalDate>> = _daysOfMonth.asStateFlow()

    private val _selectedDay = MutableStateFlow<LocalDate?>(null)
    val selectedDay: StateFlow<LocalDate?> = _selectedDay.asStateFlow()

    private val _eventDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val eventDates: StateFlow<List<LocalDate>> = _eventDates.asStateFlow()

    init {
        updateDaysOfMonth()
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        updateDaysOfMonth()
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        updateDaysOfMonth()
    }

    private fun updateDaysOfMonth() {
        val firstDayOfMonth = _currentMonth.value.atDay(1)
        val daysInMonth = _currentMonth.value.lengthOfMonth()
        val days = (0 until daysInMonth).map { firstDayOfMonth.plusDays(it.toLong()) }
        _daysOfMonth.value = days
    }

    fun selectDay(day: LocalDate) {
        _selectedDay.value = day
    }

    fun setEventDates(eventDates: List<LocalDate>) {
        _eventDates.value = eventDates
    }
}