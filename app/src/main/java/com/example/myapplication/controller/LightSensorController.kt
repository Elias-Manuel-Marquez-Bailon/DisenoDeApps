package com.example.myapplication.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
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
    //Se agregó esta variable para niveles bruscos
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

    // Método inicia el monitoreo del sensor en la base de datos
    fun startLightSensorMonitoring() {
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
    }

    // Método detener el monitoreo del sensor de luz
    fun stopLightSensorMonitoring() {
        sensorEventListener?.let {
            sensorManager?.unregisterListener(it)
        }
        sensorEventListener = null
        sensorManager = null
        lightSensor = null
    }

    // Método para manejar los cambios en el nivel de luz y realizar las acciones correspondientes
    fun onLightLevelChanged(lightLevel: Float, mode: String) {
        // Verificar si el nivel de luz actual está fuera del rango adecuado
        if (isBrightnessDifferenceSignificant(lightLevel)) {
            cloudRepository.uploadLightReading(lightLevel, "Modo") { success ->
                // Manejar el resultado de la operación de guardado
            }
            previousLightLevel = lightLevel
        }
        checkLightLevels(lightLevel, mode)
    }

    private fun isBrightnessDifferenceSignificant(currentLightLevel: Float): Boolean {
        val brightnessDifference = abs(currentLightLevel - previousLightLevel)
        if (brightnessDifference >= max(userSettings.lowLightThreshold, userSettings.highLightThreshold) * 0.2) {
            // Enviar notificación
            sendLightChangeNotification()
            return true
        }
        return false
    }

    private fun sendLightChangeNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "light_sensor_notifications"

        // Crear un canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Luz",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Usa un ícono en drawable
            .setContentTitle("CUIDADO")
            .setContentText("Se detectó un cambio brusco de luz.")
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
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

    private fun sendTestNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "test_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Test Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Test Notification")
            .setContentText("This is a test notification.")
            .setAutoCancel(true)

        notificationManager.notify(999, notificationBuilder.build())
    }

}
