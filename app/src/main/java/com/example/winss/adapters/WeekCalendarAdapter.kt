package com.example.winss.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.winss.R
import com.example.winss.databinding.ItemCalendarDayBinding
import com.example.winss.models.CalendarDay

class WeekCalendarAdapter(
    private var calendarDays: List<CalendarDay>,
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<WeekCalendarAdapter.CalendarDayViewHolder>() {

    private var selectedPosition = -1

    inner class CalendarDayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendarDay: CalendarDay, position: Int) {
            binding.dayName.text = calendarDay.dayName
            binding.dayNumber.text = calendarDay.dayNumber.toString()

            // Show today indicator
            binding.todayIndicator.visibility = if (calendarDay.isToday) View.VISIBLE else View.GONE

            // Handle selection state
            val isSelected = position == selectedPosition || calendarDay.isSelected
            binding.root.setCardBackgroundColor(
                if (isSelected) {
                    binding.root.context.getColor(R.color.primary_purple)
                } else {
                    binding.root.context.getColor(R.color.white)
                }
            )

            // Update text colors based on selection
            val textColor = if (isSelected) {
                binding.root.context.getColor(R.color.white)
            } else {
                binding.root.context.getColor(R.color.text_primary)
            }
            binding.dayNumber.setTextColor(textColor)

            val secondaryTextColor = if (isSelected) {
                binding.root.context.getColor(R.color.white)
            } else {
                binding.root.context.getColor(R.color.text_secondary)
            }
            binding.dayName.setTextColor(secondaryTextColor)

            // Click listener
            binding.root.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = position

                // Notify changes for both positions
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)

                onDayClick(calendarDay)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(calendarDays[position], position)
    }

    override fun getItemCount(): Int = calendarDays.size

    fun updateCalendarDays(newCalendarDays: List<CalendarDay>) {
        calendarDays = newCalendarDays
        // Find today's position and set it as selected
        selectedPosition = calendarDays.indexOfFirst { it.isToday }
        notifyDataSetChanged()
    }
}
