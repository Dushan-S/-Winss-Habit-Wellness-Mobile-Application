package com.example.winss.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.winss.R
import com.example.winss.databinding.FragmentSettingsBinding
import com.example.winss.utils.NotificationManager
import com.example.winss.utils.SharedPrefManager

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var notificationManager: NotificationManager
    private var isRemindersEnabled = false

    // Dropdown options with display text and corresponding minutes
    private val intervalOptions = mapOf(
        "1 minute" to 1,
        "30 minutes" to 30,
        "1 hour" to 60,
        "2 hours" to 120,
        "3 hours" to 180
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefManager = SharedPrefManager(requireContext())
        notificationManager = NotificationManager(requireContext())
        setupDropdown()
        loadCurrentSettings()
        setupToggleButton()
        setupAutoSave()
    }

    private fun setupDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            intervalOptions.keys.toList()
        )
        binding.intervalDropdown.setAdapter(adapter)

        // Set default selection
        binding.intervalDropdown.setText("1 hour", false)
    }

    private fun setupAutoSave() {
        // Auto-save when interval is selected
        binding.intervalDropdown.setOnItemClickListener { _, _, _, _ ->
            autoSaveSettings()
        }
    }

    private fun loadCurrentSettings() {
        val currentInterval = sharedPrefManager.getHydrationInterval()

        // Find matching interval option
        val matchingOption = intervalOptions.entries.find { it.value == currentInterval }
        if (matchingOption != null) {
            binding.intervalDropdown.setText(matchingOption.key, false)
        }

        // Load reminder enabled state
        isRemindersEnabled = sharedPrefManager.isRemindersEnabled()
        updateToggleButtonState()
    }

    private fun setupToggleButton() {
        binding.reminderToggleBtn.setOnClickListener {
            isRemindersEnabled = !isRemindersEnabled
            updateToggleButtonState()
            autoSaveSettings()

            if (!isRemindersEnabled) {
                // Cancel notifications immediately when disabled
                notificationManager.cancelAllReminders()
            }
        }

        // Set initial state
        updateToggleButtonState()
    }

    private fun updateToggleButtonState() {
        if (isRemindersEnabled) {
            binding.reminderToggleBtn.text = "Disable Reminders"
            binding.reminderToggleBtn.setBackgroundColor(resources.getColor(R.color.error_red, null))
            binding.intervalDropdown.isEnabled = true
        } else {
            binding.reminderToggleBtn.text = "Enable Reminders"
            binding.reminderToggleBtn.setBackgroundColor(resources.getColor(R.color.primary_purple, null))
            binding.intervalDropdown.isEnabled = false
        }
    }

    private fun autoSaveSettings() {
        val selectedText = binding.intervalDropdown.text.toString().trim()

        if (selectedText.isEmpty()) return

        val selectedInterval = intervalOptions[selectedText] ?: return

        // Save settings automatically
        sharedPrefManager.saveHydrationInterval(selectedInterval)
        sharedPrefManager.setRemindersEnabled(isRemindersEnabled)

        // Schedule notifications if enabled
        if (isRemindersEnabled) {
            notificationManager.scheduleHydrationReminders(selectedInterval)
            Toast.makeText(context, "Notifications enabled every $selectedText", Toast.LENGTH_SHORT).show()
        } else {
            notificationManager.cancelAllReminders()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
