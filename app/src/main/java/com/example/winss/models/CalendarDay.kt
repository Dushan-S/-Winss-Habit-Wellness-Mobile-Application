package com.example.winss.models

import java.util.Date

data class CalendarDay(
    val date: Date,
    val dayName: String,
    val dayNumber: Int,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)
