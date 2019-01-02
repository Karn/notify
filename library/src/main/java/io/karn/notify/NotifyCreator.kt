package io.karn.notify

import androidx.core.app.NotificationCompat
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.Payload
import io.karn.notify.internal.RawNotification
import io.karn.notify.internal.utils.Action
import io.karn.notify.internal.utils.Errors
import io.karn.notify.internal.utils.NotifyScopeMarker

/**
 * Fluent API for creating a Notification object.
 */
@NotifyScopeMarker
class NotifyCreator internal constructor(private val notify: Notify, config: NotifyConfig = NotifyConfig()) {

    private var meta = Payload.Meta()
    private var alerts = config.defaultAlerting
    private var header = config.defaultHeader.copy()
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
     * @return An integer corresponding to the ID of the system notification. Any updates should use
     * this returned integer to make updates or to cancel the notification.
     */
    fun show(): Int {
        return notify.show(asBuilder())
    }

    /**
     * Cancel an existing notification given an ID.
     *
     * @deprecated Choose to instead use the static function {@see Notify#cancelNotification()} which provides the correct
     * encapsulation of the this `cancel` function.
     */
    @Deprecated(message = "Exposes function under the incorrect API -- NotifyCreator is reserved strictly for notification construction.",
            replaceWith = ReplaceWith("Notify.cancelNotification(id)", "io.karn.notify.Notify"))
    fun cancel(id: Int) {
        return Notify.cancelNotification(id)
    }
}
