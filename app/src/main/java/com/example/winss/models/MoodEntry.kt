package com.example.winss.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoodEntry(
    val id: String,
    val emoji: String,
    val note: String,
    val date: String,
    val time: String,
    val moodValue: Int // 1-5 scale for chart
) : Parcelable
