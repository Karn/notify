package io.karn.notify

import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.RawNotification

/**
 * Simplified Notification delivery for Android.
 */
class Notify internal constructor(internal var context: Context) {

    companion object {
        /**
         * The default CHANNEL_ID for a notification on Android O.
         */
        const val DEFAULT_CHANNEL_KEY = "application_notification"
        /**
         * The default CHANNEL_NAME for a notification on Android O.
         */
        const val DEFAULT_CHANNEL_NAME = "Application notifications."
        /**
         * The default CHANNEL_DESCRIPTION for a notification on Android O.
         */
        const val DEFAULT_CHANNEL_DESCRIPTION = "General application notifications."

        /**
         * The flag to disable notification lights.
         */
        const val NO_LIGHTS = -1

        // This is the initial configuration of the Notify Creator.
        internal var defaultConfig = NotifyConfig()

        /**
         * Modify the default configuration.
         *
         * Takes a receiver with the NotifyConfig immutable object which has mutable fields.
         */
        fun defaultConfig(init: NotifyConfig.() -> Unit) {
            defaultConfig.init()
        }

        /**
         * A new {@see Notify} and {@see Creator} instance.
         *
         * This object is automatically initialized with the singleton default configuration which
         * can be modified using {@see Notify#defaultConfig((NotifyConfig) -> Unit)}.
         */
        fun with(context: Context): Creator {
            return Creator(Notify(context), defaultConfig)
        }
    }

    init {
        this.context = context.applicationContext

        // Initialize notification manager instance.
        if (defaultConfig.notificationManager == null) {
            defaultConfig.notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        NotificationChannelInterop.with(defaultConfig.alerting)
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API
     * transformations (if any) from the {@see Creator} builder object.
     */
    internal fun asBuilder(payload: RawNotification): NotificationCompat.Builder {
        return NotificationInterop.buildNotification(this, payload)
    }

    /**
     * Delegate a {@see Notification.Builder} object to the Notify NotificationInterop class which
     * builds and displays the notification.
     *
     * This is a terminal operation.
     *
     * @return An integer corresponding to the ID of the system notification. Any updates should use
     * this returned integer to make updates or to cancel the notification.
     */
    internal fun show(builder: NotificationCompat.Builder): Int {
        return NotificationInterop.showNotification(Notify.defaultConfig.notificationManager!!, builder)
    }

    /**
     * Cancel an existing notification with a particular id.
     */
    internal fun cancel(id: Int) {
        return NotificationInterop.cancelNotification(Notify.defaultConfig.notificationManager!!, id)
    }
}
