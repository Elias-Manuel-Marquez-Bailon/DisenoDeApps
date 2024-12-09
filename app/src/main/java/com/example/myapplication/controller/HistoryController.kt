package com.example.myapplication.controller

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.model.LightReading

class HistoryController (private val context: Context) {

    //Metodo guardar una lectura de luz en la base de datos local
    private val sharedPreferences : SharedPreferences by lazy {
        context.getSharedPreferences("light_readings",Context.MODE_PRIVATE)
    }

    fun saveLightReading(lightReading: LightReading) {
        // Guardar una lectura de luz en SharedPreferences
        with(sharedPreferences.edit()) {
            putLong("${lightReading.timestamp}_timestamp",lightReading.timestamp)
            putFloat("${lightReading.timestamp}_lightLevel",lightReading.lightLevel)
            putString("${lightReading.timestamp}_mode",lightReading.mode)
            apply()
        }

    }

    //Obtener la lista de lecturas de luz almacenadas localmente
    fun getLightReadings():List<LightReading> {
        val readings = mutableListOf<LightReading>()
        sharedPreferences.all.forEach{ (key, value) ->
            if (key.endsWith("_timestamp")) {
                val timestamp = sharedPreferences.getLong(key,0L)
                val lightLevel = sharedPreferences.getFloat("${key.substringBeforeLast("_")}_lightLevel",0F)
                val mode = sharedPreferences.getString("${key.substringBeforeLast("_")}_mode",
                    "") ?: ""
                readings.add(LightReading(timestamp,lightLevel,mode))
            }
        }
        return readings
    }

    //Metodo Eliminar todas las lecturas de luz almacenadas localmente
    fun clearLightReadings() {
        sharedPreferences.edit().clear().apply()
    }

}