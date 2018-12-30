package io.karn.notify

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.internal.RawNotification
import io.karn.notify.internal.NotificationChannelInterop
import io.karn.notify.internal.NotificationInterop

/**
 * Simplified Notification delivery for Android.
 */
class Notify internal constructor(internal var context: Context) {

    companion object {
        /**
         * The default CHANNEL_ID for a notification on Android O.
         */
        const val CHANNEL_DEFAULT_KEY = "application_notification"
        /**
         * The default CHANNEL_NAME for a notification on Android O.
         */
        const val CHANNEL_DEFAULT_NAME = "Application notifications."
        /**
         * The default CHANNEL_DESCRIPTION for a notification on Android O.
         */
        const val CHANNEL_DEFAULT_DESCRIPTION = "General application notifications."
        /**
         * Lowest priority for a notification. These notifications might not be shown to the user except under special
         * circumstances, such as detailed notification logs.
         */
        const val IMPORTANCE_MIN = NotificationCompat.PRIORITY_MIN
        /**
         * Lower priority for notifications that are deemed less important. The UI may choose to show these items
         * smaller, or at a different position in the list, compared to notifications with normal importance.
         */
        const val IMPORTANCE_LOW = NotificationCompat.PRIORITY_LOW
        /**
         * Default priority for notifications. If your application does not prioritize its own notifications, use this
         * value for all notifications.
         */
        const val IMPORTANCE_NORMAL = NotificationCompat.PRIORITY_DEFAULT
        /**
         * Higher priority for notifications, for more important notifications or alerts. The UI may choose to show
         * these items larger, or at a different position in notification lists, compared with your app's notifications
         * of normal importance.
         */
        const val IMPORTANCE_HIGH = NotificationCompat.PRIORITY_HIGH
        /**
         * Highest priority for notifications, use for notifications that require the user's prompt attention or input.
         */
        const val IMPORTANCE_MAX = NotificationCompat.PRIORITY_MAX

        /**
         * The flag to disable notification lights.
         */
        const val NO_LIGHTS = 0

        // This is the initial configuration of the Notify NotifyCreator.
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
         * A new {@see Notify} and {@see NotifyCreator} instance.
         *
         * This object is automatically initialized with the singleton default configuration which can be modified using
         * {@see Notify#defaultConfig((NotifyConfig) -> Unit)}.
         */
        fun with(context: Context): NotifyCreator {
            return NotifyCreator(Notify(context), defaultConfig)
        }
    }

    init {
        this.context = context.applicationContext

        // Initialize notification manager instance.
        if (defaultConfig.notificationManager == null) {
            defaultConfig.notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        NotificationChannelInterop.with(defaultConfig.defaultAlerting)
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API transformations (if any) from the
     * {@see NotifyCreator} builder object.
     */
    internal fun asBuilder(payload: RawNotification): NotificationCompat.Builder {
        return NotificationInterop.buildNotification(this, payload)
    }

    /**
     * Delegate a {@see Notification.Builder} object to the Notify NotificationInterop class which builds and displays
     * the notification.
     *
     * This is a terminal operation.
     *
     * @return An integer corresponding to the ID of the system notification. Any updates should use this returned
     * integer to make updates or to cancel the notification.
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
