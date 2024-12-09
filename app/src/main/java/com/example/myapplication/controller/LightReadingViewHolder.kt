package com.example.myapplication.controller

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.LightReading
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LightReadingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val txtValorLux: TextView = itemView.findViewById(R.id.txtValorLux)
    private val txtModo: TextView = itemView.findViewById(R.id.txtModo)
    private val txtEstadoLuz: TextView = itemView.findViewById(R.id.txtEstadoLuz)
    private val txtFechaHora: TextView = itemView.findViewById(R.id.txtFechaHora)

    fun bind(lightReading: LightReading) {
        txtValorLux.text = "${lightReading.lightLevel} lux"
        txtModo.text = lightReading.mode
        txtFechaHora.text = formatTimestamp(lightReading.timestamp)

        // Establecer el estado de la lectura de luz (Luz adecuada, Luz baja, Luz alta)
        txtEstadoLuz.text = getReadingStatus(lightReading.lightLevel)
    }

    private fun formatTimestamp(timestamp: Long): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun getReadingStatus (lightLevel : Float): String {
        return when {
            lightLevel < userSettings.lowLightThreshold -> "Luz baja"
            lightLevel > userSettings.highLightThreshold -> "Luz alta"
            else -> "Luz adecuada"
        }
    }
}