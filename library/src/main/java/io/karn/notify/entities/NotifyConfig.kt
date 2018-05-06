package io.karn.notify.entities

import android.annotation.TargetApi
import android.app.NotificationManager
import android.os.Build
import android.support.v4.app.NotificationManagerCompat
import io.karn.notify.Notify

/**
 * Provider of the initial configuration of the Notify > Creator Fluent API.
 */
data class NotifyConfig(
        /**
         * The default CHANNEL_ID for a notification on Android O.
         */
        @TargetApi(Build.VERSION_CODES.O) val defaultChannelKey: String = Notify.DEFAULT_CHANNEL_KEY,
        /**
         * The default CHANNEL_NAME for a notification on Android O.
         */
        @TargetApi(Build.VERSION_CODES.O) val defaultChannelName: String = Notify.DEFAULT_CHANNEL_NAME,
        /**
         * The default CHANNEL_DESCRIPTION for a notification on Android O.
         */
        @TargetApi(Build.VERSION_CODES.O) val defaultChannelDescription: String = Notify.DEFAULT_CHANNEL_DESCRIPTION,
        /**
         * Specifies the default configuration of a notification (e.g the default notificationIcon,
         * and notification color.)
         */
        @TargetApi(Build.VERSION_CODES.O) val header: Payload.Header = Payload.Header(channel = defaultChannelKey),
        /**
         * A reference to the notification manager.
         */
        internal var notificationManager: NotificationManager? = null
)
