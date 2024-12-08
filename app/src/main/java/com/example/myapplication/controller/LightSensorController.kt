package com.example.myapplication.controller

import android.content.Context
import com.example.myapplication.model.UserSettings

class LightSensorController (
    private val context: Context,
    private val userSettings: UserSettings
) {
    private val alertController = AlertController(context)

    fun checkLightLevels(currentLightLevel: Float) {
        when {
            currentLightLevel < userSettings.lowLightThreshold -> {
                // Nivel de luz muy bajo
                alertController.triggerAlert(userSettings.alertType)
            }
            currentLightLevel > userSettings.highLightThreshold -> {
                // Nivel de luz muy alto
                alertController.triggerAlert(userSettings.alertType)
            }
            else -> {
                // Nivel de luz normal, detener alertas
                alertController.stopAlerts()
            }
        }
    }
}