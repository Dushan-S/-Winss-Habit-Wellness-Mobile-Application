package com.example.winss.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.winss.R
import com.example.winss.adapters.HabitAdapter
import com.example.winss.databinding.DialogAddHabitBinding
import com.example.winss.databinding.FragmentHabitBinding
import com.example.winss.models.Habit
import com.example.winss.utils.SharedPrefManager
import java.util.*

class HabitFragment : Fragment() {
    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPrefManager = SharedPrefManager(requireContext())
        setupRecyclerView()
        setupFab()
        updateProgress()
        loadHabits()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onHabitToggle = { habit ->
                sharedPrefManager.toggleHabitCompletion(habit.id)
                updateProgress()
                habitAdapter.notifyDataSetChanged()
            },
            onEditHabit = { habit ->
                showAddEditHabitDialog(habit)
            },
            onDeleteHabit = { habit ->
                showDeleteConfirmationDialog(habit)
            }
        )
        
        binding.habitsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitAdapter
        }
    }

    private fun setupFab() {
        binding.addHabitFab.setOnClickListener {
            showAddEditHabitDialog()
        }
    }

    private fun showAddEditHabitDialog(habit: Habit? = null) {
        val dialogBinding = DialogAddHabitBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        if (habit != null) {
            dialogBinding.habitNameInput.setText(habit.name)
            dialogBinding.habitDescriptionInput.setText(habit.description)
        }

        dialogBinding.saveBtn.setOnClickListener {
            val name = dialogBinding.habitNameInput.text.toString().trim()
            val description = dialogBinding.habitDescriptionInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter habit name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (habit != null) {
                // Edit existing habit
                val updatedHabit = habit.copy(name = name, description = description)
                val habits = sharedPrefManager.getHabits().toMutableList()
                val index = habits.indexOfFirst { it.id == habit.id }
                if (index != -1) {
                    habits[index] = updatedHabit
                    sharedPrefManager.saveHabits(habits)
                }
            } else {
                // Add new habit
                val newHabit = Habit(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description
                )
                val habits = sharedPrefManager.getHabits().toMutableList()
                habits.add(newHabit)
                sharedPrefManager.saveHabits(habits)
            }

            loadHabits()
            updateProgress()
            dialog.dismiss()
        }

        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Yes") { _, _ ->
                val habits = sharedPrefManager.getHabits().toMutableList()
                habits.removeAll { it.id == habit.id }
                sharedPrefManager.saveHabits(habits)
                loadHabits()
                updateProgress()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateProgress() {
        val completedCount = sharedPrefManager.getCompletedHabitsCount()
        val totalCount = sharedPrefManager.getTotalHabitsCount()
        
        binding.progressText.text = "$completedCount of $totalCount habits completed"
        
        if (totalCount > 0) {
            val progress = (completedCount * 100) / totalCount
            binding.progressBar.progress = progress
        } else {
            binding.progressBar.progress = 0
        }
    }

    private fun loadHabits() {
        val habits = sharedPrefManager.getHabits()
        if (habits.isEmpty()) {
            binding.noHabitsText.visibility = View.VISIBLE
            binding.habitsRecycler.visibility = View.GONE
        } else {
            binding.noHabitsText.visibility = View.GONE
            binding.habitsRecycler.visibility = View.VISIBLE
            habitAdapter.updateHabits(habits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
