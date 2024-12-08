package com.example.myapplication.controller

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.myapplication.model.AlertType

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
            AlertType.SOUND -> playSound()
            AlertType.VIBRATION -> vibrateDevice()
            AlertType.BOTH -> {
                playSound()
                vibrateDevice()
            }
        }
    }

    private fun playSound() {
        // Genera un tono de alerta corto
        toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 500)
    }

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

    // Método para detener alertas si es necesario
    fun stopAlerts() {
        toneGenerator.stopTone()
        vibrator.cancel()
    }
}