package com.example.winss.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winss.R
import com.example.winss.adapters.TodayHabitAdapter
import com.example.winss.adapters.WeekCalendarAdapter
import com.example.winss.databinding.FragmentHomeBinding
import com.example.winss.models.CalendarDay
import com.example.winss.models.Habit
import com.example.winss.models.MoodEntry
import com.example.winss.utils.SharedPrefManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var todayHabitAdapter: TodayHabitAdapter
    private lateinit var weekCalendarAdapter: WeekCalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPrefManager = SharedPrefManager(requireContext())
        setupCalendar()
        setupRecyclerView()
        updateProgress()
        updateCurrentMood()
        loadTodayHabits()
    }

    private fun setupCalendar() {
        weekCalendarAdapter = WeekCalendarAdapter(generateWeekDays()) { selectedDay ->
            // Handle day selection - you can add functionality here later
            // For example, filter habits or mood entries by selected date
        }

        binding.calendarRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = weekCalendarAdapter
        }
    }

    private fun generateWeekDays(): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val calendarDays = mutableListOf<CalendarDay>()

        // Start from Monday of current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Generate 7 days starting from Monday
        for (i in 0..6) {
            val date = calendar.time
            val dayName = dayFormat.format(date).uppercase()
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val isToday = calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                         calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            calendarDays.add(
                CalendarDay(
                    date = date,
                    dayName = dayName,
                    dayNumber = dayNumber,
                    isToday = isToday
                )
            )

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendarDays
    }

    private fun setupRecyclerView() {
        todayHabitAdapter = TodayHabitAdapter { habit ->
            sharedPrefManager.toggleHabitCompletion(habit.id)
            updateProgress()
            loadTodayHabits()
        }
        
        binding.habitsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todayHabitAdapter
        }
    }

    private fun updateProgress() {
        val completedCount = sharedPrefManager.getCompletedHabitsCount()
        val totalCount = sharedPrefManager.getTotalHabitsCount()
        
        binding.tasksCompletedCount.text = completedCount.toString()

        if (totalCount > 0) {
            val percentage = (completedCount * 100) / totalCount
            setupPieChart(completedCount, totalCount - completedCount)
        } else {
            setupPieChart(0, 0)
        }
    }

    private fun setupPieChart(completed: Int, remaining: Int) {
        val pieChart = binding.pieChart
        val entries = mutableListOf<PieEntry>()
        val total = completed + remaining

        if (total > 0) {
            // Always add both completed and remaining portions
            entries.add(PieEntry(completed.toFloat(), "Completed"))
            entries.add(PieEntry(remaining.toFloat(), "Remaining"))
        } else {
            // When no habits exist, show empty chart
            entries.add(PieEntry(1f, ""))
        }
        
        val dataSet = PieDataSet(entries, "").apply {
            colors = if (total > 0) {
                listOf(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple),
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple_light)
                )
            } else {
                listOf(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple_light))
            }
            setDrawValues(false) // Don't show values on segments, we'll show in center
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.WHITE
        }

        val pieData = PieData(dataSet)

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.isEnabled = false
            setHoleColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.surface_purple))
            setTransparentCircleColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.surface_purple))
            setTransparentCircleAlpha(110)
            holeRadius = 70f
            transparentCircleRadius = 75f
            setDrawEntryLabels(false)
            setNoDataText(if (total == 0) "No habits added" else "")
            isRotationEnabled = false
            animateY(1000)

            // Add percentage text in center
            if (total > 0) {
                val percentage = (completed * 100f) / total
                centerText = "${percentage.toInt()}%"
                setCenterTextSize(24f)
                setCenterTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple))
            } else {
                centerText = ""
            }

            invalidate()
        }
    }

    private fun updateCurrentMood() {
        val todayMood = sharedPrefManager.getTodayMood()
        binding.currentMood.text = todayMood?.emoji ?: "😊"
    }

    private fun loadTodayHabits() {
        val habits = sharedPrefManager.getHabits()
        if (habits.isEmpty()) {
            binding.habitsRecyclerView.visibility = View.GONE
        } else {
            binding.habitsRecyclerView.visibility = View.VISIBLE
            todayHabitAdapter.updateHabits(habits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
