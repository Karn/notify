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

import androidx.core.app.NotificationCompat
import io.karn.notify.entities.Payload
import io.karn.notify.internal.RawNotification
import io.karn.notify.internal.utils.Action
import io.karn.notify.internal.utils.Errors
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
    private var stackable: Payload.Stackable? = null

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
        this.alerts = this.alerts.copy(channelKey = key)
        this.alerts.init()
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

    /**
     * Scoped function for modifying the content of a 'Default' notification.
     */
    fun content(init: Payload.Content.Default.() -> Unit): NotifyCreator {
        this.content = Payload.Content.Default()
        (this.content as Payload.Content.Default).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'TextList' notification.
     */
    fun asTextList(init: Payload.Content.TextList.() -> Unit): NotifyCreator {
        this.content = Payload.Content.TextList()
        (this.content as Payload.Content.TextList).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigText' notification.
     */
    fun asBigText(init: Payload.Content.BigText.() -> Unit): NotifyCreator {
        this.content = Payload.Content.BigText()
        (this.content as Payload.Content.BigText).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigPicture' notification.
     */
    fun asBigPicture(init: Payload.Content.BigPicture.() -> Unit): NotifyCreator {
        this.content = Payload.Content.BigPicture()
        (this.content as Payload.Content.BigPicture).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'Message' notification.
     */
    fun asMessage(init: Payload.Content.Message.() -> Unit): NotifyCreator {
        this.content = Payload.Content.Message()
        (this.content as Payload.Content.Message).init()
        return this
    }

    /**
     * Scoped function for modifying the 'Actions' of a notification. The transformation
     * relies on adding standard notification Action objects.
     */
    fun actions(init: ArrayList<Action>.() -> Unit): NotifyCreator {
        this.actions = ArrayList()
        (this.actions as ArrayList<Action>).init()
        return this
    }

    /**
     * Scoped function for modifying the behaviour of 'Stacked' notifications. The transformation
     * relies on the 'summaryText' of a stackable notification.
     */
    fun stackable(init: Payload.Stackable.() -> Unit): NotifyCreator {
        this.stackable = Payload.Stackable()
        (this.stackable as Payload.Stackable).init()

        this.stackable
                ?.takeIf { it.key.isNullOrEmpty() }
                ?.apply {
                    throw IllegalArgumentException(Errors.INVALID_STACK_KEY_ERROR)
                }

        return this
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API
     * transformations (if any) from the {@see NotifyCreator} builder object.
     */
    fun asBuilder(): NotificationCompat.Builder {
        return notify.asBuilder(RawNotification(meta, alerts, header, content, stackable, actions))
    }

    /**
     * Delegate a {@see Notification.Builder} object to the Notify NotificationInterop class which
     * builds and displays the notification.
     *
     * This is a terminal operation.
     *
     * @param id    An optional integer which will be used as the ID for the notification that is
     *              shown. This argument is ignored if the notification is a NotifyCreator#stackable
     *              receiver is set.
     * @return An integer corresponding to the ID of the system notification. Any updates should use
     * this returned integer to make updates or to cancel the notification.
     */
    fun show(id: Int? = null): Int {
        return notify.show(id, asBuilder())
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
