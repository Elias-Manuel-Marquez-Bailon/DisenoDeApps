package com.example.myapplication.model

import com.example.myapplication.utils.Constants

data class UserSettings(
    //Umbrale de luz baja
    var lowLightThreshold: Float = Constants.DEFAULT_LOW_LIGHT_THRESHOLD,
    //Umbrale de luz alta
    var highLightThreshold: Float = Constants.DEFAULT_HIGH_LIGHT_THRESHOLD,
    var alertType: AlertType = AlertType.BOTH, //Tipo de alerta: sonido, vibracion o ambas

    // Umbrales y alertas para el modo "Lectura"
    var readingLowLightThreshold: Float = Constants.DEFAULT_READING_LOW_LIGHT_THRESHOLD,
    var readingHighLightThreshold: Float = Constants.DEFAULT_READING_HIGH_LIGHT_THRESHOLD,
    var readingAlertType: AlertType = AlertType.BOTH,

    // Umbrales y alertas para el modo "Exterior"
    var exteriorLowLightThreshold: Float = Constants.DEFAULT_EXTERIOR_LOW_LIGHT_THRESHOLD,
    var exteriorHighLightThreshold: Float = Constants.DEFAULT_EXTERIOR_HIGH_LIGHT_THRESHOLD,
    var exteriorAlertType: AlertType = AlertType.BOTH,

    var alertVolume: Int = Constants.DEFAULT_ALERT_VOLUME,
    var autoModeChangeEnabled : Boolean = true,
    var defaultMode : String = Constants.DEFAULT_MODE

) {
    //Devuelve una descripcion del tipo de alerta
    fun getAlertTypeDescription(): String {
        return when (alertType) {
            AlertType.SOUND -> "Alerta sonora"
            AlertType.VIBRATION -> "Alerta por vibración"
            AlertType.BOTH -> "Alerta sonora y vibración"
        }
    }
    override fun toString(): String {
        return "Configuraciones de Usuario:\n" +
                "Umbral de Luz Baja: $lowLightThreshold lux\n" +
                "Umbral de Luz Alta: $highLightThreshold lux\n" +
                "Tipo de Alerta: ${getAlertTypeDescription()}"
    }
}

