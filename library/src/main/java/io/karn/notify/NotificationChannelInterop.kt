package io.karn.notify

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.os.Build
import io.karn.notify.entities.Payload

/**
 * Provides compatibility functionality for the Notification channels introduced in Android O.
 */
internal object NotificationChannelInterop {

    @SuppressLint("WrongConstant")
    fun with(alerting: Payload.Alerts): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }

        val notificationManager = Notify.defaultConfig.notificationManager!!

        // Ensure that the alerting is not already registered -- return true if it exists.
        val notificationChannel = notificationManager.getNotificationChannel(alerting.channelKey)
        if (notificationChannel != null) {
            // compare and rebuild if not the same.
            if (alerting.channelName == notificationChannel.name
                    && alerting.channelDescription == notificationChannel.description
                    && alerting.channelImportance == notificationChannel.importance
                    && alerting.lightColor == notificationChannel.lightColor
                    && alerting.vibrationPattern?.equals(notificationChannel.vibrationPattern) == true
                    && alerting.sound == notificationChannel.sound) {
                return true
            } else {
                notificationManager.deleteNotificationChannel(alerting.channelKey)
            }
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

            alerting.vibrationPattern?.takeIf { it.isNotEmpty() }?.let {
                enableVibration(true)
                vibrationPattern = it.toLongArray()
            }

            alerting.sound?.let {
                setSound(it, android.media.AudioAttributes.Builder().build())
            }

            Unit
        }

        // Register the alerting with the system
        notificationManager.createNotificationChannel(channel)

        return true
    }
}
