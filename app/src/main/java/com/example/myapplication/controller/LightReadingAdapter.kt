package com.example.myapplication.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.LightReading
import com.example.myapplication.model.UserSettings

class LightReadingAdapter(
    private val context: Context,
    private val userSettings: UserSettings,
    private val onItemClick: (LightReading) -> Unit,
    private val deleteClickListener: (String) -> Unit // Agregar listener para eliminación
) : RecyclerView.Adapter<LightReadingViewHolder>() {

    private val readings = mutableListOf<LightReading>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightReadingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reading, parent, false)
        return LightReadingViewHolder(view, userSettings, deleteClickListener) // Pasar el listener
    }

    override fun onBindViewHolder(holder: LightReadingViewHolder, position: Int) {
        val lightReading = readings[position]
        holder.bind(lightReading)
        holder.itemView.setOnClickListener {
            onItemClick(lightReading)
        }
    }

    override fun getItemCount() = readings.size

    fun updateData(newReadings: List<LightReading>) {
        readings.clear()
        readings.addAll(newReadings)
        notifyDataSetChanged()
    }
}