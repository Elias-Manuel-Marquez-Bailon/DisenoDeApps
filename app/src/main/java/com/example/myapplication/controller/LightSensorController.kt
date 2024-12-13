package com.example.myapplication.controller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.LightReading
import com.example.myapplication.model.UserSettings
import kotlin.math.abs
import kotlin.math.max

class LightSensorController (
    private val context: Context,
    private val userSettings: UserSettings,
    private val cloudRepository: CloudRepository
) {
    private val alertController = AlertController(context)
    private var sensorEventListener: SensorEventListener? = null
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var isLightSensorRegistered = false // Bandera para evitar registros redundantes

    //Se agrego esta variable para niveles bruscos
    private var previousLightLevel: Float = 0F

    private var listener: LightSensorListener? = null

    interface LightSensorListener {
        fun onSensorChanged(lightLevel: Float, mode: String)
    }

    var onLightLevelChanged: ((Float) -> Unit)? = null

    // Método para establecer el listener
    fun setListener(lightSensorListener: LightSensorListener) {
        listener = lightSensorListener
    }

    //Metodo inicia el monitoreo del sensor en la base de datos}


    fun startLightSensorMonitoring() {
        if (isLightSensorRegistered) {
            return // Evitar registrar el listener si ya está registrado
        }

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val currentLightLevel = event.values[0]
                onLightLevelChanged?.invoke(currentLightLevel)
                // Notificar al listener
                listener?.onSensorChanged(currentLightLevel, "Lectura")
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Este método se llama cuando cambia la precisión del sensor
            }
        }

        sensorManager?.registerListener(
            sensorEventListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        isLightSensorRegistered = true // Marcar que el listener está registrado
    }

    fun stopLightSensorMonitoring() {
        if (!isLightSensorRegistered) {
            return // Evitar desregistrar si no está registrado
        }

        sensorEventListener?.let {
            sensorManager?.unregisterListener(it)
        }
        sensorEventListener = null
        sensorManager = null
        lightSensor = null
        isLightSensorRegistered = false // Marcar que el listener ya no está registrado
    }


    //Metodo para manejar los cambios en el nivel de luz y realizar las acciones correspondientes
    fun onLightLevelChanged(lightLevel: Float, mode: String) {
        // Verificar si el nivel de luz actual está fuera del rango adecuado
        if (isBrightnessDifferenceSignificant(lightLevel)) {
            cloudRepository.uploadLightReading(lightLevel, "Modo") { succes ->
                //Manejar el resultado de la operacion de guardado
            }
            previousLightLevel = lightLevel
        }
        checkLightLevels(lightLevel, mode)
    }

    private fun isBrightnessDifferenceSignificant(currentLightLevel: Float): Boolean {
        val brightnessDifference = abs(currentLightLevel - previousLightLevel)
        return brightnessDifference >= max(
            userSettings.lowLightThreshold,
            userSettings.highLightThreshold
        ) * 0.2 //20% De diferencia
    }

    private fun checkLightLevels(currentLightLevel: Float, mode: String) {
        when (mode) {
            "Lectura" -> {
                if (currentLightLevel < userSettings.lowLightThreshold) {
                    // Nivel de luz muy bajo
                    alertController.triggerAlert(userSettings.alertType)
                } else if (currentLightLevel > userSettings.highLightThreshold) {
                    // Nivel de luz muy alto
                    alertController.triggerAlert(userSettings.alertType)
                } else {
                    // Nivel de luz normal, detener alertas
                    alertController.stopAlerts()
                }
            }

            "Exterior" -> {
                // Verificar niveles de luz para el modo "Exterior"
                if (currentLightLevel < userSettings.exteriorLowLightThreshold) {
                    alertController.triggerAlert(userSettings.exteriorAlertType)
                } else if (currentLightLevel > userSettings.exteriorHighLightThreshold) {
                    alertController.triggerAlert(userSettings.exteriorAlertType)
                } else {
                    alertController.stopAlerts()
                }
            }

        }
    }
}