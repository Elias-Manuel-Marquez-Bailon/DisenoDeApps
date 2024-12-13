package com.example.myapplication.controller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Vibrator
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
                // Operación exitosa
            } else {
                // Error al guardar la lectura
            }
        }
    }

    private fun activateProximityAlert() {
        // Activar alerta vibratoria según la configuración del usuario
        if (userSettings.proximityAlertType == AlertType.VIBRATION || userSettings.proximityAlertType == AlertType.BOTH) {
            vibrateProximityAlert()
        }
    }

    private fun vibrateProximityAlert() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500) // Vibrar durante 500 ms
    }
}
