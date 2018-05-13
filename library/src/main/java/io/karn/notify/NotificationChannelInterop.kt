package io.karn.notify

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.os.Build
import io.karn.notify.entities.Payload

/**
 * Provides compatibility functionality for the Notification channels introduced in Android O.
 */
internal object NotificationChannelInterop {

    const val ERROR_DUPLCIATE_KEY = "Attempting to override notification channel. Please specify a unique key."

    fun getNotificationChannels(): List<NotificationChannel>? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // return false
            return ArrayList()
        }

        val notificationManager = Notify.defaultConfig.notificationManager!!

        return notificationManager.notificationChannels.toList()
    }

    @SuppressLint("WrongConstant")
    fun with(alerting: Payload.Alerts): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }

        val notificationManager = Notify.defaultConfig.notificationManager!!

        // Ensure that the alerting is not already registered -- return true if it exists.
        notificationManager.getNotificationChannel(alerting.channelKey)?.run {
            return true
        }

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val channel = NotificationChannel(alerting.channelKey, alerting.channelName, alerting.channelImportance + 2).apply {
            description = alerting.channelDescription

            // Set the lockscreen visibility.
            lockscreenVisibility = alerting.lockScreenVisibility

            alerting.lightColor
                    .takeIf { it != Notify.NO_LIGHTS }
                    ?.let {
                        enableLights(true)
                        lightColor = alerting.lightColor
                    }

            alerting.vibrationPattern.takeIf { it.isNotEmpty() }?.also {
                enableVibration(true)
                vibrationPattern = it.toLongArray()
            }

            alerting.sound.also {
                setSound(it, android.media.AudioAttributes.Builder().build())
            }

            Unit
        }

        // Register the alerting with the system
        notificationManager.createNotificationChannel(channel)

        return true
    }
}
