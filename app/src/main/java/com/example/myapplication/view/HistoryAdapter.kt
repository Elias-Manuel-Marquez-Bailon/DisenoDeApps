package com.example.myapplication.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.controller.LightReadingViewHolder
import com.example.myapplication.model.LightReading
import com.example.myapplication.model.UserSettings

class HistoryAdapter (
    private val readings: List<LightReading>,
    private val userSettings: UserSettings
): RecyclerView.Adapter<LightReadingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightReadingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reading, parent,false)
        return LightReadingViewHolder(view,userSettings)
    }

    override fun onBindViewHolder(holder: LightReadingViewHolder, position: Int) {
        holder.bind(readings[position])
    }

    override fun getItemCount() = readings.size

}