package com.example.winss.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val isCompleted: Boolean = false,
    val completedDate: String? = null
) : Parcelable
