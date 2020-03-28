/*
 * MIT License
 *
 * Copyright (c) 2018 Karn Saheb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.karn.notify.entities

import android.app.PendingIntent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import io.karn.notify.Notify
import io.karn.notify.R
import io.karn.notify.internal.utils.Action
import io.karn.notify.internal.utils.NotifyImportance

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
             * Set whether or not this notification is only relevant to the current device.
             */
            var localOnly: Boolean = false,
            /**
             * Indicates whether the notification is sticky. If enabled, the notification is not
             * affected by the clear all and is not dismissible.
             */
            var sticky: Boolean = false,
            /**
             * The duration of time in milliseconds after which the notification is automatically dismissed.
             */
            var timeout: Long = 0L,
            /**
             * Add a person that is relevant to this notification.
             *
             * Depending on user preferences, this may allow the notification to pass through interruption filters, and
             * to appear more prominently in the user interface.
             *
             * The person should be specified by the {@code String} representation of a
             * {@link android.provider.ContactsContract.Contacts#CONTENT_LOOKUP_URI}.
             *
             * The system will also attempt to resolve {@code mailto:} and {@code tel:} schema
             * URIs.  The path part of these URIs must exist in the contacts database, in the
             * appropriate column, or the reference will be discarded as invalid. Telephone schema
             * URIs will be resolved by {@link android.provider.ContactsContract.PhoneLookup}.
             */
            internal val contacts: ArrayList<String> = ArrayList()
    ) {
        fun people(init: ArrayList<String>.() -> Unit) {
            contacts.init()
        }
    }

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
             * The default CHANNEL_ID for a notification on versions >= Android O.
             */
            val channelKey: String = Notify.CHANNEL_DEFAULT_KEY,
            /**
             * The default CHANNEL_NAME for a notification on versions >= Android O.
             */
            var channelName: String = Notify.CHANNEL_DEFAULT_NAME,
            /**
             * The default CHANNEL_DESCRIPTION for a notification on versions >= Android O.
             */
            var channelDescription: String = Notify.CHANNEL_DEFAULT_DESCRIPTION,
            /**
             * The default IMPORTANCE for a notification.
             */
            @NotifyImportance var channelImportance: Int = Notify.IMPORTANCE_NORMAL,
            /**
             * The LED colors of the notification notifyChannel.
             */
            @ColorInt var lightColor: Int = Notify.NO_LIGHTS,
            /**
             * Vibration pattern for notification on this notifyChannel. This is only set on
             * notifications with importance that is at least [Notify.IMPORTANCE_NORMAL] or higher.
             */
            var vibrationPattern: List<Long> = ArrayList(),
            /**
             * A custom notification sound if any. This is only set on notifications with importance
             * that is at least [Notify.IMPORTANCE_NORMAL] or higher.
             */
            var sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
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
            @ColorInt var color: Int = 0x4A90E2,
            /**
             * The optional text that appears next to the appName of a notification.
             */
            var headerText: CharSequence? = null,
            /**
             * Setting this field to false results in the timestamp (now, 5m, ...) next to the
             * application name to be hidden.
             */
            var showTimestamp: Boolean = true
    )

    /**
     * Contains configuration that is specific to the progress of a notification, inder
     */
    class Progress constructor(

            /**
             * The default false for a indeterminate horizontal progress in notification.
             * If this is true the notification show horizontal progress with exact value
             */
            var enablePercentage: Boolean = false,

            /*
            * The value of progress percent
            * */
            var progressPercent: Int = 0,

            /**
             * The default false for simple notiffication
             * If this is true the notification show progress
             */
            var showProgress: Boolean = false

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

        interface SupportsLargeIcon {
            /**
             * The large icon of the notification.
             */
            var largeIcon: Bitmap?
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
                override var text: CharSequence? = null,
                override var largeIcon: Bitmap? = null
        ) : Content(), Standard, SupportsLargeIcon

        /**
         * The object representation of a 'TextList' notification.
         */
        data class TextList(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var largeIcon: Bitmap? = null,
                /**
                 * The lines of the notification.
                 */
                var lines: List<CharSequence> = ArrayList()
        ) : Content(), Standard, SupportsLargeIcon

        /**
         * The object representation of a 'BigText' notification.
         */
        data class BigText(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var largeIcon: Bitmap? = null,
                override var expandedText: CharSequence? = null,
                /**
                 * The large text associated with the notification.
                 */
                var bigText: CharSequence? = null
        ) : Content(), Standard, SupportsLargeIcon, Expandable

        /**
         * The object representation of a 'BigPicture' notification.
         */
        data class BigPicture(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var largeIcon: Bitmap? = null,
                override var expandedText: CharSequence? = null,
                /**
                 * The large image that appears when the notification is expanded.s
                 */
                var image: Bitmap? = null
        ) : Content(), Standard, SupportsLargeIcon, Expandable

        /**
         * The object representaiton of a 'Message' notification.
         */
        data class Message(
                override var largeIcon: Bitmap? = null,
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
        ) : Content(), SupportsLargeIcon
    }

    /**
     * Contains configuration for Android Q Bubbles which are a native implementation of the
     * chatheads functionality pioneered by Facebook. The documentation around Bubbles describes
     * them as follows:
     * "Bubbles let users easily multi-task from anywhere on their device. They are designed to be
     * an alternative to using SYSTEM_ALERT_WINDOW."
     *
     * <a href="https://developer.android.com/guide/topics/ui/bubbles">Bubbles | Android Developers</a>
     *
     * Note that you can only have a total of five Bubbles being shown at any time.
     */
    data class Bubble(
            /**
             * A pending intent which contains a reference to the Activity that is being created
             * once the bubble has been created.
             */
            var targetActivity: PendingIntent? = null,
            /**
             * A pending intent which is to be fired when the Bubble is dismissed/closed.
             */
            var clearIntent: PendingIntent? = null,
            /**
             * A configuration which defines the height of the container which holds the Activity
             * that is being show.
             */
            var desiredHeight: Int = 600,
            /**
             * The icon which will be used by the bubble.
             */
            var bubbleIcon: IconCompat? = null,
            /**
             * Flag to auto-expand the Bubble to create and display the Activity defined by the
             * PendingIntent. This flag has no effect when the app is in the background.
             */
            var autoExpand: Boolean = false,
            /**
             * Flag to hide the initial notification in the notification shade which the
             * notification is shown from the foreground. This flag has no effect when the app is in
             * the background and the initial notification is shown regardless.
             */
            var suppressInitialNotification: Boolean = false
    )

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
