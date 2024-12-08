package com.example.myapplication.model

data class UserSettings(
    var lowLightThreshold: Float, //Umbrale de luz baja
    var highLightThreshold: Float, //Umbrale de luz alta
    var alertType: AlertType //Tipo de alerta: sonido, vibracion o ambas
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

