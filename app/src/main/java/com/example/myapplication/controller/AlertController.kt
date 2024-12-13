package com.example.myapplication.controller

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.model.AlertType

//Se añadio esta importacion
import androidx.annotation.RequiresApi

class AlertController(private val context: Context) {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            // Para Android 12 y superior
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            // Para versiones anteriores de Android
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val toneGenerator: ToneGenerator by lazy {
        ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME)
    }

    fun triggerAlert(alertType: AlertType) {
        when (alertType) {
            AlertType.SOUND -> playAlertSound()
            AlertType.VIBRATION -> vibrateDevice()
            AlertType.BOTH -> {
                playAlertSound()
                vibrateDevice()
            }
            else -> {
                // No hay lógica, pero dejamos esta rama para evitar errores
            }
        }
    }


    //Reproducir una alerta
    private fun playAlertSound() {
        // Genera un tono de alerta corto
        toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 500)
    }

    //Activar la vibración del dispositivo.
    private fun vibrateDevice() {
        // Patrón de vibración: vibra 500ms, pausa 200ms, vibra 500ms
        val vibratePattern = longArrayOf(0, 500, 200, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Para Android 8.0 (Oreo) y superior
            val vibrationEffect = VibrationEffect.createWaveform(vibratePattern, -1)
            vibrator.vibrate(vibrationEffect)
        } else {
            // Para versiones anteriores de Android
            @Suppress("DEPRECATION")
            vibrator.vibrate(vibratePattern, -1)
        }
    }

    //Mostrar una notificacion del nivel de luz bajo
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showLowLightNotification(){
        // Código para mostrar una notificación de nivel de luz bajo
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("low_light_channel","Nivel de Luz Bajo",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(context,"low_light_channel")
            .setSmallIcon(R.drawable.ic_light)
            .setContentTitle("Nivel de Luz Bajo")
            .setContentText("El nivel de luz actual es bajo.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(1,builder.build())
    }

    //Mostrar una notificacion del nivel de luz alto
    @RequiresApi(Build.VERSION_CODES.O)
    fun showHighLightNotification () {
        // Código para mostrar una notificación de nivel de luz alto
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("high_light_channel","Nivel de Luz Alto",NotificationManager.IMPORTANCE_HIGH)
        val builder = NotificationCompat.Builder(context, "high_light_channel")
            .setSmallIcon(R.drawable.ic_light)
            .setContentTitle("Nivel de Luz Alto")
            .setContentText("El nivel de luz actual es alto.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(2,builder.build())
    }

    // Método para detener alertas si es necesario
    fun stopAlerts() {
        toneGenerator.stopTone()
        vibrator.cancel()
    }
}