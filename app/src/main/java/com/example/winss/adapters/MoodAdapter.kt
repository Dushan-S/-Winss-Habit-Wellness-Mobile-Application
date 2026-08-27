package com.example.winss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.winss.databinding.ItemMoodBinding
import com.example.winss.models.MoodEntry

class MoodAdapter : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    private var moodEntries = mutableListOf<MoodEntry>()

    fun updateMoodEntries(newEntries: List<MoodEntry>) {
        moodEntries.clear()
        moodEntries.addAll(newEntries)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }

    override fun getItemCount(): Int = moodEntries.size

    inner class MoodViewHolder(private val binding: ItemMoodBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry) {
            binding.moodEmoji.text = moodEntry.emoji
            binding.moodNote.text = moodEntry.note.ifEmpty { "No note" }
            binding.moodDate.text = "${moodEntry.date} at ${moodEntry.time}"
        }
    }
}
