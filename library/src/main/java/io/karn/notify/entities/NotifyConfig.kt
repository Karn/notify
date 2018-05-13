package io.karn.notify.entities

import android.app.NotificationManager

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

    fun alerting(init: Payload.Alerts.() -> Unit): NotifyConfig {
        defaultAlerting.init()

        return this
    }
}
