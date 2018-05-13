package io.karn.notify.entities

import android.app.NotificationManager
import android.os.Build
import io.karn.notify.NotificationChannelInterop

/**
 * Provider of the initial configuration of the Notify > Creator Fluent API.
 */
data class NotifyConfig(
        /**
         * A reference to the notification manager.
         */
        internal var notificationManager: NotificationManager? = null,
        /**
         * Specifies the default configuration of a notification (e.g the default notificationIcon,
         * and notification color.)
         */
        internal var defaultHeader: Payload.Header = Payload.Header(),
        /**
         * Specifies the default alerting configuration for notifications.
         */
        internal var defaultAlerting: Payload.Alerts = Payload.Alerts()
) {
    fun header(init: Payload.Header.() -> Unit): NotifyConfig {
        defaultHeader.init()
        return this
    }

    fun alerting(key: String, init: Payload.Alerts.() -> Unit): NotifyConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelInterop.getNotificationChannels()
                    ?.filter {
                        it.id == key
                    }?.takeIf { it.isNotEmpty() }?.let {
                        throw IllegalStateException(NotificationChannelInterop.ERROR_DUPLCIATE_KEY)
                    }
        }

        // Clone object and assign the key.
        this.defaultAlerting = this.defaultAlerting.copy(channelKey = key)

        defaultAlerting.init()

        return this
    }
}
