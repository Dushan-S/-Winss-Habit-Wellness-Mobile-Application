package com.example.winss.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.winss.models.Habit
import com.example.winss.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wellness_app", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val HABITS_KEY = "habits"
        private const val MOOD_ENTRIES_KEY = "mood_entries"
        private const val HYDRATION_INTERVAL_KEY = "hydration_interval"
        private const val USER_NAME_KEY = "user_name"
        private const val REMINDER_START_HOUR_KEY = "reminder_start_hour"
        private const val REMINDER_START_MINUTE_KEY = "reminder_start_minute"
        private const val REMINDER_END_HOUR_KEY = "reminder_end_hour"
        private const val REMINDER_END_MINUTE_KEY = "reminder_end_minute"
        private const val REMINDERS_ENABLED_KEY = "reminders_enabled"
        private const val SOUND_ENABLED_KEY = "sound_enabled"
        private const val VIBRATION_ENABLED_KEY = "vibration_enabled"
    }

    //habit section
    fun saveHabits(habits: List<Habit>) {
        val habitsJson = gson.toJson(habits)
        prefs.edit().putString(HABITS_KEY, habitsJson).apply()
    }

    fun getHabits(): List<Habit> {
        val habitsJson = prefs.getString(HABITS_KEY, null)
        return if (habitsJson != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(habitsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun getTodayHabits(): List<Habit> {
        val today = getCurrentDate()
        return getHabits().filter { it.completedDate == today }
    }

    fun getCompletedHabitsCount(): Int {
        val today = getCurrentDate()
        return getHabits().count { it.isCompleted && it.completedDate == today }
    }

    fun getTotalHabitsCount(): Int {
        return getHabits().size
    }
//marked completed tasks to today's progress
    fun toggleHabitCompletion(habitId: String) {
        val habits = getHabits().toMutableList()
        val habitIndex = habits.indexOfFirst { it.id == habitId }
        if (habitIndex != -1) {
            val habit = habits[habitIndex]
            val today = getCurrentDate()
            val updatedHabit = if (habit.isCompleted && habit.completedDate == today) {
                habit.copy(isCompleted = false, completedDate = null)
            } else {
                habit.copy(isCompleted = true, completedDate = today)
            }
            habits[habitIndex] = updatedHabit
            saveHabits(habits)
        }
    }

    // Mood entries management
    fun saveMoodEntry(moodEntry: MoodEntry) {
        val moodEntries = getMoodEntries().toMutableList()
        moodEntries.add(moodEntry)
        saveMoodEntries(moodEntries)
    }

    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val moodEntriesJson = gson.toJson(moodEntries)
        prefs.edit().putString(MOOD_ENTRIES_KEY, moodEntriesJson).apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val moodEntriesJson = prefs.getString(MOOD_ENTRIES_KEY, null)
        return if (moodEntriesJson != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(moodEntriesJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun getTodayMood(): MoodEntry? {
        val today = getCurrentDate()
        return getMoodEntries().lastOrNull { it.date == today }
    }

    fun getLastMoodEntries(count: Int = 5): List<MoodEntry> {
        return getMoodEntries().takeLast(count)
    }

    fun getMoodEntriesForChart(days: Int = 7): List<MoodEntry> {
        val moodEntries = getMoodEntries()
        return moodEntries.takeLast(days)
    }

    // Settings management
    fun saveHydrationInterval(intervalMinutes: Int) {
        prefs.edit().putInt(HYDRATION_INTERVAL_KEY, intervalMinutes).apply()
    }

    fun getHydrationInterval(): Int {
        return prefs.getInt(HYDRATION_INTERVAL_KEY, 60) // Default 60 minutes
    }

    // Reminder time settings
    fun saveReminderStartTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(REMINDER_START_HOUR_KEY, hour)
            .putInt(REMINDER_START_MINUTE_KEY, minute)
            .apply()
    }

    fun saveReminderEndTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(REMINDER_END_HOUR_KEY, hour)
            .putInt(REMINDER_END_MINUTE_KEY, minute)
            .apply()
    }

    fun getReminderStartHour(): Int {
        return prefs.getInt(REMINDER_START_HOUR_KEY, 9) // Default 9 AM
    }

    fun getReminderStartMinute(): Int {
        return prefs.getInt(REMINDER_START_MINUTE_KEY, 0) // Default 0 minutes
    }

    fun getReminderEndHour(): Int {
        return prefs.getInt(REMINDER_END_HOUR_KEY, 21) // Default 9 PM
    }

    fun getReminderEndMinute(): Int {
        return prefs.getInt(REMINDER_END_MINUTE_KEY, 0) // Default 0 minutes
    }

    fun setRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(REMINDERS_ENABLED_KEY, enabled).apply()
    }

    fun isRemindersEnabled(): Boolean {
        return prefs.getBoolean(REMINDERS_ENABLED_KEY, true) // Default enabled
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(SOUND_ENABLED_KEY, enabled).apply()
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(SOUND_ENABLED_KEY, true) // Default enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(VIBRATION_ENABLED_KEY, enabled).apply()
    }

    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(VIBRATION_ENABLED_KEY, true) // Default enabled
    }

    fun saveUserName(name: String) {
        prefs.edit().putString(USER_NAME_KEY, name).apply()
    }

    fun getUserName(): String {
        return prefs.getString(USER_NAME_KEY, "User") ?: "User"
    }


    fun getCurrentDate(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    fun getCurrentTime(): String {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }

    fun getDateString(date: String): String {
        val parts = date.split("-")
        if (parts.size == 3) {
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            val monthNames = arrayOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            return "${monthNames[month]} $day"
        }
        return date
    }
}
