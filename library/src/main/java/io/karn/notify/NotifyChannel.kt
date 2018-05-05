package io.karn.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Provides compatibility functionality for the Notification channels introduced in Android O.
 */
internal object NotifyChannel {

    fun registerChannel(context: Context, channelKey: String, channelName: String, channelDescription: String, importance: Int = NotificationManager.IMPORTANCE_DEFAULT) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val channel = NotificationChannel(channelKey, channelName, importance)

        channel.description = channelDescription
        // Register the channel with the system
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
