package io.karn.notify

import android.support.v4.app.NotificationCompat
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.Payload
import io.karn.notify.entities.RawNotification
import io.karn.notify.utils.Action
import io.karn.notify.utils.NotifyScopeMarker

/**
 * Fluent API for creating a Notification object.
 */
@NotifyScopeMarker
class Creator internal constructor(private val notify: Notify, config: NotifyConfig = NotifyConfig()) {

    private var meta = Payload.Meta()
    private var header = config.header
    private var content: Payload.Content = Payload.Content.Default()
    private var actions: ArrayList<Action>? = null
    private var stackable: Payload.Stackable? = null

    /**
     * Scoped function for modifying the Metadata of a notification, such as click intents,
     * notification category, and priority among other options.
     */
    fun meta(init: Payload.Meta.() -> Unit): Creator {
        this.meta.init()

        return this
    }

    /**
     * Scoped function for modifying the Header of a notification. Specifically, it allows the
     * modification of the notificationIcon, color, the headerText (optional text next to the
     * appName), and finally the channel of the notification if targeting Android O.
     */
    fun header(init: Payload.Header.() -> Unit): Creator {
        this.header.init()

        return this
    }

    /**
     * Scoped function for modifying the content of a 'Default' notification.
     */
    fun content(init: Payload.Content.Default.() -> Unit): Creator {
        this.content = Payload.Content.Default()
        (this.content as Payload.Content.Default).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'TextList' notification.
     */
    fun asTextList(init: Payload.Content.TextList.() -> Unit): Creator {
        this.content = Payload.Content.TextList()
        (this.content as Payload.Content.TextList).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigText' notification.
     */
    fun asBigText(init: Payload.Content.BigText.() -> Unit): Creator {
        this.content = Payload.Content.BigText()
        (this.content as Payload.Content.BigText).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigPicture' notification.
     */
    fun asBigPicture(init: Payload.Content.BigPicture.() -> Unit): Creator {
        this.content = Payload.Content.BigPicture()
        (this.content as Payload.Content.BigPicture).init()
        return this
    }

    /**
     * Scoped function for modifying the content of a 'Message' notification.
     */
    fun asMessage(init: Payload.Content.Message.() -> Unit): Creator {
        this.content = Payload.Content.Message()
        (this.content as Payload.Content.Message).init()
        return this
    }

    /**
     * Scoped function for modifying the 'Actions' of a notification. The transformation
     * relies on adding standard notification Action objects.
     */
    fun actions(init: ArrayList<Action>.() -> Unit): Creator {
        this.actions = ArrayList()
        (this.actions as ArrayList<Action>).init()
        return this
    }

    /**
     * Scoped function for modifying the behaviour of 'Stacked' notifications. The transformation
     * relies on the 'summaryText' of a stackable notification.
     */
    fun stackable(init: Payload.Stackable.() -> Unit): Creator {
        this.stackable = Payload.Stackable()
        (this.stackable as Payload.Stackable).init()
        return this
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API
     * transformations (if any) from the {@see Creator} builder object.
     */
    fun asBuilder(): NotificationCompat.Builder {
        return notify.asBuilder(RawNotification(meta, header, content, stackable, actions))
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
}
