package io.karn.notify

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
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

        // This is the initial configuration of the Notify Creator.
        private var defaultConfig = NotifyConfig()

        /**
         * Modify the default configuration.
         *
         * Takes a receiver with the NotifyConfig immutable object which has mutable fields.
         */
        fun defaultConfig(block: (NotifyConfig) -> Unit) {
            block(defaultConfig)
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

        /**
         * Cancel an existing notification.
         */
        fun cancel(context: Context, id: Int) {
            return NotificationInterop.cancelNotification(context, id)
        }
    }

    init {
        this.context = context.applicationContext

        NotifyChannel.registerChannel(
                this.context,
                defaultConfig.defaultChannelKey,
                defaultConfig.defaultChannelName,
                defaultConfig.defaultChannelDescription)
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
        return NotificationInterop.showNotification(context, builder)
    }
}
