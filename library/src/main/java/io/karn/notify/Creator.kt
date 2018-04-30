package io.karn.notify

import android.support.v4.app.NotificationCompat
import io.karn.notify.entities.Action
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.Payload
import io.karn.notify.entities.RawNotification

/**
 * Fluent API for creating a Notification object.
 */
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
    fun meta(meta: Payload.Meta.() -> Unit): Creator {
        meta(this.meta)

        return this
    }

    /**
     * Scoped function for modifying the Header of a notification. Specifically, it allows the
     * modification of the notificationIcon, color, the headerText (optional text next to the
     * appName), and finally the channel of the notification if targeting Android O.
     */
    fun header(header: Payload.Header.() -> Unit): Creator {
        header(this.header)

        return this
    }

    /**
     * Scoped function for modifying the content of a 'Default' notification.
     */
    fun content(block: Payload.Content.Default.() -> Unit): Creator {
        this.content = Payload.Content.Default()
        block(this.content as Payload.Content.Default)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'TextList' notification.
     */
    fun asTextList(block: Payload.Content.TextList.() -> Unit): Creator {
        this.content = Payload.Content.TextList()
        block(this.content as Payload.Content.TextList)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigText' notification.
     */
    fun asBigText(block: Payload.Content.BigText.() -> Unit): Creator {
        this.content = Payload.Content.BigText()
        block(this.content as Payload.Content.BigText)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'BigPicture' notification.
     */
    fun asBigPicture(block: Payload.Content.BigPicture.() -> Unit): Creator {
        this.content = Payload.Content.BigPicture()
        block(this.content as Payload.Content.BigPicture)
        return this
    }

    /**
     * Scoped function for modifying the content of a 'Message' notification.
     */
    fun asMessage(block: Payload.Content.Message.() -> Unit): Creator {
        this.content = Payload.Content.Message()
        block(this.content as Payload.Content.Message)
        return this
    }

    /**
     * Scoped function for modifying the 'Actions' of a notification. The transformation
     * relies on adding standard notification Action objects.
     */
    fun actions(block: ArrayList<Action>.() -> Unit): Creator {
        this.actions = ArrayList()
        block(this.actions as ArrayList<Action>)
        return this
    }

    /**
     * Scoped function for modifying the behaviour of 'Stacked' notifications. The transformation
     * relies on the 'summaryText' of a stackable notification.
     */
    fun stackable(block: Payload.Stackable.() -> Unit): Creator {
        this.stackable = Payload.Stackable()
        block(this.stackable as Payload.Stackable)
        return this
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API
     * transformations (if any) from the {@see Creator} builder object.
     */
    fun getBuilder(): NotificationCompat.Builder {
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
    fun send(): Int {
        return notify.send(getBuilder())
    }
}
