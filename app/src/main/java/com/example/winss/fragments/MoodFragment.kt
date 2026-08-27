package com.example.winss.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winss.R
import com.example.winss.adapters.MoodAdapter
import com.example.winss.databinding.FragmentMoodBinding
import com.example.winss.models.MoodEntry
import com.example.winss.utils.SharedPrefManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*

class MoodFragment : Fragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var moodAdapter: MoodAdapter
    private var selectedEmoji = "😊"
    private var selectedMoodValue = 4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPrefManager = SharedPrefManager(requireContext())
        setupRecyclerView()
        setupEmojiSelector()
        setupChart()
        setupShareButton()
        loadMoodHistory()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter()
        
        binding.moodHistoryRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodAdapter
        }
    }

    private fun setupEmojiSelector() {
        val emojiButtons = listOf(
            binding.emoji1 to ("😢" to 1),
            binding.emoji2 to ("😔" to 2),
            binding.emoji3 to ("😐" to 3),
            binding.emoji4 to ("😊" to 4),
            binding.emoji5 to ("😄" to 5)
        )

        emojiButtons.forEach { (button, emojiData) ->
            button.setOnClickListener {
                selectedEmoji = emojiData.first
                selectedMoodValue = emojiData.second
                
                // Update button states
                emojiButtons.forEach { (btn, _) ->
                    btn.isSelected = false
                }
                button.isSelected = true
            }
        }

        // Set default selection
        binding.emoji4.isSelected = true

        binding.addMoodBtn.setOnClickListener {
            addMoodEntry()
        }
    }

    private fun addMoodEntry() {
        val note = binding.moodNoteInput.text.toString().trim()
        val currentDate = sharedPrefManager.getCurrentDate()
        val currentTime = sharedPrefManager.getCurrentTime()
        
        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            emoji = selectedEmoji,
            note = note,
            date = currentDate,
            time = currentTime,
            moodValue = selectedMoodValue
        )
        
        sharedPrefManager.saveMoodEntry(moodEntry)
        binding.moodNoteInput.text?.clear()
        loadMoodHistory()
        updateChart()
        Toast.makeText(context, "Mood logged successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun setupChart() {
        val chart = binding.moodTrendsChart
        chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(true)
                textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 12f
            }
            
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
                textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 12f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
        
        updateChart()
    }

    private fun updateChart() {
        val moodEntries = sharedPrefManager.getMoodEntriesForChart(7)
        if (moodEntries.isEmpty()) return

        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        moodEntries.forEachIndexed { index, moodEntry ->
            entries.add(Entry(index.toFloat(), moodEntry.moodValue.toFloat()))
            labels.add(sharedPrefManager.getDateString(moodEntry.date))
        }

        val dataSet = LineDataSet(entries, "Mood").apply {
            color = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple)
            setCircleColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple))
            lineWidth = 3f
            circleRadius = 6f
            setDrawFilled(true)
            fillColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_purple_light)
            setDrawValues(false)
        }

        val lineData = LineData(dataSet)
        binding.moodTrendsChart.apply {
            data = lineData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            invalidate()
        }
    }

    private fun setupShareButton() {
        binding.shareMoodBtn.setOnClickListener {
            shareMoodEntries()
        }
    }

    private fun shareMoodEntries() {
        val lastMoodEntries = sharedPrefManager.getLastMoodEntries(5)
        if (lastMoodEntries.isEmpty()) {
            Toast.makeText(context, "No mood entries to share", Toast.LENGTH_SHORT).show()
            return
        }

        val shareText = buildString {
            append(getString(R.string.mood_share_text))
            lastMoodEntries.forEach { entry ->
                append("${entry.emoji} ${entry.date} at ${entry.time}")
                if (entry.note.isNotEmpty()) {
                    append(" - ${entry.note}")
                }
                append("\n")
            }
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Mood Entries"))
    }

    private fun loadMoodHistory() {
        val moodEntries = sharedPrefManager.getMoodEntries().takeLast(10).reversed()
        if (moodEntries.isEmpty()) {
            binding.noMoodEntriesText.visibility = View.VISIBLE
            binding.moodHistoryRecycler.visibility = View.GONE
        } else {
            binding.noMoodEntriesText.visibility = View.GONE
            binding.moodHistoryRecycler.visibility = View.VISIBLE
            moodAdapter.updateMoodEntries(moodEntries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
