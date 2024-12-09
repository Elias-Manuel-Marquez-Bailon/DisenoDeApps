package com.example.myapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.model.AlertType
import com.example.myapplication.model.UserSettings

class NotificationHelper (private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val channelId = "light_detector_channel"
    private val channelName = "Light Detector Notifications"
    private val channelDescription = "Notifications related to light level detection"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ). apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Metodo de una notificacion de nivel de luz bajo
    fun createLowLightNotification (userSettings: UserSettings) {
        val notificationBuilder = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.ic_light)
            .setContentTitle("Low Light Level Detected")
            .setContentText("Light level is below ${userSettings.lowLightThreshold} lux")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (userSettings.alertType == AlertType.SOUND ||
            userSettings.alertType == AlertType.BOTH) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        if (userSettings.alertType == AlertType.VIBRATION ||
            userSettings.alertType == AlertType.BOTH) {
            notificationBuilder.setVibrate(longArrayOf(0,1000,500,1000))
        }
        notificationManager.notify(1,notificationBuilder.build())
    }

    //Metodo de una notificacion de nivel de luz alto
    fun createHighLightNotification( userSettings: UserSettings) {
        val notificationBuilder = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.ic_light)
            .setContentTitle("High Light Level Detected")
            .setContentText("Light level is above ${userSettings.highLightThreshold} lux")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (userSettings.alertType == AlertType.SOUND ||
            userSettings.alertType == AlertType.BOTH) {
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        if (userSettings.alertType == AlertType.VIBRATION ||
            userSettings.alertType == AlertType.BOTH) {

            notificationBuilder.setVibrate(longArrayOf(0, 1000, 500, 1000))
        }
        notificationManager.notify(2,notificationBuilder.build())
    }

}