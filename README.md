# Winss — Wellness Android App

A modern Android wellness application built with **Kotlin**, designed to help users build healthy habits, track moods, and maintain daily hydration routines.

## Overview

Winss combines habit tracking, mood journaling, hydration reminders, and progress visualization in one simple Android application.

The app uses local storage for user data and background tasks for hydration reminders.

## Features

### Dashboard

* Personalized welcome message
* 7-day habit progress
* Daily habit completion
* Progress visualization
* Current mood summary

### Habit Management

* Create, edit, and delete habits
* Mark habits as completed
* Daily progress tracking
* Local data storage
* RecyclerView-based habit lists

### Mood Journal

* 5-level mood selection
* Optional mood notes
* Mood history
* 7-day mood trend chart
* Share mood entries

### Hydration Reminders

* Custom reminder intervals
* Background reminder scheduling
* Notification support
* Saved reminder preferences

### Home Screen Widget

* Displays daily habit progress
* Automatically updates progress
* Quick access to the application

## UI Preview

<img width="1600" height="726" alt="winss" src="https://github.com/user-attachments/assets/884657b3-22f9-495d-9da7-2f51e23ba18c" />


## Technology Stack

| Technology            | Purpose                   |
| --------------------- | ------------------------- |
| **Kotlin**            | Android app development   |
| **Android Studio**    | Development               |
| **XML**               | UI design and layouts     |
| **Material Design 3** | UI components and styling |
| **SharedPreferences** | Local data storage        |
| **WorkManager**       | Background reminders      |

## Project Structure

```text
app/
└── src/main/
    │
    ├── java/com/example/winss/
    │
    │   ├── MainActivity.kt
    │   │
    │   ├── fragments/
    │   │   ├── HomeFragment.kt
    │   │   ├── HabitFragment.kt
    │   │   ├── MoodFragment.kt
    │   │   └── SettingsFragment.kt
    │   │
    │   ├── adapters/
    │   │   ├── HabitAdapter.kt
    │   │   ├── TodayHabitAdapter.kt
    │   │   └── MoodAdapter.kt
    │   │
    │   ├── models/
    │   │   ├── Habit.kt
    │   │   └── MoodEntry.kt
    │   │
    │   ├── utils/
    │   │   ├── SharedPrefManager.kt
    │   │   └── NotificationManager.kt
    │   │
    │   ├── reminders/
    │   │   └── HydrationWorker.kt
    │   │
    │   └── widget/
    │       └── HabitWidgetProvider.kt
    │
    └── res/
        ├── layout/
        ├── drawable/
        ├── values/
        └── xml/
```
