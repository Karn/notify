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

package io.karn.notify

import android.annotation.TargetApi
import android.os.Build
import androidx.core.app.NotificationCompat
import io.karn.notify.entities.Payload
import io.karn.notify.internal.RawNotification
import io.karn.notify.internal.utils.Action
import io.karn.notify.internal.utils.Errors
import io.karn.notify.internal.utils.Experimental
import io.karn.notify.internal.utils.NotifyScopeMarker

/**
 * Fluent API for creating a Notification object.
 */
@NotifyScopeMarker
class NotifyCreator internal constructor(private val notify: Notify) {

    private var meta = Payload.Meta()
    private var alerts = Notify.defaultConfig.defaultAlerting
    private var header = Notify.defaultConfig.defaultHeader.copy()
    private var content: Payload.Content = Payload.Content.Default()
    private var actions: ArrayList<Action>? = null
    private var bubblize: Payload.Bubble? = null
    private var stackable: Payload.Stackable? = null
    private var progress: Payload.Progress = Payload.Progress()

    /**
     * Scoped function for modifying the Metadata of a notification, such as click intents,
     * notification category, and priority among other options.
     */
    fun meta(init: Payload.Meta.() -> Unit): NotifyCreator {
        this.meta.init()

        return this
    }

    /**
     * Scoped function for modifying the Alerting of a notification. This includes visibility,
     * sounds, lights, etc.
     *
     * If an existing key is provided the existing channel is retrieved (API >= AndroidO) and set as the alerting
     * configuration. If the key is new, the channel is created and set as the alerting configuration.
     */
    fun alerting(key: String, init: Payload.Alerts.() -> Unit): NotifyCreator {
        // Clone object and assign the key.
        this.alerts = this.alerts.copy(channelKey = key).also(init)
        return this
    }

    /**
     * Scoped function for modifying the Header of a notification. Specifically, it allows the
     * modification of the notificationIcon, color, the headerText (optional text next to the
     * appName), and finally the notifyChannel of the notification if targeting Android O.
     */
    fun header(init: Payload.Header.() -> Unit): NotifyCreator {
        this.header.init()
        return this
    }

    fun progress(init: Payload.Progress.() -> Unit): NotifyCreator {
        this.progress.init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'Default' notification.
     */
    fun content(init: Payload.Content.Default.() -> Unit): NotifyCreator {
        this.content = Payload.Content.Default().also(init)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'TextList' notification.
     */
    fun asTextList(init: Payload.Content.TextList.() -> Unit): NotifyCreator {
        this.content = Payload.Content.TextList().also(init)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigText' notification.
     */
    fun asBigText(init: Payload.Content.BigText.() -> Unit): NotifyCreator {
        this.content = Payload.Content.BigText().also(init)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigPicture' notification.
     */
    fun asBigPicture(init: Payload.Content.BigPicture.() -> Unit): NotifyCreator {
        this.content = Payload.Content.BigPicture().also(init)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'Message' notification.
     */
    fun asMessage(init: Payload.Content.Message.() -> Unit): NotifyCreator {
        this.content = Payload.Content.Message().also(init)
        return this
    }

    /**
     * Scoped function for modifying the 'Actions' of a notification. The transformation
     * relies on adding standard notification Action objects.
     */
    fun actions(init: ArrayList<Action>.() -> Unit): NotifyCreator {
        this.actions = ArrayList<Action>().also(init)
        return this
    }

    /**
     * Scoped function for modifying the behaviour of 'Bubble' notifications. The transformation
     * relies on the 'bubbleIcon' and 'targetActivity' values which are used to create the Bubble.
     *
     * Note that Bubbles have very specific restrictions in terms of when they can be shown to the
     * user. In particular, at least one of the following conditions must be met before the
     * notification is shown.
     * - The notification uses MessagingStyle, and has a Person added.
     * - The notification is from a call to Service.startForeground, has a category of
     *   CATEGORY_CALL, and has a Person added.
     * - The app is in the foreground when the notification is sent.
     *
     * In addition, the 'Bubbles' flag has to be enabled from the Android Developer Options in the
     * Settings of the Device for the notifications to be shown as Bubbles.
     *
     * Finally, the 'targetActivity' should also have the following attributes to correctly show a
     * Bubble notification.
     *
     *      android:documentLaunchMode="always"
     *      android:resizeableActivity="true"
     *      android:screenOrientation="portrait"
     *
     */
    @Experimental
    @TargetApi(Build.VERSION_CODES.Q)
    fun bubblize(init: Payload.Bubble.() -> Unit): NotifyCreator {
        this.bubblize = Payload.Bubble().also(init)

        this.bubblize!!
                .takeUnless { it.bubbleIcon == null }
                ?: throw IllegalArgumentException(Errors.INVALID_BUBBLE_ICON_ERROR)

        this.bubblize!!
                .takeUnless { it.targetActivity == null }
                ?: throw IllegalArgumentException(Errors.INVALID_BUBBLE_TARGET_ACTIVITY_ERROR)

        return this
    }

    /**
     * Scoped function for modifying the behaviour of 'Stacked' notifications. The transformation
     * relies on the 'summaryText' of a stackable notification.
     */
    fun stackable(init: Payload.Stackable.() -> Unit): NotifyCreator {
        this.stackable = Payload.Stackable().also(init)

        this.stackable!!
                .takeUnless { it.key.isNullOrEmpty() }
                ?: throw IllegalArgumentException(Errors.INVALID_STACK_KEY_ERROR)

        return this
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API
     * transformations (if any) from the {@see NotifyCreator} builder object.
     */
    fun asBuilder(): NotificationCompat.Builder {
        return notify.asBuilder(RawNotification(meta, alerts, header, content, bubblize, stackable, actions, progress))
    }

    /**
     * Delegate a {@see Notification.Builder} object to the NotificationInterop class which builds
     * and displays the notification.
     *
     * This is a terminal operation.
     *
     * @param id    An optional integer which will be used as the ID for the notification that is
     *              shown. This argument is ignored if the notification is a NotifyCreator#stackable
     *              receiver is set.
     * @return An integer corresponding to the ID of the system notification. Any updates should use
     * this returned integer to make updates or to cancel the notification.
     */
    @Deprecated(message = "Removed optional argument to alleviate confusion on ID that is used to create notification",
            replaceWith = ReplaceWith(
                    "Notify.show()",
                    "io.karn.notify.Notify"))
    fun show(id: Int?): Int {
        return notify.show(id, asBuilder())
    }

    /**
     * Delegate a @see{ Notification.Builder} object to the NotificationInterop class which builds
     * and displays the notification.
     *
     * This is a terminal operation.
     *
     * @return An integer corresponding to the ID of the system notification. Any updates should use
     * this returned integer to make updates or to cancel the notification.
     */
    fun show(): Int {
        return notify.show(null, asBuilder())
    }

    /**
     * Cancel an existing notification given an ID.
     *
     * @deprecated Choose to instead use the static function {@see Notify#cancelNotification()}
     * which provides the correct encapsulation of the this `cancel` function.
     */
    @Deprecated(message = "Exposes function under the incorrect API -- NotifyCreator is reserved strictly for notification construction.",
            replaceWith = ReplaceWith(
                    "Notify.cancelNotification(context, id)",
                    "android.content.Context", "io.karn.notify.Notify"))
    @Throws(NullPointerException::class)
    fun cancel(id: Int) {
        // This should be safe to call from here because the Notify.with(context) function call
        // would have initialized the NotificationManager object. In any case, the function has been
        // annotated as one which can throw a NullPointerException.
        return Notify.cancelNotification(id)
    }
}
