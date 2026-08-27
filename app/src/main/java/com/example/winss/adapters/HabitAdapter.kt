package com.example.winss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.winss.R
import com.example.winss.databinding.ItemHabitBinding
import com.example.winss.models.Habit
import com.example.winss.utils.SharedPrefManager

class HabitAdapter(
    private val onHabitToggle: (Habit) -> Unit,
    private val onEditHabit: (Habit) -> Unit,
    private val onDeleteHabit: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private var habits = mutableListOf<Habit>()
    private lateinit var sharedPrefManager: SharedPrefManager

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    fun setSharedPrefManager(sharedPrefManager: SharedPrefManager) {
        this.sharedPrefManager = sharedPrefManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size

    inner class HabitViewHolder(private val binding: ItemHabitBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.habitName.text = habit.name
            binding.habitDescription.text = habit.description
            binding.habitCheckbox.isChecked = habit.isCompleted

            binding.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != habit.isCompleted) {
                    onHabitToggle(habit)
                }
            }

            binding.editHabitBtn.setOnClickListener {
                onEditHabit(habit)
            }

            binding.deleteHabitBtn.setOnClickListener {
                onDeleteHabit(habit)
            }
        }
    }
}
