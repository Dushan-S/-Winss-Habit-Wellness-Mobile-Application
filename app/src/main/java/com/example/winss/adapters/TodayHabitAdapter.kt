package com.example.winss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.winss.databinding.ItemHabitBinding
import com.example.winss.models.Habit

class TodayHabitAdapter(
    private val onHabitToggle: (Habit) -> Unit
) : RecyclerView.Adapter<TodayHabitAdapter.TodayHabitViewHolder>() {

    private var habits = mutableListOf<Habit>()

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayHabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodayHabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodayHabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size

    inner class TodayHabitViewHolder(private val binding: ItemHabitBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.habitName.text = habit.name
            binding.habitDescription.text = habit.description
            binding.habitCheckbox.isChecked = habit.isCompleted

            // Hide edit and delete buttons for today's view
            binding.editHabitBtn.visibility = android.view.View.GONE
            binding.deleteHabitBtn.visibility = android.view.View.GONE

            binding.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != habit.isCompleted) {
                    onHabitToggle(habit)
                }
            }
        }
    }
}
