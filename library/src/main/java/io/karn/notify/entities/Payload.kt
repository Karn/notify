package io.karn.notify.entities

import android.annotation.TargetApi
import android.app.PendingIntent
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.NotificationCompat
import io.karn.notify.R
import io.karn.notify.utils.Action
import java.util.*

/**
 * Wrapper class to provide configurable options for a NotifcationCompact object.
 */
sealed class Payload {

    /**
     * The Metadata contains configuration that is considered to be such that it controls the
     * overall non-layout behaviour of the notification.
     */
    data class Meta(
            /**
             * The handler for a notification click.
             */
            var clickIntent: PendingIntent? = null,
            /**
             * The handler for a dismissal of a notification ('clear all' or swipe away).
             */
            var clearIntent: PendingIntent? = null,
            /**
             * Specifies the behaviour of the notification once it has been clicked. If set to
             * false, the notification is not dismissed once it has been clicked.
             */
            var cancelOnClick: Boolean = true,
            /**
             * The category of the notification which allows android to prioritize the
             * notification as required.
             */
            var category: String? = null,
            /**
             * Manual specification of the priority of the notification.
             */
            var priority: Int = NotificationCompat.PRIORITY_DEFAULT,
            /**
             * Set whether or not this notification is only relevant to the current device.
             */
            var localOnly: Boolean = false,
            /**
             * Indicates whether the notification is sticky. If enabled, the notification is not
             * affected by the clear all and is not dismissible.
             */
            var sticky: Boolean = false
    )

    /**
     * Defines the alerting configuration for a particular notification. This includes notification
     * visibility, sounds, vibrations, etc.
     *
     * This configuration system may not work as expected on all devices. Refer to the Wiki for more
     * information.
     */
    data class Alerts(
            /**
             * The visibility of the notification as it appears on the lockscreen. By default it is
             * hidden.
             */
            @NotificationCompat.NotificationVisibility var lockScreenVisibility: Int = NotificationCompat.VISIBILITY_PRIVATE,
            /**
             * The duration of time in milliseconds after which the notification is automatically dismissed.
             */
            var timeout: Long = 0L
    )

    /**
     * Contains configuration that is specific to the header of a notification.
     */
    data class Header(
            /**
             * The icon that appears for the notification as a DrawableRes Integer.
             */
            @DrawableRes var icon: Int = R.drawable.ic_app_icon,
            /**
             * The color of the notification items -- icon, appName, and expand indicator.
             */
            @ColorRes var color: Int = R.color.notification_header_color,
            /**
             * The optional text that appears next to the appName of a notification.
             */
            var headerText: CharSequence? = null,
            /**
             * Manual override of channel on which this notification is broadcasted.
             */
            @TargetApi(Build.VERSION_CODES.O) var channel: String = "",
            /**
             * Setting this field to false results in the timestamp (now, 5m, ...) next to the
             * application name to be hidden.
             */
            var showTimestamp: Boolean = true
    )

    /**
     * Deterministic property assignment for a notification type.
     */
    sealed class Content {

        /**
         * All 'standard' notifications specify a title and a text field.
         */
        interface Standard {
            /**
             * The first line of a standard notification.
             */
            var title: CharSequence?
            /**
             * The second line of the notification.
             */
            var text: CharSequence?
        }

        /**
         * Indicates whether a notification is expandable.
         */
        interface Expandable {
            /**
             * The content that is displayed when the notification is expanded expanded.
             */
            var expandedText: CharSequence?
        }

        /**
         * The object representation of a 'Default' notification.
         */
        data class Default(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null
        ) : Content(), Standard

        /**
         * The object representation of a 'TextList' notification.
         */
        data class TextList(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                /**
                 * The lines of the notification.
                 */
                var lines: List<CharSequence> = ArrayList()
        ) : Content(), Standard

        /**
         * The object representation of a 'BigText' notification.
         */
        data class BigText(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var expandedText: CharSequence? = null,
                /**
                 * The large text associated with the notification.
                 */
                var bigText: CharSequence? = null
        ) : Content(), Standard, Expandable

        /**
         * The object representation of a 'BigPicture' notification.
         */
        data class BigPicture(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var expandedText: CharSequence? = null,
                /**
                 * The large image that appears when the notification is expanded.s
                 */
                var image: Bitmap? = null
        ) : Content(), Standard, Expandable

        /**
         * The object representaiton of a 'Message' notification.
         */
        data class Message(
                /**
                 * The title of the conversation.
                 */
                var conversationTitle: CharSequence? = null,
                /**
                 * The display name of the device user.
                 */
                var userDisplayName: CharSequence = "",
                /**
                 * A collection of messages associated with a particualar conversation.
                 */
                var messages: List<NotificationCompat.MessagingStyle.Message> = ArrayList()
        ) : Content()
    }

    /**
     * Contains configuration specific to the manual stacking behaviour of a notification.
     * Manual stacking occurs for all notifications with the same key, additionally the summary
     * configuration is taken from the latest notification with the specified stack key.
     */
    data class Stackable(
            /**
             * The key which defines the stack as well as the corresponding notification ID.
             */
            var key: String? = null,
            /**
             * The click intent of the stacked notification.
             */
            var clickIntent: PendingIntent? = null,
            /**
             * The summary content of this particular notification. How it appears in the list of
             * notifications in the stack.
             */
            var summaryContent: CharSequence? = null,
            /**
             * The title of the stacked notification.
             *
             * Takes a function that receives a lambda with the total count of existing
             * notifications with the same stack key.
             */
            var summaryTitle: ((count: Int) -> String)? = null,
            /**
             * The second line of the collapsed notification which is meant to show a summary of the
             * stack.
             *
             * Takes a function that receives a lambda with the total count of existing
             * notifications with the same stack key.
             */
            var summaryDescription: ((count: Int) -> String)? = null,
            /**
             * The actions associated with the stackable notification when it is stacked. These
             * actions override the actions for the individual notification.
             */
            internal var stackableActions: ArrayList<Action>? = null
    ) {

        /**
         * Scoped function for modifying the behaviour of the actions associated with the 'Stacked'
         * notification.
         */
        fun actions(init: ArrayList<Action>.() -> Unit) {
            this.stackableActions = ArrayList()
            this.stackableActions?.init()
        }
    }
}
