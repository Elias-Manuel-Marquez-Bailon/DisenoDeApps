package com.example.myapplication.controller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.myapplication.model.AlertType
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.ProximityReading
import com.example.myapplication.model.UserSettings

class ProximitySensorController(
    private val context: Context,
    private val cloudRepository: CloudRepository,
    private val userSettings: UserSettings
) {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private var proximityReadings: MutableList<ProximityReading> = mutableListOf()

    init {
        startProximitySensorMonitoring()
    }

    private fun startProximitySensorMonitoring() {
        proximitySensor?.let { sensor ->
            sensorManager.registerListener(
                proximityListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun stopProximitySensorMonitoring() {
        proximitySensor?.let { sensor ->
            sensorManager.unregisterListener(proximityListener, sensor)
        }
    }

    private val proximityListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let { sensorEvent ->
                val proximityReading = ProximityReading(
                    timestamp = System.currentTimeMillis(),
                    distance = sensorEvent.values[0]
                )
                processProximityReading(proximityReading)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // No se necesita implementar este método en este caso
        }
    }

    private fun processProximityReading(reading: ProximityReading) {
        proximityReadings.add(reading)

        if (reading.distance <= userSettings.proximityAlertThreshold) {
            // Activar alerta de proximidad
            activateProximityAlert()
        }

        // Guardar las lecturas en la nube
        cloudRepository.storeProximityReading(reading) { success ->
            if (success) {
                // La operación de guardado fue exitosa
            } else {
                // Ocurrió un error al guardar la lectura
            }
        }
    }

    private fun activateProximityAlert() {
        // Activar alerta sonora y/o vibratoria según la configuración del usuario
        when (userSettings.proximityAlertType) {
            AlertType.SOUND -> playProximityAlertSound()
            AlertType.VIBRATION -> vibrateProximityAlert()
            AlertType.BOTH -> {
                playProximityAlertSound()
                vibrateProximityAlert()
            }
            else -> {
                // No hacer nada si el tipo de alerta no está configurado
            }
        }
    }

    private fun playProximityAlertSound() {
        // Implementar lógica para reproducir la alerta sonora
    }

    private fun vibrateProximityAlert() {
        // Implementar lógica para activar la vibración
    }



}